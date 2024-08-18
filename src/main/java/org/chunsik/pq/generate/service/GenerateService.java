package org.chunsik.pq.generate.service;

import org.chunsik.pq.generate.dto.GenerateApiResponseDTO;
import org.chunsik.pq.generate.dto.GenerateImageDTO;
import org.chunsik.pq.generate.dto.GenerateResponseDTO;
import org.chunsik.pq.generate.manager.GenerateManager;
import org.chunsik.pq.s3.dto.S3UploadDTO;
import org.chunsik.pq.s3.manager.S3Manager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
public class GenerateService {
    private final S3Manager s3Manager;
    private final GenerateManager generateManager;

    public GenerateService(GenerateManager generateManager, S3Manager s3Manager) {
        this.generateManager = generateManager;
        this.s3Manager = s3Manager;
    }

    public GenerateResponseDTO generateAndUploadImage(GenerateImageDTO generateImageDTO) throws IOException {
        // 이미지 생성
        GenerateResponseDTO generateResponseDTO = generateManager.generateImage(generateImageDTO.getTags());

        // URL을 jpg로 바꿈.
        File jpgFile = downloadAndConvertToJpg(generateResponseDTO.getUrl());

        S3UploadDTO s3UploadDTO = S3UploadDTO.builder()
                .file(jpgFile)
                .size(jpgFile.length())
                .tags(generateImageDTO.getTags())
                .userId(generateImageDTO.getUserId())
                .categoryId(generateImageDTO.getCategoryId())
                .build();

        // S3 업로드
        return s3Manager.uploadFile(s3UploadDTO);
    }


    // 이미지 변환 부분
    private File downloadAndConvertToJpg(String imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);    // 파일명만 추출
        File jpgFile = File.createTempFile(fileName, "jpg");    // 파일명.jpg
        ImageIO.write(image, "jpg", jpgFile);
        return jpgFile;
    }

    public ResponseEntity<String> generateImageDownload(String fileName) {
        String home = System.getProperty("user.home");  // 사용자의 home 디렉토리 위치 받아오기
        String downloadPath = home + "/Downloads/" + fileName;  // Downloads 디렉토리에 사진 저장

        s3Manager.downloadFile(fileName, downloadPath);
        return ResponseEntity.ok("Download success");
    }
}
