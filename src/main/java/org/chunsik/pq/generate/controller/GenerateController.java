package org.chunsik.pq.generate.controller;

import org.chunsik.pq.generate.dto.GenerateImageDTO;
import org.chunsik.pq.generate.service.GenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class GenerateController {

    @Autowired
    private GenerateService generateService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateImage(@RequestBody GenerateImageDTO generateImageDTO) {
        // userId와 categoryId가 null인 경우 에러 메시지 반환
        if (generateImageDTO.getUserId() == null) {
            return ResponseEntity.badRequest().body("userId는 필수 값입니다.");
        }
        if (generateImageDTO.getCategoryId() == null) {
            return ResponseEntity.badRequest().body("categoryId는 필수 값입니다.");
        }

        // 태그의 개수가 3개를 초과하는 경우 에러 메시지 반환
        if (generateImageDTO.getTags().size() > 3) {
            return ResponseEntity.badRequest().body("태그는 최대 3개까지 가능합니다.");
        }

        try {
            String imageUrl = generateService.generateAndUploadImage(
                    generateImageDTO.getTags(),
                    generateImageDTO.getUserId(),
                    generateImageDTO.getCategoryId());
            return ResponseEntity.ok().body(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("이미지 생성 중 오류 발생: " + e.getMessage());
        }
    }

}