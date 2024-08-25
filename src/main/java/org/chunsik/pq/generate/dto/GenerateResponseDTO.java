package org.chunsik.pq.generate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GenerateResponseDTO {
    private String url;
    private Long backgroundImageId;
}
