package com.memopet.memopet.global.common.controller;

import com.memopet.memopet.global.common.dto.EmailAuthRequestDto;
import com.memopet.memopet.global.common.dto.EmailAuthResponseDto;
import com.memopet.memopet.global.common.dto.RestResult;
import com.memopet.memopet.global.common.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/sign-in/verification")
    public RestResult sendVerificationEmail(@RequestBody EmailAuthRequestDto emailDto) {

        Map<String,Object> hashMap = new LinkedHashMap<>();
        hashMap.put("response", emailService.sendEmail(emailDto.getEmail()));

        return new RestResult(hashMap);
    }

    @PostMapping("/sign-in/verification-email")
    public RestResult checkVerificationCode(@RequestBody EmailAuthRequestDto emailAuthRequestDto) {

        EmailAuthResponseDto emailAuthResponseDto = emailService.checkVerificationCode(emailAuthRequestDto);
        Map<String,Object> hashMap = new LinkedHashMap<>();
        hashMap.put("response", emailAuthResponseDto);

        return new RestResult(hashMap);
    }

}
