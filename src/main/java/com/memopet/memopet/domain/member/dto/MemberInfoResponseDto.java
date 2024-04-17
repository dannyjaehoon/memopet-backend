package com.memopet.memopet.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponseDto {

    private String email;
    private String username;
    private String phoneNum;

}
