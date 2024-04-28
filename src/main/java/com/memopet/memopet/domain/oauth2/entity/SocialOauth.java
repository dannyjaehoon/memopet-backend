package com.memopet.memopet.domain.oauth2.entity;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface SocialOauth {
    String getOauthRedirectURL();
    ResponseEntity<String> requestAccessToken(String code);
    ResponseEntity<String> requestAccessToken(String code, HttpServletRequest request);
    SocialOauthToken getAccessToken(ResponseEntity<String> response);
    ResponseEntity<String> requestUserInfo(SocialOauthToken oauthToken);
    SocialUser getUserInfo(ResponseEntity<String> userInfoRes);

    default SocialLoginType type() {
        if (this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
        } else if (this instanceof NaverOauth) {
            return SocialLoginType.NAVER;
        } else {
            return null;
        }
    }


}
