package com.memopet.memopet.domain.member.service;

import com.memopet.memopet.domain.member.dto.ErrorResponseDto;
import com.memopet.memopet.domain.member.dto.LoginRequestDto;
import com.memopet.memopet.domain.member.dto.LoginResponseDto;
import com.memopet.memopet.domain.member.dto.SignUpRequestDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.RefreshTokenEntity;
import com.memopet.memopet.domain.member.mapper.MemberInfoMapper;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.member.repository.RefreshTokenRepository;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.token.JwtTokenGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.AccountLockedException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService  {


    public static final int ACCESSTOKENEXPIRYTIME = 3;
    public static final int REFRESHTOKENEXPIRYTIME = 15 * 24 * 60 * 60;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final MemberInfoMapper memberInfoMapper;
    private final LoginService loginService;

    /**
     * return the user id if the sign up process is successfully completed
     * if the same email exists, it will return -2L
     *
     * @param signUpDto
     * @return user_id
     */
    @Transactional(readOnly = false)
    public LoginResponseDto join (SignUpRequestDto signUpDto, HttpServletResponse httpServletResponse)  {
        try{
            log.info("[AuthService:registerUser]User Registration Started with :::{}", signUpDto);

            Optional<Member> memberOptional = memberRepository.findMemberByEmail(signUpDto.getEmail());
            if(memberOptional.isPresent()) throw new BadRequestRuntimeException("User Already Exists");


            Member member = memberInfoMapper.convertToEntity(signUpDto);
            Authentication authentication = createAuthenticationObject(member);

            // Generate a JWT token
            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            Member savedmember = memberRepository.save(member);
            saveUserRefreshToken(savedmember,refreshToken);

            createRefreshTokenCookie(httpServletResponse,refreshToken);

            log.info("[AuthService:registerUser] User:{} Successfully registered",member.getUsername());
            return  LoginResponseDto.builder()
                    .username(savedmember.getUsername())
                    .userStatus(savedmember.getMemberStatus())
                    .userRole(savedmember.getRoles() == "ROLE_USER" ? "GU" : "SA")
                    .loginFailCount(savedmember.getLoginFailCount())
                    .accessToken(accessToken)
                    .accessTokenExpiry(ACCESSTOKENEXPIRYTIME)
                    .build();

        }catch (Exception e){
            log.error("[AuthService:registerUser]Exception while registering the user due to :"+e.getMessage());
            throw new BadRequestRuntimeException(e.getMessage());
        }
    }

    @Transactional(readOnly = false)
    public LoginResponseDto getJWTTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
        try {
            var savedmember = memberRepository.findMemberByEmail(authentication.getName())
                    .orElseThrow(()->{
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        throw new BadRequestRuntimeException("USER NOT FOUND");
                    });

            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            createRefreshTokenCookie(response,refreshToken);

            saveUserRefreshToken(savedmember,refreshToken);
            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated",savedmember.getUsername());
            return  LoginResponseDto.builder()
                                    .username(savedmember.getUsername())
                                    .userStatus(savedmember.getMemberStatus())
                                    .userRole(savedmember.getRoles().equals("ROLE_USER") ? "GU" : "SA")
                                    .loginFailCount(savedmember.getLoginFailCount())
                                    .accessToken(accessToken)
                                    .accessTokenExpiry(ACCESSTOKENEXPIRYTIME)
                                    .build();
        } catch(Exception e) {
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :" + e.getMessage());
            throw new BadRequestRuntimeException("An error occurred during the authentication process. Please Try Again");
        }
    }

    @Transactional(readOnly = false)
    public void saveUserRefreshToken(Member member, String refreshToken) {
        var refreshTokenEntity = RefreshTokenEntity.builder()
                .member(member)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);
    }

    public Cookie createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token",refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(REFRESHTOKENEXPIRYTIME); // in seconds
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;
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
                                .accessTokenExpiry(ACCESSTOKENEXPIRYTIME)
                                .build();

    }
    public static Authentication createAuthenticationObject(Member member) {
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
        boolean isValidEmail = loginService.isValidEmail(loginRequestDto.getEmail());
        if(!isValidEmail) throw new UsernameNotFoundException("User not found");

        // check if the account is locked
        boolean accountLock = loginService.isAccountLock(loginRequestDto.getEmail());
        if(accountLock) throw new BadRequestRuntimeException("Your account is locked because of 5 failed Login attempts");


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
