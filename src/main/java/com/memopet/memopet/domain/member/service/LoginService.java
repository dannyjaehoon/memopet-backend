package com.memopet.memopet.domain.member.service;

import com.memopet.memopet.domain.member.dto.*;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.repository.LoginFailedRepository;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.global.common.dto.EmailAuthResponseDto;
import com.memopet.memopet.global.common.exception.BadCredentialsRuntimeException;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
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
import java.util.Optional;

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
        log.info("loadUserByUsername start with Email : " + email);

        return memberRepository.findMemberByEmail(email)
                .map(UserInfoConfig::new)
                .orElseThrow(() -> {throw new UsernameNotFoundException("User not found");});
    }

    public boolean isAccountLock(String email) {
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new BadRequestRuntimeException("User Not Found");

        Member member = memberByEmail.get();

        if(member.getMemberStatus().equals(MemberStatus.LOCKED)) {
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loginAttemptCheck(String email,String password) {
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new BadRequestRuntimeException("User Not Found");

        Member member = memberByEmail.get();

        log.info("loginAttemptCheck method starts");
        if(member != null) {
            log.info("member exists");
            // 비밀번호가 맞는지 체크
            if (passwordEncoder.matches(password, member.getPassword())) {
                loginFailedRepository.resetCount(member); // 계정 잠금 후 실패 횟수 초기화
            } else {
                log.info("password incorrect");
                log.info("member.getLoginFailCount() : " + member.getLoginFailCount());
                // 비밀번호가 맞지 않으면 로그인 login_fail_count +1

                if (member.getLoginFailCount() >= MAX_ATTEMPT_COUNT) {
                    log.info("login failed attempt : " + member.getLoginFailCount());
                    changeAccountStatus(member, MemberStatus.LOCKED);
                } else {
                    member.increaseLoginFailCount(member.getLoginFailCount()+1);
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

        EmailAuthResponseDto emailAuthResponseDto = null;

        emailAuthResponseDto = emailService.sendEmail(email);

        log.info(" authCode : " + emailAuthResponseDto.getAuthCode());
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new BadRequestRuntimeException("User Not Found");

        Member member = memberByEmail.get();
        member.changePassword(passwordEncoder.encode(emailAuthResponseDto.getAuthCode()));

        PasswordResetResponseDto passwordResetResponseDto = PasswordResetResponseDto.builder().dscCode("1").errMessage("complete reset password").build();

        // unlock the account
        changeAccountStatus(member, MemberStatus.NORMAL);

        return passwordResetResponseDto;
    }


    public DuplicationCheckResponseDto checkDupplication(String email) {
        DuplicationCheckResponseDto duplicationCheckResponseDto;
        if(!isValidEmail(email)) {
            duplicationCheckResponseDto = DuplicationCheckResponseDto.builder().dscCode("1").errMessage("Email is valid").build();
        } else {
            duplicationCheckResponseDto = DuplicationCheckResponseDto.builder().dscCode("0").errMessage("Email is invalid").build();
        }
        return duplicationCheckResponseDto;
    }

    public boolean isValidEmail(String email) {
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) return false;
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
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new BadRequestRuntimeException("User Not Found");

        Member member = memberByEmail.get();
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
