package org.chunsik.pq.login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.login.constant.JwtConstant;
import org.chunsik.pq.login.dto.JoinDto;
import org.chunsik.pq.login.dto.TokenDto;
import org.chunsik.pq.login.dto.UserLoginRequestDto;
import org.chunsik.pq.login.service.UserService;
import org.chunsik.pq.model.OauthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Value("${chunsik.cookie.maxage}")
    private int maxAge;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginRequestDto userLoginRequestDto, HttpServletResponse response) {
        String email = userLoginRequestDto.getEmail();
        String password = userLoginRequestDto.getPassword();
        TokenDto tokenDto = userService.login(email, password);

        Cookie refreshTokenCookie = new Cookie(JwtConstant.REFRESH_TOKEN, tokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(maxAge);
        response.addCookie(refreshTokenCookie);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put(JwtConstant.ACCESS_TOKEN, tokenDto.getAccessToken());

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> join(@Validated @RequestBody JoinDto joinDto, Errors errors) {
        if (errors.hasErrors()) {
            return new ResponseEntity<>(Map.of("message", "Malformed request"), HttpStatus.BAD_REQUEST);
        }
        userService.join(joinDto, OauthProvider.LOCAL);
        return new ResponseEntity<>(Map.of("message", "Registration success"), HttpStatus.CREATED);
    }
}
