package com.memopet.memopet.domain.oauth2.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.exception.OAuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverOauth implements SocialOauth {
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String NAVER_SNS_CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String NAVER_SNS_CLIENT_SECRET;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String NAVER_SNS_REDIRECT_URI;
    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String NAVER_SNS_AUTH_TYPE;
    @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}")
    private String NAVER_SNS_AUTHORIZATION_URI;
    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String NAVER_SNS_TOKEN_URI;
    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String NAVER_SNS_USER_INFO_URI;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    // 1. redirect Url 생성
    @Override
    public String getOauthRedirectURL() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("response_type", "code");
        queryParams.set("client_id", NAVER_SNS_CLIENT_ID);
        queryParams.set("redirect_uri", NAVER_SNS_REDIRECT_URI);
        queryParams.set("state", new BigInteger(130, new SecureRandom()).toString());

        return UriComponentsBuilder
                .fromUriString(NAVER_SNS_AUTHORIZATION_URI)
                .queryParams(queryParams)
                .encode().build().toString();
    }

    // 2. 코드 추가한 url 생성
    @Override
    public ResponseEntity<String> requestAccessToken(String code) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        try {
            queryParams.set("grant_type", NAVER_SNS_AUTH_TYPE);
            queryParams.set("client_id", NAVER_SNS_CLIENT_ID);
            queryParams.set("client_secret", NAVER_SNS_CLIENT_SECRET);
            queryParams.set("code", code);
            queryParams.set("state", new BigInteger(130, new SecureRandom()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(NAVER_SNS_TOKEN_URI, queryParams, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity;
        } else {
            throw new OAuthException("네이버 로그인에 실패하였습니다.");
        }
    }

    @Override
    public ResponseEntity<String> requestAccessToken(String code, HttpServletRequest request) {
        return null;
    }

    // 3. responseEntity에 담긴 JSON String을 역직렬화해 SocialOAuthToken 객체에 담고 반환
    @Override
    public SocialOauthToken getAccessToken(ResponseEntity<String> response) {
        try {
            log.info("response.getBody() : {}", response.getBody());
            return objectMapper.readValue(response.getBody(), SocialOauthToken.class);
        } catch (Exception e) {
            throw new BadRequestRuntimeException("Mapping problem with UserInfo and SocialLogin Info");
        }
    }

    // 4. 다시 네이버로 3에서 받아온 액세스 토큰을 보내 네이버 사용자 정보를 받아온다.
    @Override
    public ResponseEntity<String> requestUserInfo(SocialOauthToken oAuthToken) {
        //header에 accessToken을 담는다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+ oAuthToken.getAccess_token());

        URI uri = UriComponentsBuilder
                .fromUriString(NAVER_SNS_USER_INFO_URI)
                .build().toUri();

        //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    // 5. 네이버 유저 정보가 담긴 JSON 문자열을 파싱하여 SocialUser 객체에 담기
    @Override
    public SocialUser  getUserInfo(ResponseEntity<String> userInfoRes) {

        try {
            JSONObject jsonObject =
                    (JSONObject) JSONValue.parse(Objects.requireNonNull(userInfoRes.getBody()));

            return objectMapper.readValue(jsonObject .get("response").toString(), SocialUser.class);
        } catch (Exception e) {
            throw new BadRequestRuntimeException("Mapping problem with UserInfo and SocialLogin Info");
        }
    }
}
