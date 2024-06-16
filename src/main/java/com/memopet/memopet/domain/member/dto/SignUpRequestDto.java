package com.memopet.memopet.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    @NotEmpty(message = "roleDscCode must not be empty")
    private String roleDscCode;
    @NotEmpty(message = "User Name must not be empty")
    private String username;
    @NotEmpty(message = "Password must not be empty")
    private String password;
    @NotEmpty(message = "User's email must not be empty") //Neither null nor 0 size
    @Email(message = "Invalid email format")
    private String email;
    @NotEmpty(message = "phoneNum must not be empty")
    private String phoneNum;
}
