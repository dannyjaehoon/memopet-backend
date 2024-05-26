package com.memopet.memopet.domain.member.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {

    private int status;

    private LocalDateTime timestamp;

    private String message;
}
