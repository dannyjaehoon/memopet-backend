package com.memopet.memopet.domain.member.controller;

import com.memopet.memopet.domain.member.dto.*;
import com.memopet.memopet.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    // update member's info
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PatchMapping("/member-info")
    public MemberInfoResponseDto changeMemberInfo(@RequestBody MemberInfoRequestDto memberInfoRequestDto) {

        MemberInfoResponseDto memberInfoResponseDto = memberService.changeMemberInfo(memberInfoRequestDto);

        return memberInfoResponseDto;
    }

    // retrieve member's info
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/member-profile")
    public MemberProfileResponseDto retrieveMemberProfile(Authentication authentication) {

        MemberProfileResponseDto memberProfileResponseDto = memberService.getMemberProfile(authentication.getName());

        return memberProfileResponseDto;
    }

    // deactivate member
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/user")
    public ResponseEntity<DeactivateMemberResponseDto> deactivateMember(@RequestBody DeactivateMemberRequestDto deactivateMemberRequestDto) {

        DeactivateMemberResponseDto deactivateMemberResponseDto = memberService.deactivateMember(deactivateMemberRequestDto.getEmail(), deactivateMemberRequestDto.getDeactivationReason(), deactivateMemberRequestDto.getDeactivationReasonComment());

        return new ResponseEntity<>(deactivateMemberResponseDto, HttpStatus.OK);
    }

    //@PreAuthorize("hasAnyRole('SCOPE_USER','SCOPE_ADMIN')")
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/welcome-message")
    public ResponseEntity<String> UserMemberMessageTest(Authentication authentication) {
        return ResponseEntity.ok("Welcome to the JWT Tutorial:"+authentication.getName()+"with scope:"+authentication.getAuthorities());
    }

    //@PreAuthorize("hasRole('SCOPE_ADMIN')")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN_AUTHORITY')")
    @GetMapping("/admin-message")
    public ResponseEntity<String> getAdminData(){
        return ResponseEntity.ok("admin");
    }
}
