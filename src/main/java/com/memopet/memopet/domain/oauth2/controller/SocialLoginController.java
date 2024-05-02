package com.memopet.memopet.domain.oauth2.controller;


import com.memopet.memopet.domain.member.dto.LoginResponseDto;
import com.memopet.memopet.domain.member.dto.SocialLoginResponseDto;
import com.memopet.memopet.domain.oauth2.service.OauthService;
import com.memopet.memopet.global.common.dto.RestResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SocialLoginController {
    private final OauthService oauthService;
    @GetMapping("/login/oauth2/code/{socialLoginType}")
    public RestResult socialLogin(@PathVariable(name = "socialLoginType") String socialLoginType, @RequestParam(name = "code") String code, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SocialLoginResponseDto socialLoginResponseDto = oauthService.oAuthLogin(socialLoginType, code, request,response);
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("loginResponseDto", "loginResponseDto");

        // redirect -> front
//        String strUsername = URLEncoder.encode(socialLoginResponseDto.getUsername(), "UTF-8");
//        String url = "http://localhost:3000/login?username="+ strUsername +"&email=" + socialLoginResponseDto.getEmail();
//
//        response.sendRedirect(url);

        return new RestResult(dataMap);
    }
}
