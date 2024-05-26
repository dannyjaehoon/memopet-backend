package com.memopet.memopet.domain.member.service;

import com.memopet.memopet.domain.member.dto.DuplicationCheckResponseDto;
import com.memopet.memopet.domain.member.dto.MyIdResponseDto;
import com.memopet.memopet.domain.member.dto.MyPasswordResponseDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.repository.LoginFailedRepository;
import com.memopet.memopet.domain.member.repository.MemberRepository;
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

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class LoginService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loginAttemptCheck(String email,String password) {
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");

        Member member = memberByEmail.get();

        log.info("loginAttemptCheck method starts");
        if(member != null) {
            // 비밀번호가 맞는지 체크
            if (passwordEncoder.matches(password, member.getPassword())) {
                loginFailedRepository.resetCount(member); // 계정 잠금 후 실패 횟수 초기화
            } else {
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



    public DuplicationCheckResponseDto checkDupplication(String email) {
        DuplicationCheckResponseDto duplicationCheckResponseDto;
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if(memberByEmail.isPresent()) {
            duplicationCheckResponseDto = DuplicationCheckResponseDto.builder().dscCode("1").errMessage("Email is valid").build();
        } else {
            duplicationCheckResponseDto = DuplicationCheckResponseDto.builder().dscCode("0").errMessage("Email is invalid").build();
        }
        return duplicationCheckResponseDto;
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
        if(memberByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");

        Member member = memberByEmail.get();
        MyPasswordResponseDto myPasswordResponseDto;

        member.changePassword(passwordEncoder.encode(password));
        myPasswordResponseDto = MyPasswordResponseDto.builder().dscCode("1").build();

        return myPasswordResponseDto;
    }
}
