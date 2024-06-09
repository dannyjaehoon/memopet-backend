package com.memopet.memopet.domain.pet.dto;

import com.memopet.memopet.domain.pet.entity.Blocked;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BlockedAndBlockerListResponseDto {

    private List<Blocked> petList;
    private char decCode;
    private String message;
}
