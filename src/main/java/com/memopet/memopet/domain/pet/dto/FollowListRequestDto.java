package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowListRequestDto {
    @JsonProperty("pet_id")
    private Long petId;
    @JsonProperty("follow_type")
    private int followType; //리스트 조회- 1:팔로워 2:팔로우
    @JsonProperty("current_page")
    private int currentPage;
    @JsonProperty("data_counts")
    private int dataCounts;

}
