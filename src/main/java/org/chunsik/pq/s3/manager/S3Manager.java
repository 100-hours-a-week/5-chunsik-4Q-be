package org.chunsik.pq.s3.manager;

import org.chunsik.pq.generate.dto.GenerateResponseDTO;
import org.chunsik.pq.s3.dto.S3UploadDTO;
import org.chunsik.pq.s3.model.PhotoBackground;
import org.chunsik.pq.s3.repository.S3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.UUID;


@Component
public class S3Manager {
    @Autowired
    private S3Client s3Client;
    @Autowired
    private S3Repository s3Repository;

    // 파일 업로드
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public GenerateResponseDTO uploadFile(S3UploadDTO s3UploadDTO) {
        String key = UUID.randomUUID() + ".jpg"; // UUID로 대체하여 고유한 파일 이름을 생성
        String fullKey = "generate/" + key;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .contentType("image/jpg")
                .build();

        String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, "ap-northeast-2", fullKey);

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(s3UploadDTO.getFile()));

        // 데이터베이스에 URL 저장
        PhotoBackground photoBackground = PhotoBackground.builder()
                .userId(s3UploadDTO.getUserId())
                .url(s3Url)
                .categoryId(s3UploadDTO.getCategoryId())
                .size(s3UploadDTO.getSize())
                .firstTag(s3UploadDTO.getTags().get(0))
                .secondTag(s3UploadDTO.getTags().get(1))
                .thirdTag(s3UploadDTO.getTags().get(2))
                .build();

        s3Repository.save(photoBackground);

        return GenerateResponseDTO.builder()
                .url(fullKey)
                .build();
    }

    // 파일 다운로드
    public void downloadFile(String key, String downloadPath) {
        String fullKey = "generate/" + URLEncoder.encode(key, StandardCharsets.UTF_8);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .build();

        s3Client.getObject(getObjectRequest, Paths.get(downloadPath));
    }
}
