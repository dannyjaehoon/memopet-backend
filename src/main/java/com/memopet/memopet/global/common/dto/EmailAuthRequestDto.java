package com.memopet.memopet.global.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailAuthRequestDto {
    @Email
    @NotBlank
    private String email;
    private String confirmCode;
    private long verificationStatusId;
}
