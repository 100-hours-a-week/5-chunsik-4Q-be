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
    private Boolean hidden;

    @Builder
    public GenerateImageDTO(List<String> tags, String category, Boolean hidden) {
        this.tags = tags;
        this.category = category;
        this.hidden = hidden;
    }
}