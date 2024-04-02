package com.memopet.memopet.global.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentSearchDeleteResponseDto {
    @JsonProperty("dsc_code")
    private String dscCode;
    @JsonProperty("data_counts")
    private int dataCounts;
    @JsonProperty("search_texts")
    private List<String> searchTexts;
}
