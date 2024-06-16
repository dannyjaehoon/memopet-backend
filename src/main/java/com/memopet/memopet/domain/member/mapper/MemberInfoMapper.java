package com.memopet.memopet.domain.member.mapper;

import com.memopet.memopet.domain.member.dto.MemberCreationDto;
import com.memopet.memopet.domain.member.dto.SignUpRequestDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberSocial;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MemberInfoMapper {
    private final PasswordEncoder passwordEncoder;

    public Member convertToMemberEntity(SignUpRequestDto signUpRequestDto, String memberId) {
        Member member = Member.builder()
                .username(signUpRequestDto.getUsername())
                .memberId(memberId)
                .phoneNum(signUpRequestDto.getPhoneNum())
                .agreeYn(true)
                .agreeDate(LocalDateTime.now())
                .build();
        return member;
    }
    public MemberSocial convertToMemberSocialEntity(MemberCreationDto memberCreationDto) {
        MemberSocial memberSocial = MemberSocial.builder()
                .username(memberCreationDto.getUsername())
                .memberId(memberCreationDto.getMemberId())
                .phoneNum(memberCreationDto.getPhoneNum())
                .email(memberCreationDto.getEmail())
                .password(passwordEncoder.encode(memberCreationDto.getPassword()))
                .memberStatus(MemberStatus.NORMAL)
                .loginFailCount(0)
                .lastLoginDate(LocalDateTime.now())
                .roles(memberCreationDto.getRoleDscCode().equals("1") ? "ROLE_USER" : "ROLE_ADMIN")
                .build();
        return memberSocial;
    }
}
