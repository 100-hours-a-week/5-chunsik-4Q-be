package org.chunsik.pq.login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.login.exception.JwtValidationExpireException;
import org.chunsik.pq.login.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {

        String refreshToken = getRefreshTokenFromCookies(request);
        try {
            if (refreshToken == null || !jwtTokenProvider.validateTokenExpiration(refreshToken)) {
                throw new JwtValidationExpireException();
            } else {
                String email = jwtTokenProvider.getMemberEmail(refreshToken);
                String newAccessToken = jwtTokenProvider.createToken(email);
                Map<String, String> response = new HashMap<>();
                response.put(ACCESS_TOKEN, newAccessToken);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) { // 재발급 로직 수행 중 예외 발생 시 재로그인 요구 예외를 던진다.
            throw new JwtValidationExpireException();
        }
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}