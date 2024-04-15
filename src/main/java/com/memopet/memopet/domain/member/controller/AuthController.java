package com.memopet.memopet.domain.member.controller;

import com.memopet.memopet.domain.member.dto.*;
import com.memopet.memopet.domain.member.service.AuthService;
import com.memopet.memopet.domain.member.service.LoginService;
import com.memopet.memopet.global.common.dto.EmailAuthRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Tag(name = "인증", description = "인증 관련 api 입니다.")	// (1)
@Slf4j
@RestController
@RequiredArgsConstructor
public class  AuthController {

    private final AuthService authService;
    private final LoginService loginService;

    /**
     * when a user tries to log-in, this method is triggered.
     * @param loginRequestDto
     * @param response
     * @return LoginResponseDto
     */
    @Operation(summary = "로그인 메서드", description = "로그인 메서드입니다.")	// (2)
    @ApiResponses(value = {	// (3)
            // (4)
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {

        // check if the email is valid
        boolean isValidEmail = loginService.isValidEmail(loginRequestDto.getEmail());

        if(!isValidEmail) {
            ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                    .message("해당 고객은 존재하지 않습니다.")
                    .status(403).timestamp(LocalDateTime.now()).build();
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }


        // check if the account is locked
        boolean accountLock = loginService.isAccountLock(loginRequestDto.getEmail());
        if(accountLock) {
            ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                    .message("어카운트가 5회 실패로 사용불가능 합니다.")
                    .status(423).timestamp(LocalDateTime.now()).build();
            return new ResponseEntity<>(errorResponse, HttpStatus.LOCKED);
        }
        // get an authentication object to generate access and refresh token
        Authentication authentication = authService.authenicateUser(loginRequestDto);
        // generate access and refresh token
        LoginResponseDto loginResponseDto = authService.getJWTTokensAfterAuthentication(authentication,response);
        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);

    }
//
//    @PostMapping("/sign-in/password-reset")
//    public PasswordResetResponseDto resetPassword(@RequestBody EmailAuthRequestDto emailAuthRequestDto) {
//        PasswordResetResponseDto passwordResetResponseDto = loginService.checkValidEmail(emailAuthRequestDto.getEmail());
//
//        if(passwordResetResponseDto.getDscCode().equals("1")) {
//            passwordResetResponseDto = loginService.resetPassword(emailAuthRequestDto.getEmail());
//        }
//
//        return passwordResetResponseDto;
//    }

    @GetMapping("/sign-in/duplication-check")
    public DuplicationCheckResponseDto emailDuplicationCheck(DuplicationCheckRequestDto duplicationCheckRequestDto ) {
        DuplicationCheckResponseDto duplicationCheckResponseDto = loginService.checkDupplication(duplicationCheckRequestDto.getEmail());
        return duplicationCheckResponseDto;
    }

    @PostMapping("/sign-in/my-id")
    public MyIdResponseDto findMyId(@RequestBody MyIdRequestDto myIdRequestDto) {
        MyIdResponseDto myIdResponseDto = loginService.findIdByUsernameAndPhoneNum(myIdRequestDto.getUsername(), myIdRequestDto.getPhoneNum());

        return myIdResponseDto;
    }

    @PostMapping("/sign-in/my-password")
    public MyPasswordResponseDto changeMyPassword(@RequestBody MyPasswordRequestDto  myPasswordRequestDto) {
        MyPasswordResponseDto myPasswordResponseDto = loginService.saveNewPassword(myPasswordRequestDto.getEmail(), myPasswordRequestDto.getPassword());

        return myPasswordResponseDto;
    }

    /**
     * when a user tries to sign-up
     * @param signUpRequestDto
     * @param bindingResult
     * @param httpServletResponse
     * @return
     */
    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto, BindingResult bindingResult, HttpServletResponse httpServletResponse){
        boolean isValidEmail = loginService.isValidEmail(signUpRequestDto.getEmail());
        if(isValidEmail) {
            ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                    .message("이미 존재하는 아이디입니다.")
                    .status(403).timestamp(LocalDateTime.now()).build();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(authService.join(signUpRequestDto, httpServletResponse));
    }

    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping ("/refresh-token")
    public ResponseEntity<?> getAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return ResponseEntity.ok(authService.getAccessTokenUsingRefreshToken(authorizationHeader));
    }
}
