package com.memopet.memopet.domain.member.mapper;

import com.memopet.memopet.domain.member.dto.MemberCreationDto;
import com.memopet.memopet.domain.member.dto.SignUpRequestDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberInfoMapper {
    private final PasswordEncoder passwordEncoder;
    public Member convertToEntity(SignUpRequestDto signUpRequestDto) {
        Member member = Member.builder()
                    .username(signUpRequestDto.getUsername())
                    .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                    .email(signUpRequestDto.getEmail())
                    .phoneNum(signUpRequestDto.getPhoneNum())
                    .memberStatus(MemberStatus.NORMAL)
                    .loginFailCount(0)
                    .roles("ROLE_USER")
                    .activated(true)
                    .build();
        return member;
    }
    public Member convertToEntity(MemberCreationDto memberCreationDto) {
        Member member = Member.builder()
                .username(memberCreationDto.getUsername())
                .password(passwordEncoder.encode(memberCreationDto.getPassword()))
                .memberId(memberCreationDto.getMemberId())
                .email(memberCreationDto.getEmail())
                .phoneNum(memberCreationDto.getPhoneNum())
                .memberStatus(MemberStatus.NORMAL)
                .loginFailCount(0)
                .roles("ROLE_USER")
                .activated(true)
                .build();
        return member;
    }
}
