package org.chunsik.pq.generate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class KarloResponseDTO {
    private String id;
    private String modelVersion;
    private List<Map<String,String>> images;

    public KarloResponseDTO(String id, String modelVersion, List<Map<String, String>> images) {
        this.id = id;
        this.modelVersion = modelVersion;
        this.images = images;
    }
}