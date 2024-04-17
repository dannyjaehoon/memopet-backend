package com.memopet.memopet.domain.member.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.memopet.memopet.domain.member.dto.ErrorResponseDto;
import com.memopet.memopet.global.common.exception.BadCredentialsRuntimeException;
import com.memopet.memopet.global.common.exception.ForbiddenRuntimeException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {

        log.info("CustomAuthenticationEntryPoint start");
        String errorMessage;
        if (exception instanceof BadCredentialsException) {
            log.info("BadCredentialsException occurred");
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
            throw new BadCredentialsRuntimeException(errorMessage);
        } else if (exception instanceof InternalAuthenticationServiceException) {
            log.info("InternalAuthenticationServiceException occurred");
            errorMessage = "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
            throw new InternalAuthenticationServiceException(errorMessage);
        } else if (exception instanceof UsernameNotFoundException) {
            log.info("UsernameNotFoundException occurred");
            errorMessage = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
            throw new BadCredentialsRuntimeException(errorMessage);
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            log.info("AuthenticationCredentialsNotFoundException occurred");
            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
            throw new ForbiddenRuntimeException(errorMessage);
        } else {
            log.info("InternalAuthenticationServiceException occurred");
            errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
            throw new InternalAuthenticationServiceException(errorMessage);
        }

//        String responseBody = objectMapper.writeValueAsString(ErrorResponseDto.builder().status(HttpStatus.FORBIDDEN.value()).message(errorMessage).timestamp(LocalDateTime.now()).build());
//
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(responseBody);
    }
}
