package com.memopet.memopet.global.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.pet.dto.MemoryResponseDto;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentSearchResponseDto {
    @JsonProperty("data_counts")
    private int dataCounts;
    @JsonProperty("search_texts")
    private List<String> searchTexts;
}
