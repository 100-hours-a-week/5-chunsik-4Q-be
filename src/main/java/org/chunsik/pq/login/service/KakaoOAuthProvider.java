package org.chunsik.pq.login.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chunsik.pq.login.dto.KakaoAccountResponseDto;
import org.chunsik.pq.login.exception.KakaoJsonProcessingException;
import org.chunsik.pq.login.exception.OauthTokenIsNullException;
import org.chunsik.pq.login.model.OAuthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoOAuthProvider {

    @Value("${kakao.api.client-id}")
    private String clientId;

    @Value("${kakao.api.redirect-uri}")
    private String redirectUri;

    public OAuthToken getTokenByCode(String code) {
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


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(tokenResponse.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException occurred while parsing the OAuth token response: {}", e.getMessage());
            throw new KakaoJsonProcessingException("Kakao JSON ERROR in getAccount", e);
        }

    }

    public KakaoAccountResponseDto getAccountByOAuthToken(OAuthToken oAuthToken) {
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


        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(profileResponse.getBody());
            JsonNode kakaoAccount = jsonNode.path("kakao_account");

            String nickname = kakaoAccount.path("profile").path("nickname").asText();
            String email = kakaoAccount.path("email").asText();

            return new KakaoAccountResponseDto(nickname, email);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException occurred while parsing the OAuth account response: {}", e.getMessage());
            throw new KakaoJsonProcessingException("Kakao JSON ERROR in getAccount", e);
        }
    }
}