package com.memopet.memopet.domain.pet.dto;

import lombok.Builder;
import lombok.Getter;

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
