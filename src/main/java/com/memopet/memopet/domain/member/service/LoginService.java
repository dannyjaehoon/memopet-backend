package com.memopet.memopet.domain.member.service;

import com.memopet.memopet.domain.member.dto.*;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.repository.LoginFailedRepository;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.global.common.service.EmailService;
import com.memopet.memopet.global.config.SecurityConfig;
import com.memopet.memopet.global.config.UserInfoConfig;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import javax.security.auth.login.AccountLockedException;

import java.io.UnsupportedEncodingException;

import static com.memopet.memopet.domain.member.entity.QMember.member;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class LoginService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final LoginFailedRepository loginFailedRepository;
    public static final int MAX_ATTEMPT_COUNT = 4;

    @Override
    // 로그인시에 DB에서 유저정보와 권한정보를 가져와서 해당 정보를 기반으로 userdetails.User 객체를 생성해 리턴
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername start with Email : " + email);

        return memberRepository.findOptionalMemberByEmail(email)
                .map(UserInfoConfig::new)
                .orElseThrow(() -> {throw new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다.");});
    }

    public boolean isAccountLock(String email) {
        Member member = memberRepository.findByEmail(email);
        if(member.getMemberStatus().equals(MemberStatus.LOCKED)) {
            return true;
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loginAttemptCheck(String email,String password) {
        Member member  = memberRepository.findByEmail(email);

        if(member != null) {
            // 비밀번호가 맞는지 체크
            if (passwordEncoder.matches(password, member.getPassword())) {
                loginFailedRepository.resetCount(member); // 계정 잠금 후 실패 횟수 초기화
            } else {
                // 비밀번호가 맞지 않으면 로그인 login_fail_count +1
                if (member.getLoginFailCount() >= MAX_ATTEMPT_COUNT) {
                    changeAccountStatus(member, MemberStatus.LOCKED);

                } else {
                    System.out.println(loginFailedRepository.increment(member));
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void changeAccountStatus(Member member, MemberStatus memberStatus) {
        loginFailedRepository.changeMemberStatusAndActivation(member, memberStatus);
        loginFailedRepository.resetCount(member); // 계정 잠금 후 실패 횟수 초기화
    }


    public PasswordResetResponseDto resetPassword(String email) {

        String authCode = null;
        try {
            authCode = emailService.sendEmail(email);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(" authCode : " + authCode);
        Member member = memberRepository.findByEmail(email);
        member.changePassword(passwordEncoder.encode(authCode));

        PasswordResetResponseDto passwordResetResponseDto = PasswordResetResponseDto.builder().dscCode("1").errMessage("complete reset password").build();

        // unlock the account
        changeAccountStatus(member, MemberStatus.NORMAL);

        return passwordResetResponseDto;
    }

    public PasswordResetResponseDto checkValidEmail(String email) {
        PasswordResetResponseDto passwordResetResponseDto;
        if(isValidEmail(email)) {
            passwordResetResponseDto = PasswordResetResponseDto.builder().dscCode("1").errMessage("Email is valid").build();
        } else {
            passwordResetResponseDto = PasswordResetResponseDto.builder().dscCode("0").errMessage("Email is not valid").build();
        }
        return passwordResetResponseDto;
    }

    public DuplicationCheckResponseDto checkDupplication(String email) {
        DuplicationCheckResponseDto duplicationCheckResponseDto;
        if(!isValidEmail(email)) {
            duplicationCheckResponseDto = DuplicationCheckResponseDto.builder().dscCode("1").errMessage("사용가능한 이메일 입니다").build();
        } else {
            duplicationCheckResponseDto = DuplicationCheckResponseDto.builder().dscCode("0").errMessage("이미 저장된 이메일입니다").build();
        }
        return duplicationCheckResponseDto;
    }

    public boolean isValidEmail(String email) {
        Member member  = memberRepository.findByEmail(email);
        if(member == null) return false;
        return true;
    }



    public MyIdResponseDto findIdByUsernameAndPhoneNum(String username, String phoneNum) {

        Member member = memberRepository.findIdByUsernameAndPhoneNum(username, phoneNum);
        MyIdResponseDto myIdResponseDto;
        if(member == null) {
            myIdResponseDto = MyIdResponseDto.builder().dscCode("0").build();
        } else if(member.getProvideId() == null) {
            myIdResponseDto = MyIdResponseDto.builder().dscCode("1").email(member.getEmail()).build();
        } else {
            myIdResponseDto = MyIdResponseDto.builder().dscCode("2").email(member.getEmail()).build();
        }
        return myIdResponseDto;
    }

    public MyPasswordResponseDto saveNewPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        MyPasswordResponseDto myPasswordResponseDto;
        if(member != null) {
            member.changePassword(passwordEncoder.encode(password));
            myPasswordResponseDto =MyPasswordResponseDto.builder().dscCode("1").build();
        } else {
            myPasswordResponseDto = MyPasswordResponseDto.builder().dscCode("0").build();
        }

        return myPasswordResponseDto;
    }
}
