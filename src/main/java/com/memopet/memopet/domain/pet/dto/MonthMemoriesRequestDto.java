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

    private Long petId;

    private Long myPetId;

    private int currentPage;

    private int dataCounts;

    private String yearMonth;
}
