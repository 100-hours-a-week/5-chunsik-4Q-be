package org.chunsik.pq.email.controller;

import org.chunsik.pq.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/email/request")
    public ResponseEntity<String> sendEmail(@RequestParam String toEmail) {
        emailService.sendSimpleEmail(toEmail);
        return new ResponseEntity<>("Verification email sent.", HttpStatus.CREATED);
    }

    @PatchMapping("/email/verification")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = emailService.verifyCode(email, code);
        if (isVerified) {
            return new ResponseEntity<>("Secret code verified successfully. Confirmation status updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid or expired secret code.", HttpStatus.UNAUTHORIZED);
        }
    }
}
