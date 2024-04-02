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
    @JsonProperty("current_page")
    private int currentPage;
    @JsonProperty("data_counts")
    private int dataCounts;
    @JsonProperty("has_next")
    private boolean hasNext;
    @JsonProperty("search_memory_comment_response_dto")
    private List<SearchMemoryCommentResponseDto> searchMemoryCommentResponseDtos;

    @JsonProperty("current_page_2")
    private int currentPage2;
    @JsonProperty("data_counts_2")
    private int dataCounts2;
    @JsonProperty("has_next_2")
    private boolean hasNext2;
    @JsonProperty("search_pet_comment_response_dto")
    private List<SearchPetCommentResponseDto> searchPetCommentResponseDtos;

}
