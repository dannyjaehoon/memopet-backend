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


    private boolean hasNext;

    private int currentPage;

    private int dataCounts;
    private List<BlockedListResponseDto> petList;

    private char decCode;
    private String message;
}
