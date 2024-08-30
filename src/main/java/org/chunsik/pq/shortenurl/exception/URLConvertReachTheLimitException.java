package org.chunsik.pq.shortenurl.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class URLConvertReachTheLimitException extends RuntimeException{
    private final ErrorCode errorCode;
}
