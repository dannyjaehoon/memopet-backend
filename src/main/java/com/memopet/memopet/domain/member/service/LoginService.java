package com.memopet.memopet.domain.member.service;

import com.memopet.memopet.domain.member.dto.DuplicationCheckResponseDto;
import com.memopet.memopet.domain.member.dto.MyIdResponseDto;
import com.memopet.memopet.domain.member.dto.MyPasswordResponseDto;
import com.memopet.memopet.domain.member.dto.ResetPasswordResponseDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberSocial;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.repository.LoginFailedRepository;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.member.repository.MemberSocialRepository;
import com.memopet.memopet.global.common.dto.EmailAuthResponseDto;
import com.memopet.memopet.global.common.entity.Audit;
import com.memopet.memopet.global.common.repository.AuditRepository;
import com.memopet.memopet.global.common.service.EmailService;
import com.memopet.memopet.global.config.UserInfoConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class LoginService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberSocialRepository memberSocialRepository;
    private final LoginFailedRepository loginFailedRepository;
    private final AuditRepository auditRepository;
    private final EmailService emailService;
    public static final int MAX_ATTEMPT_COUNT = 4;

    @Override
    // 로그인시에 DB에서 유저정보와 권한정보를 가져와서 해당 정보를 기반으로 userdetails.User 객체를 생성해 리턴
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername start with Email : " + email);

        return memberSocialRepository.findMemberByEmail(email)
                .map(UserInfoConfig::new)
                .orElseThrow(() -> {throw new UsernameNotFoundException("User not found");});
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loginAttemptCheck(String email,String password) {
        Optional<MemberSocial> memberSocialByEmail = memberSocialRepository.findMemberByEmail(email);

        if(memberSocialByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");

        MemberSocial memberSocial = memberSocialByEmail.get();

        log.info("loginAttemptCheck method starts");
        // check if the input password is correct
        if (passwordEncoder.matches(password, memberSocial.getPassword())) {
            loginFailedRepository.resetCount(memberSocial); // reset the count of login failure attempts
        } else {
            log.info("member.getLoginFailCount() : " + memberSocial.getLoginFailCount());
            // login failure +1

            if (memberSocial.getLoginFailCount() >= MAX_ATTEMPT_COUNT) {
                log.info("login failed attempt : " + memberSocial.getLoginFailCount());
                changeAccountStatus(MemberStatus.LOCKED, memberSocial);

                Audit audit = Audit.builder().createdDate(LocalDateTime.now()).cnbf("account is active").cnaf("account is locked").modifier(memberSocial.getEmail()).build();
                // save audit log
                auditRepository.save(audit);
            } else {
                memberSocial.increaseLoginFailCount(memberSocial.getLoginFailCount()+1);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void changeAccountStatus(MemberStatus memberStatus, MemberSocial memberSocial) {
        loginFailedRepository.changeMemberStatusAndActivation(memberSocial, memberStatus);
        loginFailedRepository.resetCount(memberSocial); // 계정 잠금 후 실패 횟수 초기화
    }



    public DuplicationCheckResponseDto checkDupplication(String email) {
        DuplicationCheckResponseDto duplicationCheckResponseDto;
        Optional<MemberSocial> memberByEmail = memberSocialRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) {
            duplicationCheckResponseDto = DuplicationCheckResponseDto.builder().dscCode("1").errMessage("Email is valid").build();
        } else {
            duplicationCheckResponseDto = DuplicationCheckResponseDto.builder().dscCode("0").errMessage("Email is invalid").build();
        }
        return duplicationCheckResponseDto;
    }

    public MyIdResponseDto findIdByUsernameAndPhoneNum(String username, String phoneNum) {
        Optional<MemberSocial> memberSocialOptional = memberSocialRepository.findIdByUsernameAndPhoneNum(username, phoneNum);
        MyIdResponseDto myIdResponseDto;

        if(memberSocialOptional.isEmpty()) {
            myIdResponseDto = MyIdResponseDto.builder().dscCode("0").build();

        } else {
            MemberSocial memberSocial = memberSocialOptional.get();

            if(memberSocial.getProviderId() == null) {
                myIdResponseDto = MyIdResponseDto.builder().dscCode("1").email(memberSocial.getEmail()).build();
            } else {
                myIdResponseDto = MyIdResponseDto.builder().dscCode("2").email(memberSocial.getEmail()).build();
            }
        }
        return myIdResponseDto;
    }

    public MyPasswordResponseDto saveNewPassword(String email, String password) {
        Optional<MemberSocial> memberSocialByEmailOptional = memberSocialRepository.findMemberByEmail(email);
        if(memberSocialByEmailOptional.isEmpty()) throw new UsernameNotFoundException("User Not Found");

        MemberSocial memberSocial = memberSocialByEmailOptional.get();

        memberSocial.changePassword(passwordEncoder.encode(password));
        MyPasswordResponseDto myPasswordResponseDto = MyPasswordResponseDto.builder().dscCode("1").build();

        return myPasswordResponseDto;
    }

    public ResetPasswordResponseDto resetNewPassword(String email) {
        Optional<MemberSocial> memberSocialByEmailOptional = memberSocialRepository.findMemberByEmail(email);
        if(memberSocialByEmailOptional.isEmpty()) throw new UsernameNotFoundException("User Not Found");

        MemberSocial memberSocial = memberSocialByEmailOptional.get();

        EmailAuthResponseDto emailAuthResponseDto = emailService.sendEmail(memberSocial.getEmail());

        memberSocial.changePassword(passwordEncoder.encode(emailAuthResponseDto.getAuthCode()));

        ResetPasswordResponseDto myPasswordResponseDto = ResetPasswordResponseDto.builder().dscCode("1").newPassword(emailAuthResponseDto.getAuthCode()).build();

        return myPasswordResponseDto;
    }
}
