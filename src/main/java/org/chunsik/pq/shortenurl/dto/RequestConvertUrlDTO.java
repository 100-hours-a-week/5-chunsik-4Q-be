package org.chunsik.pq.shortenurl.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RequestConvertUrlDTO {
    private String srcUrl;
}
