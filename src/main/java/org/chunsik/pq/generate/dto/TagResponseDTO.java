package org.chunsik.pq.generate.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TagResponseDTO {
    private final List<String> hotTag;
}
