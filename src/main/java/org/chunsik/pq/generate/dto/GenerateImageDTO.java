package org.chunsik.pq.generate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GenerateImageDTO {
    private List<String> tags;
    private Long userId;
    private String categoryId;

    @Builder
    public GenerateImageDTO(List<String> tags, Long userId, String categoryId) {
        this.tags = tags;
        this.userId = userId;
        this.categoryId = categoryId;
    }
}