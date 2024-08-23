package org.chunsik.pq.generate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GenerateImageDTO {
    private List<String> tags;
    private String categoryId;

    @Builder
    public GenerateImageDTO(List<String> tags, String categoryId) {
        this.tags = tags;
        this.categoryId = categoryId;
    }
}