package org.chunsik.pq.login.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.login.dto.JoinDto;
import org.chunsik.pq.login.dto.TokenDto;
import org.chunsik.pq.login.exception.DuplicateEmailException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@RestController
public class OauthController {
    private final UserService userService;

    @Value("${kakao.api.client-id}")
    private String clientId;

    @Value("${kakao.api.redirect-uri}")
    private String redirectUri;

//     "카카오로 로그인" 버튼이 존재하는 페이지 resources/templates/oauthPage.html 확인.
//    @GetMapping("/oauth")
//    public String health() {
//        return "oauthPage";
//    }

    @GetMapping("/auth/kakao/callback")
    public @ResponseBody TokenDto kakaoCallback(String code){
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        // 요청하기 위해 헤더(Header)와 데이터(Body)를 합친다.
        // kakaoTokenRequest는 데이터(Body)와 헤더(Header)를 Entity가 된다.
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // POST 방식으로 Http 요청한다. 그리고 response 변수의 응답 받는다.
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token", // https://{요청할 서버 주소}
                HttpMethod.POST, // 요청할 방식
                kakaoTokenRequest, // 요청할 때 보낼 데이터
                String.class // 요청 시 반환되는 데이터 타입
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;

        try{
            oAuthToken = objectMapper.readValue(response.getBody(),OAuthToken.class);
        }catch (JsonMappingException e){
            e.printStackTrace();
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        RestTemplate rt2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization","Bearer "+oAuthToken.getAccess_token());
        headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
        params2.add("property_keys", "[\"kakao_account.email\" , \"kakao_account.name\",\"kakao_account.profile\"]");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 = new HttpEntity<>(params2, headers2);

        ResponseEntity<String> response2 = rt2.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST,
            kakaoProfileRequest2,// 요청할 때 보낼 데이터 String.class
            String.class
        );

        JoinDto joinDTO = new JoinDto();
        try {
            JsonNode jsonNode = objectMapper.readTree(response2.getBody());
            JsonNode kakaoAccount = jsonNode.path("kakao_account");

            joinDTO.setNickname(kakaoAccount.path("profile").path("nickname").asText());
            joinDTO.setEmail(kakaoAccount.path("email").asText());
            joinDTO.setPassword("1234"); // null 값을 넣어버리면 로그인할 때 오류 발생.
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        // 1. 회원 이메일 존재 확인.
        // 2. 없으면 회원 가입, 있으면 로그인
        // 3. JWT 토큰 발급 (refresh 포함)
        // userService.login은 액세스, 리프래쉬 토큰을 리턴하여 클라이언트가 다음 요청때 사용한다.
        try { // 카카오에서 받은 개인정보로 로그인 수행.
            userService.join(joinDTO, OauthProvider.KAKAO);
            return userService.login(joinDTO.getEmail(), joinDTO.getPassword());
            // 로그인 처리 및 JWT 발급
        }catch (DuplicateEmailException e){ // 이미 존재하는 이메일인 경우 로그인만 진행
            return userService.login(joinDTO.getEmail(), joinDTO.getPassword());
        }
    }
}
