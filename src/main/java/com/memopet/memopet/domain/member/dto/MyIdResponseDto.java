package com.memopet.memopet.domain.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyIdResponseDto {

    private String dscCode;
    private String email;
}
