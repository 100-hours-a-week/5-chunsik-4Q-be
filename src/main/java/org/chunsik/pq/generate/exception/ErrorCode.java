package org.chunsik.pq.generate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. 입력값을 확인해주세요."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "해당 리소스에 접근할 권한이 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "해당 리소스에 대한 접근이 금지되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "현재 리소스 상태와 충돌이 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 예기치 않은 오류가 발생했습니다."),
    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "제공된 데이터에 유효성 오류가 있습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스에 접근하는 동안 오류가 발생했습니다."),
    IMAGE_GENERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 생성 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}