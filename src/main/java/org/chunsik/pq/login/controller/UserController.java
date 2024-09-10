package org.chunsik.pq.login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.login.dto.*;
import org.chunsik.pq.login.service.UserService;
import org.chunsik.pq.login.model.OauthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Value("${chunsik.cookie.maxage}")
    private int maxAge;

    @Value("${chunsik.domain}")
    private String cookieDomain;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponseDto> login(@RequestBody UserLoginRequestDto userLoginRequestDto, HttpServletResponse response) {
        String email = userLoginRequestDto.getEmail();
        String password = userLoginRequestDto.getPassword();
        TokenDto tokenDto = userService.login(email, password);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(maxAge);
        refreshTokenCookie.setDomain(cookieDomain);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new AccessTokenResponseDto(tokenDto.getAccessToken(), tokenDto.getExpiration()));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutSuccessDTO> logout(HttpServletResponse response) {
        LogoutSuccessDTO logoutSuccessDTO = userService.logout();
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setDomain(cookieDomain);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(logoutSuccessDTO);
    }


    @PostMapping("/register")
    public ResponseEntity<String> join(@Validated @RequestBody JoinDto joinDto, Errors errors) {
        if (errors.hasErrors()) {
            ObjectError objectError = errors.getAllErrors().stream().findAny().orElseThrow();
            return new ResponseEntity<>("Invalid request format. " + objectError.getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        userService.join(joinDto, OauthProvider.LOCAL);
        return new ResponseEntity<>("Registration successful.", HttpStatus.CREATED);
    }

    @PatchMapping("/modify")
    public ResponseEntity<ModifyResponseDTO> modify(@Validated @RequestBody ModifyRequestDTO modifyRequestDTO) {
        return ResponseEntity.ok(userService.modify(modifyRequestDTO));
    }

    @PatchMapping("/reset")
    public ResponseEntity<ResetResponseDTO> resetPassword(@Validated @RequestBody ResetPasswordDTO resetPasswordDTO) {
        return ResponseEntity.ok(userService.resetPassword(resetPasswordDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDto> me() {
        return ResponseEntity.ok(userService.me());
    }
}
