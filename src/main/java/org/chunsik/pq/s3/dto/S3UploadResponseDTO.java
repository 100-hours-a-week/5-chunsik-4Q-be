package org.chunsik.pq.s3.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class S3UploadResponseDTO {
    private String fileName;
    private String s3Url;

    public S3UploadResponseDTO(String fileName, String s3Url) {
        this.fileName = fileName;
        this.s3Url = s3Url;
    }
}
