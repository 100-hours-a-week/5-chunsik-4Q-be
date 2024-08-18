package org.chunsik.pq.generate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class GenerateApiResponseDTO {
    private int created;
    private List<Map<String, String>> data;

    public GenerateApiResponseDTO(int created, List<Map<String, String>> data) {
        this.created = created;
        this.data = data;
    }

}
