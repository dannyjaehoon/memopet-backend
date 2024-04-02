package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsRequestDto {
    @JsonProperty("pet_id")
    private Long petId;
    @JsonProperty("current_page")
    private int currentPage;
    @JsonProperty("data_counts")
    private int dataCounts;

}
