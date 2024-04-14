package com.memopet.memopet.global.exception;

import com.memopet.memopet.global.common.dto.RestError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalRestExceptionHandler {

    // custom exception 처리
    @ExceptionHandler(BadRequestRuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)   // 400
    public RestError badRequestException(HttpServletRequest request, BadRequestRuntimeException ex) {

        return new RestError("bad_request", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   // 500
    public RestError exception(HttpServletRequest request, Exception ex) {
        String requestInfo = String.format("Method: %s, Request URI: %s", request.getMethod(), request.getRequestURI());
        String errorMessage = String.format("서버 오류 발생 - %s - 에러 메세지 : %s", requestInfo, ex.getMessage());

        // todo Telegram 으로 전송 !!!!   429 에러...
        // todo Rate Limit 을 이해하자.... 서버를 보호하는 방법이면서, 라이선스 정책과 관련 있다. (API 사용에 대해서 유료화 가능...)
//        telegramService.sendMessage(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);

        return new RestError("server_error", "서버에러 입니다.");
    }
}