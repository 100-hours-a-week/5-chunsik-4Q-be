package org.chunsik.pq.email.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.chunsik.pq.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/request")
    public ResponseEntity<String> sendEmail(@RequestParam String toEmail, HttpServletRequest request, HttpServletResponse response) {
        try {
            emailService.sendSimpleEmail(toEmail, request, response);
            return new ResponseEntity<>("Verification email sent.", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @PatchMapping("/verification")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = emailService.verifyCode(email, code);
        if (isVerified) {
            return new ResponseEntity<>("Secret code verified successfully. Confirmation status updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid or expired secret code.", HttpStatus.UNAUTHORIZED);
        }
    }
}
