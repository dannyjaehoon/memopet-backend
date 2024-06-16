package com.memopet.memopet.domain.member.service;

import com.memopet.memopet.domain.member.entity.RefreshToken;
import com.memopet.memopet.domain.member.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutHandlerService implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepo;

    @Override
    @Transactional(readOnly = false)
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(!authHeader.startsWith("Bearer")){
            return;
        }

        final String accessToken = authHeader.substring(7);
        Optional<RefreshToken> storedRefreshToken = refreshTokenRepo.findByAccessToken(accessToken);

        RefreshToken refreshToken = storedRefreshToken.get();

        refreshToken.setRevoked(true);

        refreshTokenRepo.save(refreshToken);


    }
}