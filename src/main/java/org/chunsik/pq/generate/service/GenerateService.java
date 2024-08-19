package org.chunsik.pq.generate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.dto.GenerateImageDTO;
import org.chunsik.pq.generate.dto.GenerateResponseDTO;
import org.chunsik.pq.generate.manager.OpenAIManager;
import org.chunsik.pq.generate.model.BackgroundImage;
import org.chunsik.pq.generate.repository.BackgroundImageRepository;
import org.chunsik.pq.s3.dto.S3UploadDTO;
import org.chunsik.pq.s3.dto.S3UploadResponseDTO;
import org.chunsik.pq.s3.manager.S3Manager;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateService {
    private final S3Manager s3Manager;
    private final OpenAIManager openAIManager;
    private final BackgroundImageRepository backgroundImageRepository;

    @Transactional
    public GenerateResponseDTO generateAndUploadImage(GenerateImageDTO generateImageDTO) throws IOException {
        // 이미지 생성
        GenerateResponseDTO generateResponseDTO = openAIManager.generateImage(generateImageDTO.getTags());

        // URL에서 파일 이름만 추출
        File jpgFile = downloadJpg(generateResponseDTO.getUrl());

        // S3 업로드
        S3UploadDTO s3UploadDTO = S3UploadDTO.builder()
                .file(jpgFile)
                .size(jpgFile.length())
                .tags(generateImageDTO.getTags())
                .userId(generateImageDTO.getUserId())
                .categoryId(generateImageDTO.getCategoryId())
                .build();

        S3UploadResponseDTO s3UploadResponseDTO = s3Manager.uploadFile(s3UploadDTO);


        // DB에 저장
        BackgroundImage.BackgroundImageBuilder builder = BackgroundImage.builder()
                .userId(s3UploadDTO.getUserId())
                .url(s3UploadResponseDTO.getS3Url())
                .categoryId(s3UploadDTO.getCategoryId())
                .size(s3UploadDTO.getSize());

        List<String> tags = s3UploadDTO.getTags();

        switch (tags.size()) {
            case 3:
                builder.thirdTag(tags.get(2));
            case 2:
                builder.secondTag(tags.get(1));
            case 1:
                builder.firstTag(tags.get(0));
        }

        BackgroundImage backgroundImage = builder.build();

        backgroundImageRepository.save(backgroundImage);

        return new GenerateResponseDTO(backgroundImage.getUrl());
    }

    // 이미지 변환 부분
    private File downloadJpg(String imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);    // 파일명만 추출
        File jpgFile = File.createTempFile(fileName, "jpg");    // 파일명.jpg
        ImageIO.write(image, "jpg", jpgFile);
        return jpgFile;
    }
}
