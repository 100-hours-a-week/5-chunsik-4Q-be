package org.chunsik.pq.s3.service;

import org.chunsik.pq.s3.dto.S3UploadDTO;
import org.chunsik.pq.s3.model.Image;
import org.chunsik.pq.s3.repository.S3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;
    @Autowired
    private S3Repository s3Repository;

    // 파일 업로드
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(S3UploadDTO s3UploadDTO) {
        // 파일 이름에 UUID 추가
//        String uuid = UUID.randomUUID().toString();
//        String key = s3UploadDTO.getOriginalFileName().replace(".png", "") + "_" + uuid + ".jpg";
        String key = UUID.randomUUID().toString() + ".jpg"; // UUID로 대체하여 고유한 파일 이름을 생성
        String fullKey = "generate/" + key;

        // S3에 파일 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(s3UploadDTO.getFile()));

        String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, "ap-northeast-2", fullKey);

        // 데이터베이스에 URL 저장
        Image image = new Image();
        image.setUserId(s3UploadDTO.getUserId());
        image.setUrl(s3Url);
        image.setSize(s3UploadDTO.getFile().length());
        image.setCategoryId(s3UploadDTO.getCategoryId());
        image.setCreatedAt(LocalDateTime.now());

        // 태그가 없으면 null로 처리
        if (s3UploadDTO.getTags() != null) {
            image.setTag1(!s3UploadDTO.getTags().isEmpty() ? s3UploadDTO.getTags().get(0) : null);
            image.setTag2(s3UploadDTO.getTags().size() > 1 ? s3UploadDTO.getTags().get(1) : null);
            image.setTag3(s3UploadDTO.getTags().size() > 2 ? s3UploadDTO.getTags().get(2) : null);
        } else {
            image.setTag1(null);
            image.setTag2(null);
            image.setTag3(null);
        }

        s3Repository.save(image);

        // URL 반환
        return s3Url;
    }

    // 파일 다운로드
    public void downloadFile(String key, String downloadPath) {
        String fullKey = "generate/" + URLEncoder.encode(key, StandardCharsets.UTF_8);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .build();

        s3Client.getObject(getObjectRequest, Paths.get(downloadPath));

        System.out.println("File downloaded successfully.");
    }
}