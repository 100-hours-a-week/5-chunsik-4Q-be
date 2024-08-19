package org.chunsik.pq.s3.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.util.List;

@Getter
@Builder
public class S3UploadDTO {
    private File file;
    private Long size;
    private Long userId;
    private String categoryId;
    private List<String> tags;

    public S3UploadDTO(File file, Long size, Long userId, String categoryId, List<String> tags) {
        this.file = file;
        this.size = size;
        this.userId = userId;
        this.categoryId = categoryId;
        this.tags = tags;
    }
}
