package com.memopet.memopet.domain.member.service;

import com.memopet.memopet.domain.member.dto.LoginRequestDto;
import com.memopet.memopet.domain.member.dto.LoginResponseDto;
import com.memopet.memopet.domain.member.dto.SignUpRequestDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.RefreshToken;
import com.memopet.memopet.domain.member.mapper.MemberInfoMapper;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.member.repository.RefreshTokenRepository;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService  {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final MemberInfoMapper memberInfoMapper;
    private final LoginService loginService;
    private final BusinessUtil businessUtil;

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

        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(signUpDto.getEmail());
        if(memberByEmail.isPresent()) throw new BadRequestRuntimeException("User Already Exists");

        Member member = memberInfoMapper.convertToEntity(signUpDto);
        Authentication authentication = createAuthenticationObject(member);

        // Generate a JWT token
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        Member savedmember = memberRepository.save(member);
        saveRefreshToken(savedmember, accessToken);
        log.info("[AuthService:registerUser] User:{} Successfully registered",member.getUsername());
        return  LoginResponseDto.builder()
                .username(savedmember.getUsername())
                .userStatus(savedmember.getMemberStatus())
                .userRole(savedmember.getRoles() == "ROLE_USER" ? "GU" : "SA")
                .loginFailCount(savedmember.getLoginFailCount())
                .accessToken(accessToken)
                .build();
    }


    public LoginResponseDto getJWTTokensAfterAuthentication(Authentication authentication) {
        Member savedmember = businessUtil.getValidEmail(authentication.getName());
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

    @Transactional(readOnly = false)
    public void saveRefreshToken(Member member, String accessToken) {
        RefreshToken refreshToken = jwtTokenGenerator.generateRefreshToken(member.getEmail());
        refreshToken.setAccessToken(accessToken);
        refreshToken.setMember(member);
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
    }

    public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {
        if(!authorizationHeader.startsWith("Bearer")){
            throw new BadRequestRuntimeException("Please verify your token type");
        }

        final String refreshToken = authorizationHeader.substring(7);

        //Find refreshToken from database and should not be revoked : Same thing can be done through filter.
        var refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .filter(tokens-> !tokens.isRevoked())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked"));

        Member savedmember = refreshTokenEntity.getMember();

        //Now create the Authentication object
        Authentication authentication =  createAuthenticationObject(savedmember);

        //Use the authentication object to generate new accessToken as the Authentication object that we will have may not contain correct role.
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        return  LoginResponseDto.builder()
                                .username(savedmember.getUsername())
                                .userStatus(savedmember.getMemberStatus())
                                .userRole(savedmember.getRoles().equals("ROLE_USER") ? "GU" : "SA")
                                .loginFailCount(savedmember.getLoginFailCount())
                                .accessToken(accessToken)
                                .build();

    }
    public Authentication createAuthenticationObject(Member member) {
        // Extract user details from UserDetailsEntity
        String username = member.getEmail();
        String password = member.getPassword();
        String roles = member.getRoles();

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
