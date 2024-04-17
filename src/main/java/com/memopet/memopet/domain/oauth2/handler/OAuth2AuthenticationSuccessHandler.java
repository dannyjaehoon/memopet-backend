package com.memopet.memopet.domain.oauth2.handler;


import com.memopet.memopet.domain.member.service.AuthService;
import com.memopet.memopet.domain.oauth2.service.OAuth2UserPrincipal;

import com.memopet.memopet.global.token.JwtTokenGenerator;
import com.memopet.memopet.global.token.JwtTokenUtils;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static com.memopet.memopet.domain.member.service.AuthService.ACCESSTOKENEXPIRYTIME;
import static com.memopet.memopet.domain.member.service.AuthService.REFRESHTOKENEXPIRYTIME;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenGenerator jwtTokenGenerator ;

    public OAuth2AuthenticationSuccessHandler(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2UserPrincipal member = (OAuth2UserPrincipal) authentication.getPrincipal();


        Collection<? extends GrantedAuthority> authorities = member.getAuthorities();

        Authentication authentication1 = new UsernamePasswordAuthenticationToken(member.getName(), member.getPassword(), authorities);

        String accessToken = jwtTokenGenerator.generateAccessToken(authentication, Long.valueOf(ACCESSTOKENEXPIRYTIME));
        String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication1);

        createRefreshTokenCookie(response, refreshToken);
        //token 저장

        String refreshTokenFromCookie = getCookie(request);


        //response.setHeader("AccessToken" , accessToken);
        //response.sendRedirect("http://localhost:3000/");
    }

    public Cookie createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token",refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(REFRESHTOKENEXPIRYTIME); // in seconds
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;
    }
    public String getCookie(HttpServletRequest req){
        Cookie[] cookies=req.getCookies(); // 모든 쿠키 가져오기
        if(cookies!=null){
            for (Cookie c : cookies) {
                String name = c.getName(); // 쿠키 이름 가져오기
                String value = c.getValue(); // 쿠키 값 가져오기
                if (name.equals("refresh_token")) {
                    return value;
                }
            }
        }
        return null;
    }
    public void deleteAllCookies(HttpServletRequest req,HttpServletResponse res) {
        Cookie[] cookies = req.getCookies(); // 모든 쿠키의 정보를 cookies에 저장
        if (cookies != null) { // 쿠키가 한개라도 있으면 실행
            for (int i = 0; i < cookies.length; i++) {
                cookies[i].setMaxAge(0); // 유효시간을 0으로 설정
                res.addCookie(cookies[i]); // 응답에 추가하여 만료시키기.
            }
        }
    }
}