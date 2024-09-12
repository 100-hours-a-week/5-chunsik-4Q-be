package org.chunsik.pq.generate.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.model.HotTag;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TagResponseDTO {
    private final List<String> hotTag;
}
