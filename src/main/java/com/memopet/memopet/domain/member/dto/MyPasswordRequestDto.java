package com.memopet.memopet.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPasswordRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
