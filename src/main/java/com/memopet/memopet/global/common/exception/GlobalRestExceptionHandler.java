package com.memopet.memopet.global.common.exception;

import com.memopet.memopet.global.common.dto.RestError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.sql.ConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

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
    @ExceptionHandler(UnauthorizedRuntimeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)   // 401
    public RestError UnAuthorizedRequestException(HttpServletRequest request, UnauthorizedRuntimeException ex) {

        return new RestError("Unauthorized_request", ex.getMessage());
    }
    @ExceptionHandler(ForbiddenRuntimeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)   // 403
    public RestError ForbiddenRequestException(HttpServletRequest request, ForbiddenRuntimeException ex) {
        return new RestError("Forbidden_request", ex.getMessage());
    }
    @ExceptionHandler(NotFoundRuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)   // 404
    public RestError NotFoundRequestException(HttpServletRequest request, NotFoundRuntimeException ex) {

        return new RestError("NotFound_request", ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)   // 400
    public RestError badCredentialsRequestException(HttpServletRequest request, BadCredentialsException ex) {

        return new RestError("Bad_Credentials_request", "Password is incorrect");
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)   // 403
    public RestError badCredentialsRequestException() {

        return new RestError("Refresh_token_request", "Refresh token revoked");
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)   // 400
    public RestError UsernameNotFoundRequestException(HttpServletRequest request, UsernameNotFoundException ex) {

        return new RestError("UsernameNotFoundException", "User Not Found");
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)   // 400
    public RestError IllegalArgumentException(HttpServletRequest request, UsernameNotFoundException ex) {

        return new RestError("IllegalArgumentException", ex.getMessage());
    }
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)   // 400
    public RestError IllegalStateException(HttpServletRequest request, UsernameNotFoundException ex) {

        return new RestError("IllegalStateException", ex.getMessage());
    }
    // 처리 과정에 생기는 예외를 custom exception 으로 처리
    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<String> oAuthExceptionHandler(OAuthException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // SocialLoginType Enum에 없는 type이 요청되면 ConversionException예외 처리
    @ExceptionHandler(ConversionException.class)
    public ResponseEntity<String> conversionExceptionHandler() {
        return new ResponseEntity<>("알 수 없는 SocialLoginType 입니다.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   // 500
    public RestError internalServerException(HttpServletRequest request, Exception ex) {
        String requestInfo = String.format("Method: %s, Request URI: %s", request.getMethod(), request.getRequestURI());
        String errorMessage = String.format("서버 오류 발생 - %s - 에러 메세지 : %s", requestInfo, ex.getMessage());

        // todo Telegram 으로 전송 !!!!   429 에러...
        // todo Rate Limit 을 이해하자.... 서버를 보호하는 방법이면서, 라이선스 정책과 관련 있다. (API 사용에 대해서 유료화 가능...)
//        telegramService.sendMessage(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);

        return new RestError("server_error", errorMessage);
    }
}