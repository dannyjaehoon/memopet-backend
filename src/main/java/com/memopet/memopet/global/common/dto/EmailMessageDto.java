package com.memopet.memopet.global.common.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageDto {

    private String id;
    private String email;
    private String auth;
    private int RetryCount;     // todo 별건 아니지만 카멜케이스로 변경해주는게 좋습니다.
}