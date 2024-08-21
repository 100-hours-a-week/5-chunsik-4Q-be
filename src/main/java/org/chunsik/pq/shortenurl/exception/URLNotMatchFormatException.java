package org.chunsik.pq.shortenurl.exception;

import lombok.Getter;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
public class URLNotMatchFormatException extends MethodArgumentNotValidException {
    private final ErrorCode errorCode;

    public URLNotMatchFormatException(MethodParameter parameter, BindingResult bindingResult, ErrorCode errorCode) {
        super(parameter, bindingResult);
        this.errorCode = errorCode;
    }
}