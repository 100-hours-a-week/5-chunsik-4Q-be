package org.chunsik.pq.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class S3UploadDTO {
    private File file;
    private String originalFileName;
    private Long userId;
    private String categoryId;
    private List<String> tags;
}
