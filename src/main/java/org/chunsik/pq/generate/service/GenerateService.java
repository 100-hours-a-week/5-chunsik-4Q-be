package org.chunsik.pq.generate.service;

import org.chunsik.pq.s3.dto.S3UploadDTO;
import org.chunsik.pq.s3.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenerateService {
    private final RestTemplate restTemplate;

    @Autowired
    private S3Service s3Service;

    @Value("${openai.api.key}")
    private String apiKey;

    public GenerateService(RestTemplate restTemplate, S3Service s3Service) {
        this.restTemplate = restTemplate;
        this.s3Service = s3Service;
    }

    public String generateAndUploadImage(List<String> tags, Long userId, String categoryId) throws IOException {
        // 1. OpenAI API를 사용하여 이미지 생성
        String imageUrl = generateImage(tags);
        String fileName = extractFileNameFromUrl(imageUrl);


        // 2. 이미지 다운로드 및 JPG 파일로 변환
        File jpgFile = downloadAndConvertToJpg(imageUrl, fileName);

        S3UploadDTO s3UploadDTO = new S3UploadDTO(jpgFile, fileName, userId, categoryId, tags);
        // 3. S3에 업로드
        return s3Service.uploadFile(s3UploadDTO);
    }

    public String generateImage(List<String> tags) {
        String url = "https://api.openai.com/v1/images/generations";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        // 태그를 콤마로 구분하여 문자열로 변환
        String prompt = "You are the world’s best designer. Create an image that matches the mood of these tags: " +
                String.join(", ", tags) + ".";

        System.out.println(prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "dall-e-3");
        requestBody.put("prompt", prompt);
        requestBody.put("n", 1);
        requestBody.put("size", "1024x1024");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        List<Map<String, String>> data = (List<Map<String, String>>) response.getBody().get("data");
        return data.get(0).get("url");  // API에서 반환된 이미지 URL 등을 포함한 JSON 응답
    }

    private File downloadAndConvertToJpg(String imageUrl, String originalFileName) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        File jpgFile = File.createTempFile(originalFileName.replace(".png", ""), ".jpg");
        ImageIO.write(image, "jpg", jpgFile);
        return jpgFile;
    }

    private String extractFileNameFromUrl(String imageUrl) {
        String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        return fileName.replace(".png", ".jpg");
    }
}
