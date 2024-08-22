package org.chunsik.pq.generate.manager;


import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.dto.GenerateApiResponseDTO;
import org.chunsik.pq.generate.dto.GenerateResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OpenAIManager {
    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;

    public GenerateResponseDTO generateImage(List<String> tags) {
        String url = "https://api.openai.com/v1/images/generations";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        // 태그를 콤마로 구분하여 문자열로 변환
        String prompt = "You are the world’s best designer. Create an image that matches the mood of these tags: " +
                String.join(", ", tags) + ".";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "dall-e-3");
        requestBody.put("prompt", prompt);
        requestBody.put("n", 1);
        requestBody.put("size", "1024x1024");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<GenerateApiResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, GenerateApiResponseDTO.class);


        String responseUrl = response.getBody().getData().get(0).get("url");

        return GenerateResponseDTO.builder()
                .url(responseUrl)
                .build();
    }
}
