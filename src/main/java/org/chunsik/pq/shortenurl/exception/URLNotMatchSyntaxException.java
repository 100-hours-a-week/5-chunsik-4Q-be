package org.chunsik.pq.shortenurl.exception;

import lombok.Getter;

import java.net.URISyntaxException;

@Getter
public class URLNotMatchSyntaxException extends URISyntaxException {
    private final ErrorCode errorCode;

    public URLNotMatchSyntaxException(String input, String reason, ErrorCode errorCode) {
        super(input, reason);
        this.errorCode = errorCode;
    }
}