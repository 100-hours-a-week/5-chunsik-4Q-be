package org.chunsik.pq.generate.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class CreateImageResponseDto {
    private final String message;
    private final Long id;
}
