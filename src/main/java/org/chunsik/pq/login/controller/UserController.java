package org.chunsik.pq.login.controller;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.login.dto.JoinDto;
import org.chunsik.pq.login.dto.TokenDto;
import org.chunsik.pq.login.dto.UserLoginRequestDto;
import org.chunsik.pq.login.service.UserService;
import org.chunsik.pq.model.OauthProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserLoginRequestDto userLoginRequestDto){
        String email = userLoginRequestDto.getEmail();
        String password = userLoginRequestDto.getPassword();
        TokenDto tokenDto =  userService.login(email,password);

        return ResponseEntity.ok(tokenDto);
    }

    // 인증 필요 페이지 (임시 테스트 용)
    @PostMapping("/test")
    public String test(){
        return "토큰 사용 success";
    }

    @PostMapping("/register")
    public ResponseEntity<?> join(@Validated @RequestBody JoinDto joinDto, Errors errors){
        if (errors.hasErrors()) {
            return new ResponseEntity<>("Invalid request format.",HttpStatus.BAD_REQUEST);
        }
        userService.join(joinDto, OauthProvider.LOCAL);
        return new ResponseEntity<>("Registration successful.",HttpStatus.CREATED);
    }
}
