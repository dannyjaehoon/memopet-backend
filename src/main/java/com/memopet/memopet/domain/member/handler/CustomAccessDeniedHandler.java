package com.memopet.memopet.domain.member.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.memopet.memopet.domain.member.dto.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        String errorMessage = "권한이 없습니다.";


        String responseBody = objectMapper.writeValueAsString(ErrorResponseDto.builder().status(HttpStatus.FORBIDDEN.value()).message(errorMessage).timestamp(LocalDateTime.now()).build());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
