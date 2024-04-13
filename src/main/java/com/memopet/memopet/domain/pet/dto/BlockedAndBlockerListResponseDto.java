package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.pet.entity.Blocked;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BlockedAndBlockerListResponseDto {

    private List<Blocked> petList;
    @JsonProperty("dec_code")
    private char decCode;
    private String message;
}