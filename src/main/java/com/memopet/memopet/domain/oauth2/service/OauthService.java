package com.memopet.memopet.domain.oauth2.service;

import com.memopet.memopet.domain.member.dto.SocialLoginResponseDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberSocial;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.entity.RefreshToken;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.member.repository.MemberSocialRepository;
import com.memopet.memopet.domain.member.repository.RefreshTokenRepository;
import com.memopet.memopet.domain.member.service.AuthService;
import com.memopet.memopet.domain.oauth2.entity.SocialLoginType;
import com.memopet.memopet.domain.oauth2.entity.SocialOauth;
import com.memopet.memopet.domain.oauth2.entity.SocialOauthToken;
import com.memopet.memopet.domain.oauth2.entity.SocialUser;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.exception.OAuthException;
import com.memopet.memopet.global.common.service.MemberCreationRabbitConsumer;
import com.memopet.memopet.global.token.JwtTokenGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class OauthService extends DefaultOAuth2UserService {
    private final List<SocialOauth> socialOauthList;
    private final MemberRepository memberRepository;
    private final MemberCreationRabbitConsumer memberCreationRabbitConsumer;
    private final MemberSocialRepository memberSocialRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final AuthService authService;

    // 1. redirectURL 만들기
    public String request(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        return socialOauth.getOauthRedirectURL();
    }

    // 2. 소셜 타입 찾기
    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new OAuthException("알 수 없는 SocialLoginType 입니다."));
    }

    // 3. 클라이언트로 보낼 GetSocialOAuthRes 객체 만들기

    @Transactional(readOnly = false)
    public SocialLoginResponseDto oAuthLogin(String socialLoginTypeStr, String code, HttpServletRequest request) {

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
        String phoneNum = Objects.isNull(socialUser.getMobile()) ? "" : socialUser.getMobile().replace("-","");


        Optional<Member> memberByPhoneNum = memberRepository.findMemberByPhoneNum(phoneNum);
        String memberId = memberCreationRabbitConsumer.generateUniqueId();

        if(memberByPhoneNum.isEmpty()) {
            Member member = Member.builder()
                    .username(username)
                    .memberId(memberId)
                    .phoneNum(phoneNum)
                    .agreeYn(true)
                    .agreeDate(LocalDateTime.now())
                    .build();
            memberRepository.save(member);
        } else {
            Member member = memberByPhoneNum.get();
            // if member entity is already created, use the memberId from member
            memberId = member.getMemberId();
        }

        Optional<MemberSocial> memberByEmail = memberSocialRepository.findMemberByEmail(userEmail);
        MemberSocial memberSocial = null;
        // DB에 해당 유저가 있는지 조회 후 없으면 save
        if (memberByEmail.isEmpty()) {
            memberSocial = MemberSocial.builder()
                    .username(username)
                    .email(userEmail)
                    .memberId(memberId)
                    .phoneNum(phoneNum)
                    .loginFailCount(0)
                    .memberStatus(MemberStatus.NORMAL)
                    .providerId(socialUser.id)
                    .provider(socialLoginTypeStr)
                    .lastLoginDate(LocalDateTime.now())
                    .roles("ROLE_USER")
                    .build();
            memberSocialRepository.save(memberSocial);
        } else {
            memberSocial = memberByEmail.get();
        }

        OAuth2UserPrincipal userDetailsInfo = new OAuth2UserPrincipal(memberSocial);

        Collection<? extends GrantedAuthority> authorities = userDetailsInfo.getAuthorities();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsInfo.getName(), memberSocial.getPassword(), authorities);

        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        Optional<RefreshToken> byAccessToken = refreshTokenRepository.findByAccessToken(accessToken);

        if(byAccessToken.isEmpty()) {
            authService.saveRefreshToken(memberSocial, accessToken);
        }

        return SocialLoginResponseDto.builder()
                .username(memberSocial.getUsername())
                .email(memberSocial.getEmail())
                .userRole(memberSocial.getRoles())
                .userStatus(memberSocial.getMemberStatus())
                .phoneNumYn(phoneNum.equals("") ? "N": "Y")
                .accessToken(accessToken)
                .build();

    }

}