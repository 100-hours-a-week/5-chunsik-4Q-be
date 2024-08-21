package org.chunsik.pq.login.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chunsik.pq.login.dto.JoinDto;
import org.chunsik.pq.login.dto.TokenDto;
import org.chunsik.pq.login.exception.OauthTokenIsNullException;
import org.chunsik.pq.login.service.UserService;
import org.chunsik.pq.model.OAuthToken;
import org.chunsik.pq.model.OauthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
public class OauthController {
    private final UserService userService;

    @Value("${kakao.api.client-id}")
    private String clientId;

    @Value("${kakao.api.redirect-uri}")
    private String redirectUri;

    @Value("${chunsik.cookie.maxage}")
    private int maxAge;

    @GetMapping("/auth/kakao/callback")
    public void kakaoCallback(String code, HttpServletResponse response) throws IOException {
        RestTemplate tokenRequestTemplate = new RestTemplate();

        HttpHeaders tokenRequestHeaders = new HttpHeaders();
        tokenRequestHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> tokenRequestParams = new LinkedMultiValueMap<>();
        tokenRequestParams.add("grant_type", "authorization_code");
        tokenRequestParams.add("client_id", clientId);
        tokenRequestParams.add("redirect_uri", redirectUri);
        tokenRequestParams.add("code", code);

        // 요청하기 위해 헤더(Header)와 데이터(Body)를 합친다.
        // kakaoTokenRequest는 데이터(Body)와 헤더(Header)를 Entity가 된다.
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(tokenRequestParams, tokenRequestHeaders);

        // POST 방식으로 Http 요청한다. 그리고 response 변수의 응답 받는다.
        ResponseEntity<String> tokenResponse = tokenRequestTemplate.exchange(
                "https://kauth.kakao.com/oauth/token", // https://{요청할 서버 주소}
                HttpMethod.POST, // 요청할 방식
                kakaoTokenRequest, // 요청할 때 보낼 데이터
                String.class // 요청 시 반환되는 데이터 타입
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;

        try {
            oAuthToken = objectMapper.readValue(tokenResponse.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException occurred while parsing the OAuth token response: {}", e.getMessage());
        }

        RestTemplate profileRequestTemplate = new RestTemplate();

        HttpHeaders profileRequestHeaders = new HttpHeaders();
        try {
            profileRequestHeaders.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
        } catch (NullPointerException e) {
            throw new OauthTokenIsNullException();
        }
        profileRequestHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> profileRequestParams = new LinkedMultiValueMap<>();
        profileRequestParams.add("property_keys", "[\"kakao_account.email\" , \"kakao_account.name\",\"kakao_account.profile\"]");

        HttpEntity<MultiValueMap<String, String>> profileRequestEntity = new HttpEntity<>(profileRequestParams, profileRequestHeaders);

        ResponseEntity<String> profileResponse = profileRequestTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                profileRequestEntity,// 요청할 때 보낼 데이터 String.class
                String.class
        );

        JoinDto joinDTO = new JoinDto();
        try {
            JsonNode jsonNode = objectMapper.readTree(profileResponse.getBody());
            JsonNode kakaoAccount = jsonNode.path("kakao_account");

            String nickname = kakaoAccount.path("profile").path("nickname").asText();
            String email = kakaoAccount.path("email").asText();
            String password = "1234"; // null 값을 넣어버리면 로그인할 때 오류 발생.

            joinDTO = new JoinDto(nickname, email, password);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException occurred while parsing the OAuth token response: {}", e.getMessage());
        }

        // 1. 회원 이메일 존재 확인.
        // 2. 없으면 회원 가입, 있으면 로그인
        // 3. JWT 토큰 발급 (refresh 포함)
        // userService.login은 액세스, 리프래쉬 토큰을 리턴하여 클라이언트가 다음 요청때 사용한다.

        if (!userService.checkIfUserExistsByEmail(joinDTO.getEmail())) {
            userService.join(joinDTO, OauthProvider.KAKAO);
        }

        TokenDto tokenDto = userService.login(joinDTO.getEmail(), joinDTO.getPassword());
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(maxAge);
        response.addCookie(refreshTokenCookie);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", tokenDto.getAccessToken());

        response.sendRedirect("https://www.qqqq.world");
    }
}