package com.memopet.memopet.domain.oauth2.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.exception.OAuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {
    @Value("${spring.security.oauth2.client.registration.google.url}")
    private String GOOGLE_SNS_BASE_URL;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_SNS_CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.google.redirect-url}")
    private String GOOGLE_SNS_REDIRECT_URL;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;
    @Value("${spring.security.oauth2.client.registration.google.token-url}")
    private String GOOGLE_SNS_TOKEN_BASE_URL;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;


    // 1. redirect Url 생성
    @Override
    public String getOauthRedirectURL() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("scope", "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile");
        queryParams.set("response_type", "code");
        queryParams.set("client_id", GOOGLE_SNS_CLIENT_ID);
        queryParams.set("redirect_uri", GOOGLE_SNS_REDIRECT_URL);

        return UriComponentsBuilder
                .fromUriString(GOOGLE_SNS_BASE_URL)
                .queryParams(queryParams)
                .encode().build().toString();
    }


    // 2. 코드 추가한 url 생성
    @Override
    public ResponseEntity<String> requestAccessToken(String code, HttpServletRequest request ) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        queryParams.set("code", code);
        queryParams.set("client_id", GOOGLE_SNS_CLIENT_ID);
        queryParams.set("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        queryParams.set("redirect_uri", request.getRequestURL().toString());
        queryParams.set("grant_type", "authorization_code");


        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(GOOGLE_SNS_TOKEN_BASE_URL, queryParams, String.class);


        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity;
        } else {
            throw new OAuthException("구글 로그인에 실패하였습니다.");
        }
    }

    @Override
    public ResponseEntity<String> requestAccessToken(String code) {
        return null;
    }

    // 3. responseEntity에 담긴 JSON String을 역직렬화해 SocialOAuthToken 객체에 담고 반환
    @Override
    public SocialOauthToken getAccessToken(ResponseEntity<String> response) {

        try  {
            log.info("response.getBody() : {}", response.getBody());
            return objectMapper.readValue(response.getBody(), SocialOauthToken.class);
        } catch (Exception e) {
            throw new BadRequestRuntimeException("Mapping problem with UserInfo and SocialLogin Info");
        }
    }

    // 4. 다시 구글로 3에서 받아온 액세스 토큰을 보내 구글 사용자 정보를 받아온다.
    @Override
    public ResponseEntity<String> requestUserInfo(SocialOauthToken oAuthToken) {
        //header에 accessToken을 담는다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ oAuthToken.getAccess_token());

        URI uri = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/oauth2/v1/userinfo")
                .build().toUri();

        //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    // 5. 구글 유저 정보가 담긴 JSON 문자열을 파싱하여 SocailUser 객체에 담기
    @Override
    public SocialUser getUserInfo(ResponseEntity<String> userInfoRes) {
        try {
            SocialUser socialUser = objectMapper.readValue(userInfoRes.getBody(), SocialUser.class);
            return socialUser;
        } catch(Exception e) {
            throw new BadRequestRuntimeException("Mapping problem with UserInfo and SocialLogin Info");
        }

    }
}

