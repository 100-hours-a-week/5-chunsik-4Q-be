package org.chunsik.pq.shortenurl.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseConvertUrlDTO {
    private final Long id;
    private final String destUrl;
}
