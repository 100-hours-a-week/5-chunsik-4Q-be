package org.chunsik.pq.generate.manager;


import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.dto.KarloResponseDTO;
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

@Component
@RequiredArgsConstructor
public class KarloManager {
    private final RestTemplate restTemplate;

    @Value("${kakao.api.client-id}")
    private String apiKey;

    public String generateImage(List<String> tags) {
        String url = "https://api.kakaobrain.com/v2/inference/karlo/t2i";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        headers.set("Content-Type", "application/json");


        String prompt = String.join(", ", tags) + ".";


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("version", "v2.1");
        requestBody.put("prompt", prompt);
        requestBody.put("width", 1024);
        requestBody.put("height", 1024);
        requestBody.put("image_format", "jpeg");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<KarloResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, KarloResponseDTO.class);

        return response.getBody().getImages().get(0).get("image");
    }
}