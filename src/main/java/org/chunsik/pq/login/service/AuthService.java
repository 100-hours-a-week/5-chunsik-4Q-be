package org.chunsik.pq.login.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.login.constant.JwtConstant;
import org.chunsik.pq.login.dto.JwtTokenResponseDto;
import org.chunsik.pq.login.dto.TokenClaimsDto;
import org.chunsik.pq.login.exception.JwtValidationExpireException;
import org.chunsik.pq.login.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenResponseDto issueAccessTokenByRefreshToken(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookies(request);
        try {
            if (refreshToken == null || !jwtTokenProvider.validateTokenExpiration(refreshToken)) {
                throw new JwtValidationExpireException();
            } else {
                TokenClaimsDto claimsFromToken = jwtTokenProvider.getClaimsFromToken(refreshToken);
                String newAccessToken = jwtTokenProvider.createToken(claimsFromToken.getEmail(), claimsFromToken.getUserId());
                return new JwtTokenResponseDto(newAccessToken);
            }
        } catch (Exception e) { // 재발급 로직 수행 중 예외 발생 시 재로그인 요구 예외를 던진다.
            throw new JwtValidationExpireException();
        }
    }


    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JwtConstant.REFRESH_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
