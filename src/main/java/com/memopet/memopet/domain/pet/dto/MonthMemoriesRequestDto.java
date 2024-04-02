package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthMemoriesRequestDto {
    @JsonProperty("pet_id")
    private Long petId;
    @JsonProperty("my_pet_id")
    private Long myPetId;
    @JsonProperty("current_page")
    private int currentPage;
    @JsonProperty("data_counts")
    private int dataCounts;
    @JsonProperty("year_month")
    private String yearMonth;
}
