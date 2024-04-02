package com.memopet.memopet.global.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentSearchDeleteRequestDto {
    @JsonProperty("pet_id")
    private Long petId;

    @JsonProperty("search_text")
    private String searchText;
}
