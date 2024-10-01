package org.chunsik.pq.login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chunsik.pq.login.dto.KakaoAccountResponseDto;
import org.chunsik.pq.login.dto.SignUpOrLoginDto;
import org.chunsik.pq.login.dto.TokenDto;
import org.chunsik.pq.login.model.OAuthToken;
import org.chunsik.pq.login.model.OauthProvider;
import org.chunsik.pq.login.service.KakaoOAuthProvider;
import org.chunsik.pq.login.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@Slf4j
public class OauthController {
    private final UserService userService;
    private final KakaoOAuthProvider kakaoOAuthProvider;

    @Value("${chunsik.domain}")
    private String cookieDomain;

    @Value("${chunsik.front.url}")
    private String frontDomain;

    @Value("${chunsik.cookie.maxage}")
    private int maxAge;

    @GetMapping("/auth/kakao/callback")
    public void kakaoCallback(String code, HttpServletResponse response) throws IOException {
        OAuthToken tokenByCode = kakaoOAuthProvider.getTokenByCode(code);
        KakaoAccountResponseDto responseDto = kakaoOAuthProvider.getAccountByOAuthToken(tokenByCode);
        TokenDto tokenDto = userService.signUpOrLogin(new SignUpOrLoginDto(responseDto.getNickname(), responseDto.getEmail()), OauthProvider.KAKAO);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(maxAge);
        refreshTokenCookie.setDomain(cookieDomain);
        response.addCookie(refreshTokenCookie);

        response.sendRedirect(frontDomain);
    }
}