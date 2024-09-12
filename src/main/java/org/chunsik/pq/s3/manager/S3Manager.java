package org.chunsik.pq.s3.manager;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.s3.dto.S3UploadResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
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

    public S3UploadResponseDTO uploadFile(File file, String folder) throws IOException {
        String fullFileName = generateFileName(folder); // 이미지 생성 ai에 따라 karlo 또는 generate(OpenAI)

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullFileName)
                .contentType("image/jpg")
                .build();

        String s3Url = makeS3Url(fullFileName);

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

        return new S3UploadResponseDTO(fullFileName, s3Url);
    }

    public void deleteFile(String fileDir) {
        String baseUrl = "https://chunsik-dev.s3.ap-northeast-2.amazonaws.com/";
        String fileName = fileDir.replace(baseUrl, ""); // ticket 디렉토리가 아닌 다른 디렉토리의 이미지를 삭제할 땐 파싱방식을 그거에 맞게 바꿔야 함. (fileName = 디렉토리/이미지명 형식)
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(request);
    }

    private String generateFileName(String prefix) {
        String filename = UUID.randomUUID() + ".jpg"; // UUID로 대체하여 고유한 파일 이름을 생성
        return prefix + "/" + filename;
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