package com.memopet.memopet.domain.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordResponseDto {

    private String dscCode;
    private String newPassword;
}
