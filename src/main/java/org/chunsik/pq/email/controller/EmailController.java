package org.chunsik.pq.email.controller;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.email.exception.TooManyRequestsException;
import org.chunsik.pq.email.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    // 인증번호 요청
    @PostMapping("/request")
    public ResponseEntity<String> sendEmail(@RequestParam String email, HttpServletRequest request, HttpServletResponse response) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("Invalid request format.", HttpStatus.BAD_REQUEST);
        }
        emailService.sendSimpleEmail(email, request, response);
        return new ResponseEntity<>("Verification email sent.", HttpStatus.CREATED);
    }

    // 인증번호 검증
    @PatchMapping("/verification")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        if (email.isEmpty() || code.isEmpty()) {
            return new ResponseEntity<>("Invalid request format.", HttpStatus.BAD_REQUEST);
        }
        boolean isVerified = emailService.verifyCode(email, code);
        if (isVerified) {
            return new ResponseEntity<>("Secret code verified successfully. Confirmation status updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid or expired secret code.", HttpStatus.UNAUTHORIZED);
        }
    }

    // 예외 처리
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<String> handleTooManyRequestsException(TooManyRequestsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        Sentry.captureException(e);
        return new ResponseEntity<>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        Sentry.captureException(e);
        return new ResponseEntity<>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
