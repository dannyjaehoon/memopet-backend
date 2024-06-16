package com.memopet.memopet.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCreationDto {
    private String username;
    private String password;
    private String email;
    private String phoneNum;
    private String roleDscCode;
    private String memberId;
}
