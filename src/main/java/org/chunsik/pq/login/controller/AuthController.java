package org.chunsik.pq.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.login.dto.JwtTokenResponseDto;
import org.chunsik.pq.login.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<JwtTokenResponseDto> refreshAccessToken(HttpServletRequest request) {
        JwtTokenResponseDto jwtTokenResponseDto = authService.issueAccessTokenByRefreshToken(request);
        return ResponseEntity.of(Optional.of(jwtTokenResponseDto));
    }
}