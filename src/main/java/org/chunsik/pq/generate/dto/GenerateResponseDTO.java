package org.chunsik.pq.generate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class GenerateResponseDTO {

    private String url;

    public GenerateResponseDTO(String url) {
        this.url = url;
    }
}
