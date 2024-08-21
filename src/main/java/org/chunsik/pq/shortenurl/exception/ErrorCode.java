package org.chunsik.pq.shortenurl.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    REACH_LIMIT(HttpStatus.NOT_FOUND, "URL 단축에 실패했습니다."),
    FORMAT_NOT_MATCH(HttpStatus.BAD_REQUEST, "URL 형식이 올바르지 않습니다."),
    SYNTAX_NOT_MATCH(HttpStatus.BAD_REQUEST, "연결된 URL이 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
