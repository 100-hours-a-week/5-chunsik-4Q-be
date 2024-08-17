package org.chunsik.pq.generate.dto;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateImageDTO {
    private List<String> tags;
    private String tag1;
    private String tag2;
    private String tag3;
    private Long userId;
    private String categoryId;

    @Builder
    public GenerateImageDTO(List<String> tags, Long userId, String categoryId) {
        this.tag2 = tags.size() > 1 ? tags.get(1) : null;
        this.tag3 = tags.size() > 2 ? tags.get(2) : null;
        this.userId = userId;
        this.categoryId = categoryId;
    }
}