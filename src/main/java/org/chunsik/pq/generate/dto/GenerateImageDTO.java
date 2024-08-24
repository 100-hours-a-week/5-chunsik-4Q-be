package org.chunsik.pq.generate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GenerateImageDTO {
    private List<String> tags;
    private String category;

    @Builder
    public GenerateImageDTO(List<String> tags, String category) {
        this.tags = tags;
        this.category = category;
    }
}