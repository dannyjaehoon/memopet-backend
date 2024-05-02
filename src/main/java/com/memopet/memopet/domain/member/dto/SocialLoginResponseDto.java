package com.memopet.memopet.domain.member.dto;

import com.memopet.memopet.domain.member.entity.MemberStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginResponseDto {

    private String username;
    private String email;
    private String accessToken;

}
