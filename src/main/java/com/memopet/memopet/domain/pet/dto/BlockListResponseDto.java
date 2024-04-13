package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.pet.entity.Blocked;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class BlockListResponseDto {

    @JsonProperty("has_next")
    private boolean hasNext;
    @JsonProperty("current_page")
    private int currentPage;
    @JsonProperty("data_counts")
    private int dataCounts;
    private List<BlockedListResponseDto> petList;
    @JsonProperty("dec_code")
    private char decCode;
    private String message;
}
