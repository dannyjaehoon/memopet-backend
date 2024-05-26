package com.memopet.memopet.domain.member.dto;

import com.memopet.memopet.domain.member.entity.MemberStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private String username;

    private MemberStatus userStatus;

    private String userRole;

    private int loginFailCount;

    private String accessToken;

    private int accessTokenExpiry;

}
