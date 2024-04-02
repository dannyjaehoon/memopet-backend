package com.memopet.memopet.global.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDTO {
    @JsonProperty("pet_id")
    private Long petId;
    @JsonProperty("search_text")
    private String searchText;
    @JsonProperty("current_page")
    private int currentPage;
    @JsonProperty("data_counts")
    private int dataCounts;
    @JsonProperty("current_page_2")
    private int currentPage2;
    @JsonProperty("data_counts_2")
    private int dataCounts2;
    @JsonProperty("des_code")
    private int desCode;
}
