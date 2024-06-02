package com.memopet.memopet.domain.member.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoRequestDto {

    @NotEmpty(message = "Email must not be empty")
    private String email;
    private String username;
    private String password;
    private String phoneNum;

}
