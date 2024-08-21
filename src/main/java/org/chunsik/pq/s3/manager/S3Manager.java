package org.chunsik.pq.s3.manager;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.s3.dto.S3UploadDTO;
import org.chunsik.pq.s3.dto.S3UploadResponseDTO;
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


@RequiredArgsConstructor
@Component
public class S3Manager {
    private final S3Client s3Client;

    // 파일 업로드
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public S3UploadResponseDTO uploadFile(S3UploadDTO s3UploadDTO) {
        String filename = UUID.randomUUID() + ".jpg"; // UUID로 대체하여 고유한 파일 이름을 생성
        String fullFileName = "generate/" + filename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullFileName)
                .contentType("image/jpg")
                .build();

        String s3Url = makeS3Url(fullFileName);

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(s3UploadDTO.getFile()));

        return new S3UploadResponseDTO(fullFileName, s3Url);
    }

    private String makeS3Url(String fullFileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, "ap-northeast-2", fullFileName);
    }

    // 파일 다운로드
    // TODO : private 다운로드 기능에 쓰일 듯?
    public void downloadFile(String key, String downloadPath) {
        String fullKey = "generate/" + URLEncoder.encode(key, StandardCharsets.UTF_8);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .build();

        s3Client.getObject(getObjectRequest, Paths.get(downloadPath));
    }
}
