package org.chunsik.pq.shortenurl.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseRepositoryUrlDTO {
    private final String srcUrl;
    private final String destUrl;
}
