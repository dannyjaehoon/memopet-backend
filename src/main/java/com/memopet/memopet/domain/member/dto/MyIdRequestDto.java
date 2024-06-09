package com.memopet.memopet.domain.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyIdRequestDto {
    private String username;
    private String phoneNum;
}
