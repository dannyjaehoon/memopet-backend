package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class BlockListWrapper {
    private Page<BlockedListResponseDto> petList;
    @JsonProperty("dec_code")
    private char decCode;
    private String message;
}
