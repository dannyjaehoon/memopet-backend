package com.memopet.memopet.domain.oauth2.service;

import com.memopet.memopet.domain.member.dto.LoginResponseDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.member.service.AuthService;
import com.memopet.memopet.domain.oauth2.entity.*;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.exception.OAuthException;
import com.memopet.memopet.global.token.JwtTokenGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.memopet.memopet.domain.member.service.AuthService.ACCESSTOKENEXPIRYTIME;
import static com.memopet.memopet.domain.member.service.AuthService.REFRESHTOKENEXPIRYTIME;

@Service
@Slf4j
@RequiredArgsConstructor
public class OauthService extends DefaultOAuth2UserService {
    private final List<SocialOauth> socialOauthList;
    private final MemberRepository memberRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    // 1. redirectURL 만들기
    public String request(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        return socialOauth.getOauthRedirectURL();
    }

    // 2. 액세스 토큰 만들기
    public ResponseEntity<String> requestAccessToken(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        return socialOauth.requestAccessToken(code);
    }
    // 3. 소셜 타입 찾기
    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new OAuthException("알 수 없는 SocialLoginType 입니다."));
    }
    // 4. 클라이언트로 보낼 GetSocialOAuthRes 객체 만들기
    public LoginResponseDto oAuthLogin(String socialLoginTypeStr, String code, HttpServletRequest request, HttpServletResponse response) {
        SocialLoginType socialLoginType = null;
        if(socialLoginTypeStr.equals("google")) {
            socialLoginType = SocialLoginType.GOOGLE;
        } else if(socialLoginTypeStr.equals("naver")){
            socialLoginType = SocialLoginType.NAVER;
        } else {
            throw new BadRequestRuntimeException("This socialLoginType is not supported");
        }

        SocialOauth socialOauth = findSocialOauthByType(socialLoginType);
        ResponseEntity<String> accessTokenResponse = null;
        if(socialLoginType.equals(SocialLoginType.GOOGLE)) {
            //소셜서버로 일회성 코드를 보내 액세스 토큰이 담긴 응답객체를 받아옴
            accessTokenResponse = socialOauth.requestAccessToken(code,request);
        } else {
            accessTokenResponse = socialOauth.requestAccessToken(code);
        }

        //응답 객체가 JSON형식으로 되어 있으므로, 이를 deserialization해서 자바 객체에 담기
        SocialOauthToken oAuthToken =
                socialOauth.getAccessToken(accessTokenResponse);
        //액세스 토큰을 다시 서비스 제공자로 보내 서비스제공자에 저장된 사용자 정보가 담긴 응답 객체를 받아옴
        ResponseEntity<String> userInfoResponse =
                socialOauth.requestUserInfo(oAuthToken);
        //다시 JSON 형식의 응답 객체를 자바 객체로 역직렬화한다.
        SocialUser socialUser = socialOauth.getUserInfo(userInfoResponse);
        String userEmail = socialUser.getEmail();
        String username = socialUser.getName();

        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(userEmail);
        Member member = null;
        // DB에 해당 유저가 있는지 조회 후 없으면 save
        if (memberByEmail.isEmpty()) {
            member = Member.builder()
                    .username(username)
                    .password(passwordEncoder.encode(userEmail))
                    .email(userEmail)
                    .loginFailCount(0)
                    .memberStatus(MemberStatus.NORMAL)
                    .provideId(socialUser.id)
                    .provider(socialLoginTypeStr)
                    .roles("ROLE_USER")
                    .activated(true)
                    .build();
            memberRepository.save(member);
        } else {
            member = memberByEmail.get();
        }

        OAuth2UserPrincipal userDetailsInfo = new OAuth2UserPrincipal(member);

        Collection<? extends GrantedAuthority> authorities = userDetailsInfo.getAuthorities();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsInfo.getName(), member.getPassword(), authorities);

        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

        createRefreshTokenCookie(response,refreshToken);

        authService.saveUserRefreshToken(member,refreshToken);

        // 액세스 토큰과 위에서 만든 jwtToken, 이외 정보들이 담긴 자바 객체를 다시 전송한다.

        return LoginResponseDto.builder()
                .username(member.getUsername())
                .userStatus(member.getMemberStatus())
                .userRole(member.getRoles().equals("ROLE_USER") ? "GU" : "SA")
                .loginFailCount(member.getLoginFailCount())
                .accessToken(accessToken)
                .accessTokenExpiry(ACCESSTOKENEXPIRYTIME)
                .build();

    }

    public static Cookie createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token",refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(REFRESHTOKENEXPIRYTIME); // in seconds
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;
    }
    public static String getCookie(HttpServletRequest req){
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
}