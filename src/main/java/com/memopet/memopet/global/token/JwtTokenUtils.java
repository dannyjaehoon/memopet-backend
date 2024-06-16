package com.memopet.memopet.global.token;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.member.repository.MemberSocialRepository;
import com.memopet.memopet.domain.member.service.AuthService;
import com.memopet.memopet.domain.member.service.LoginService;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.config.UserInfoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    private final MemberRepository memberRepository ;
    private final MemberSocialRepository memberSocialRepository;
    public String getUserName(Jwt jwtToken){
        return jwtToken.getSubject();
    }

    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails){
        final String userName = getUserName(jwtToken);
        boolean isTokenExpired = getIfTokenIsExpired(jwtToken);
        boolean isTokenUserSameAsDatabase = userName.equals(userDetails.getUsername());
        return !isTokenExpired  && isTokenUserSameAsDatabase;

    }

    private boolean getIfTokenIsExpired(Jwt jwtToken) {
        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }

    public UserDetails userDetails(String email){
        return memberSocialRepository
                .findMemberByEmail(email)
                .map(UserInfoConfig::new)
                .orElseThrow(()-> new BadRequestRuntimeException("UserEmail: "+email+" does not exist"));
    }
}