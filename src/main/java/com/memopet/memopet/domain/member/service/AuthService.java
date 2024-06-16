package com.memopet.memopet.domain.member.service;

import com.memopet.memopet.domain.member.dto.LoginRequestDto;
import com.memopet.memopet.domain.member.dto.LoginResponseDto;
import com.memopet.memopet.domain.member.dto.MemberCreationDto;
import com.memopet.memopet.domain.member.dto.SignUpRequestDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberSocial;
import com.memopet.memopet.domain.member.entity.RefreshToken;
import com.memopet.memopet.domain.member.mapper.MemberInfoMapper;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.member.repository.MemberSocialRepository;
import com.memopet.memopet.domain.member.repository.RefreshTokenRepository;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.service.MemberCreationRabbitPublisher;
import com.memopet.memopet.global.common.utils.BusinessUtil;
import com.memopet.memopet.global.token.JwtTokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService  {

    private final MemberRepository memberRepository;
    private final MemberSocialRepository memberSocialRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final MemberInfoMapper memberInfoMapper;
    private final LoginService loginService;
    private final BusinessUtil businessUtil;
    private final MemberCreationRabbitPublisher memberCreationRabbitPublisher;

    /**
     * return the user id if the sign up process is successfully completed
     * if the same email exists, it will return -2L
     *
     * @param signUpDto
     * @return user_id
     */
    @Transactional(readOnly = false)
    public LoginResponseDto join (SignUpRequestDto signUpDto)  {

        log.info("[AuthService:registerUser]User Registration Started with :::{}", signUpDto.getEmail());

        Optional<MemberSocial> memberSocialByEmail = memberSocialRepository.findMemberByEmail(signUpDto.getEmail());
        if(memberSocialByEmail.isPresent()) throw new BadRequestRuntimeException("User Already Exists");

        // RabbitMQ 로 채번
        String memberId = memberCreationRabbitPublisher.pubsubMessage();

        // check if Member Entity does not exist
        Optional<Member> memberByPhoneNum = memberRepository.findMemberByPhoneNum(signUpDto.getPhoneNum());

        if(memberByPhoneNum.isEmpty()) {
            Member member = memberInfoMapper.convertToMemberEntity(signUpDto, memberId);
            memberRepository.save(member);
        }

        MemberCreationDto memberCreationDto = MemberCreationDto.builder()
                .email(signUpDto.getEmail())
                .username(signUpDto.getUsername())
                .phoneNum(signUpDto.getPhoneNum())
                .password(signUpDto.getPassword())
                .memberId(memberId)
                .roleDscCode(signUpDto.getRoleDscCode())
                .build();

        MemberSocial memberSocial = memberInfoMapper.convertToMemberSocialEntity(memberCreationDto);

        memberSocialRepository.save(memberSocial);

        Authentication authentication = createAuthenticationObject(memberSocial);

        // Generate a JWT token
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        saveRefreshToken(memberSocial, accessToken);

        log.info("[AuthService:registerUser] User:{} Successfully registered",memberSocial.getUsername());
        return  LoginResponseDto.builder()
                .username(memberSocial.getUsername())
                .userStatus(memberSocial.getMemberStatus())
                .userRole(memberSocial.getRoles() == "ROLE_USER" ? "GU" : "SA")
                .loginFailCount(memberSocial.getLoginFailCount())
                .phoneNumYn(memberSocial.getPhoneNum().isEmpty() ? "Y" : "N")
                .accessToken(accessToken)
                .build();
    }


    @Transactional(readOnly = false)
    public LoginResponseDto getJWTTokensAfterAuthentication(Authentication authentication) {
        MemberSocial savedmember = businessUtil.getValidEmail(authentication.getName());
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        saveRefreshToken(savedmember, accessToken);
        log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated",savedmember.getUsername());
        return  LoginResponseDto.builder()
                                .username(savedmember.getUsername())
                                .userStatus(savedmember.getMemberStatus())
                                .userRole(savedmember.getRoles().equals("ROLE_USER") ? "GU" : "SA")
                                .loginFailCount(savedmember.getLoginFailCount())
                                .accessToken(accessToken)
                                .build();
    }
    public void saveRefreshToken(MemberSocial memberSocial, String accessToken) {
        RefreshToken refreshToken = jwtTokenGenerator.generateRefreshToken(memberSocial.getEmail());
        refreshToken.setAccessToken(accessToken);
        refreshToken.setMemberSocial(memberSocial);
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
    }



    public Authentication createAuthenticationObject(MemberSocial memberInfo) {
        // Extract user details from UserDetailsEntity
        String username = memberInfo.getEmail();
        String password = memberInfo.getPassword();
        String roles = memberInfo.getRoles();

        // Extract authorities from roles (comma-separated)
        String[] roleArray = roles.split(",");
        GrantedAuthority[] authorities = Arrays.stream(roleArray)
                .map(role -> (GrantedAuthority) role::trim)
                .toArray(GrantedAuthority[]::new);

        return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(authorities));
    }

    public Authentication authenticateUser(LoginRequestDto loginRequestDto) {

        log.info("authenticateUser method starts");
        // check if the email is valid
        businessUtil.isValidEmail(loginRequestDto.getEmail());

        // check if the account is locked
        businessUtil.isAccountLock(loginRequestDto.getEmail());

        loginService.loginAttemptCheck(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        // Created the authentication token
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());


        // when this line of code executes, it will call the loadUserByUsername method in AuthService.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // save authentication object in the SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }
}
