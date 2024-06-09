package com.memopet.memopet.domain.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class BlockedResponseDto {
    private char decCode;

    private String message;

}
