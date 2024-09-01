package org.chunsik.pq.generate.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.repository.BackgroundImageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BackgroundImageService {

    private final BackgroundImageRepository backgroundImageRepository;

    // 날짜 형식을 "yyyy/MM/dd"로 지정
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public Page<Map<String, Object>> getBackgroundImages(String tagName, String categoryName, String sort, Pageable pageable) {
        // 배경 이미지를 필터링하고 페이징하여 가져옴
        return backgroundImageRepository.findByTagAndCategory(tagName, categoryName, sort, pageable)
                .map(objects -> {
                    // 결과를 Map으로 변환하여 반환
                    Map<String, Object> map = new HashMap<>();
                    map.put("imageId", objects.get("imageId"));
                    map.put("url", objects.get("url"));

                    // 생성 날짜를 "yyyy/MM/dd" 형식으로 포맷팅
                    LocalDateTime localDateTime = (LocalDateTime) objects.get("createdAt");
                    String formattedDate = localDateTime.format(FORMATTER);
                    map.put("createdAt", formattedDate);

                    map.put("categoryName", objects.get("categoryName"));
                    map.put("tags", objects.get("tags"));
                    map.put("likeCount", objects.get("likeCount"));
                    map.put("userName", objects.get("userName"));
                    return map;
                });
    }
}
