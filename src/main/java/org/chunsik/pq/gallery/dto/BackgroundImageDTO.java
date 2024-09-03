package org.chunsik.pq.gallery.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@AllArgsConstructor
public class BackgroundImageDTO {

    private Long imageId;
    private String url;
    private String createdAt;
    private String categoryName;
    private List<String> tags;
    private String userName;
    private Long likeCount;
    private boolean isLiked;

    public BackgroundImageDTO(Long imageId, String url, LocalDateTime createdAt, String categoryName, List<String> tags, String userName, Long likeCount, boolean isLiked) {
        this.imageId = imageId;
        this.url = url;
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        this.categoryName = categoryName;
        this.tags = tags;
        this.userName = userName;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}
