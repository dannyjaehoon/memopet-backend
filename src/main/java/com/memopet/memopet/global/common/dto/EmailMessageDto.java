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
    private int retryCount;
    private String reason;
}