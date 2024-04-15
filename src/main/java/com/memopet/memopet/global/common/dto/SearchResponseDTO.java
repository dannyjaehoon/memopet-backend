package com.memopet.memopet.global.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.pet.dto.NotificationResponseDto;
import com.memopet.memopet.domain.pet.dto.SearchMemoryCommentResponseDto;
import com.memopet.memopet.domain.pet.dto.SearchPetCommentResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDTO {

    private int currentPage;

    private int dataCounts;

    private boolean hasNext;
    private List<SearchMemoryCommentResponseDto> searchMemoryCommentResponseDtos;

    private int currentPage2;

    private int dataCounts2;

    private boolean hasNext2;
    private List<SearchPetCommentResponseDto> searchPetCommentResponseDtos;

}
