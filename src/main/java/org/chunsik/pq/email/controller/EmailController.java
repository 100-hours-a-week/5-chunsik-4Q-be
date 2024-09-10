package org.chunsik.pq.email.controller;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.email.dto.EmailConfirmRequestDTO;
import org.chunsik.pq.email.exception.InvalidEmailException;
import org.chunsik.pq.email.exception.NotChooseVerifyRoleException;
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
    public ResponseEntity<String> sendEmail(@RequestBody EmailConfirmRequestDTO dto, HttpServletRequest request, HttpServletResponse response) {
        String email = dto.getEmail();
        if (email == null || email.isEmpty()) {
            return new ResponseEntity<>("Invalid request format.", HttpStatus.BAD_REQUEST);
        }
        emailService.sendSimpleEmail(email, request, response);
        return new ResponseEntity<>("Verification email sent.", HttpStatus.CREATED);
    }

    // 인증번호 검증
    @PatchMapping("/verification")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailConfirmRequestDTO dto) {

        if (dto.getEmail().isEmpty() || dto.getCode().isEmpty()) {
            return new ResponseEntity<>("Invalid request format.", HttpStatus.BAD_REQUEST);
        }

        boolean isVerified = emailService.verifyCode(dto);


        if (isVerified) {
            return new ResponseEntity<>("Secret code verified successfully. Confirmation status updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid or expired secret code.", HttpStatus.UNAUTHORIZED);
        }
    }

    @PatchMapping("/reset")
    public ResponseEntity<String> resetEmail(@RequestBody EmailConfirmRequestDTO dto) {

        if (dto.getEmail().isEmpty() || dto.getCode().isEmpty()) {
            return new ResponseEntity<>("Invalid request format.", HttpStatus.BAD_REQUEST);
        }

        boolean isVerified = emailService.resetCode(dto);


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

    @ExceptionHandler(NotChooseVerifyRoleException.class)
    public ResponseEntity<String> handleNotChooseVerifyRoleException(NotChooseVerifyRoleException e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<String> handleInvalidEmailException(InvalidEmailException e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
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
