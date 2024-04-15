package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowListResponseDto {



    private boolean hasNext;

    private int currentPage;

    private int dataCounts;
    private List<PetFollowingResponseDto> followList;

    private char decCode;
    private String errorDescription;
}
