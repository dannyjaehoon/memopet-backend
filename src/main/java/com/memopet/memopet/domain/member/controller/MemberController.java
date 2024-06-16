package com.memopet.memopet.domain.member.controller;

import com.memopet.memopet.domain.member.dto.*;
import com.memopet.memopet.domain.member.service.MemberService;
import com.memopet.memopet.global.common.dto.RestResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    // update member's info
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PatchMapping("/member-info")
    public RestResult changeMemberInfo(@RequestBody @Valid MemberInfoRequestDto memberInfoRequestDto) {
        MemberInfoResponseDto memberInfoResponseDto = memberService.changeMemberInfo(memberInfoRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("changeMemberInfoResponse", memberInfoResponseDto);

        return new RestResult(dataMap);
    }

    // retrieve member's info
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/member-profile")
    public RestResult retrieveMemberProfile(Authentication authentication) {
        MemberProfileResponseDto memberProfileResponseDto = memberService.getMemberProfile(authentication.getName());

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("retrieveMemberProfileResponse", memberProfileResponseDto);

        return new RestResult(dataMap);
    }

    // deactivate member
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/user")
    public RestResult deactivateMember(@RequestBody DeactivateMemberRequestDto deactivateMemberRequestDto) {
        DeactivateMemberResponseDto deactivateMemberResponseDto = memberService.deactivateMember(deactivateMemberRequestDto.getEmail(), deactivateMemberRequestDto.getDeactivationReason(), deactivateMemberRequestDto.getDeactivationReasonComment());

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("deactivateMemberResponse", deactivateMemberResponseDto);

        return new RestResult(dataMap);
    }
}
