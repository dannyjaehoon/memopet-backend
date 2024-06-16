package com.memopet.memopet.domain.member.controller;

import com.memopet.memopet.domain.member.dto.*;
import com.memopet.memopet.domain.member.service.AuthService;
import com.memopet.memopet.domain.member.service.LoginService;
import com.memopet.memopet.global.common.dto.RestResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "인증", description = "인증 관련 api 입니다.")	// (1)
@Slf4j
@RestController
@RequiredArgsConstructor
public class  AuthController {

    private final AuthService authService;
    private final LoginService loginService;

    /**
     * when a user tries to log-in, this method is triggered.
     * @param loginRequestDto
     * @param response
     * @return LoginResponseDto
     */
    @PostMapping("/sign-in")
    public RestResult authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        log.info("sign-in start");
        // get an authentication object to generate access and refresh token
        Authentication authentication = authService.authenticateUser(loginRequestDto);
        // generate access and refresh token
        LoginResponseDto loginResponseDto = authService.getJWTTokensAfterAuthentication(authentication);

        response.setHeader("Authorization", "Bearer " + loginResponseDto.getAccessToken());

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("loginInfo", loginResponseDto);
        return new RestResult(dataMap);

    }

    @GetMapping("/sign-in/duplication-check")
    public RestResult emailDuplicationCheck(DuplicationCheckRequestDto duplicationCheckRequestDto ) {
        DuplicationCheckResponseDto duplicationCheckResponseDto = loginService.checkDupplication(duplicationCheckRequestDto.getEmail());

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("duplicationCheckResponse", duplicationCheckResponseDto);

        return new RestResult(dataMap);
    }

    @PostMapping("/sign-in/my-id")
    public RestResult findMyId(@RequestBody MyIdRequestDto myIdRequestDto) {
        MyIdResponseDto myIdResponseDto = loginService.findIdByUsernameAndPhoneNum(myIdRequestDto.getUsername(), myIdRequestDto.getPhoneNum());
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("findMyIdResponse", myIdResponseDto);

        return new RestResult(dataMap);
    }

    @PostMapping("/sign-in/my-password")
    public RestResult changeMyPassword(@RequestBody MyPasswordRequestDto  myPasswordRequestDto) {
        MyPasswordResponseDto myPasswordResponseDto = loginService.saveNewPassword(myPasswordRequestDto.getEmail(), myPasswordRequestDto.getPassword());

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("changeMyPasswordResponse", myPasswordResponseDto);

        return new RestResult(dataMap);
    }

    @PostMapping("/sign-in/password-reset")
    public RestResult resetMyPassword(@RequestBody MyPasswordRequestDto  myPasswordRequestDto) {
        ResetPasswordResponseDto resetPasswordResponseDto = loginService.resetNewPassword(myPasswordRequestDto.getEmail());

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("changeMyPasswordResponse", resetPasswordResponseDto);

        return new RestResult(dataMap);
    }

    /**
     * when a user tries to sign-up
     * @param signUpRequestDto
     * @return
     */
    @PostMapping("/sign-up")
    public RestResult registerUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto, HttpServletResponse response){
        log.info("sign-up start");
        LoginResponseDto loginResponseDto = authService.join(signUpRequestDto);

        response.setHeader("Authorization", "Bearer " + loginResponseDto.getAccessToken());

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("sigupInfo", loginResponseDto);
        return new RestResult(dataMap);
    }


}
