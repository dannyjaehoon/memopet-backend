package com.memopet.memopet.domain.pet.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoryUpdateRequestDto {

    private Long memoryId;

    private Long petId;

    private String memoryTitle;

    private Integer openRestrictionLevel;

    private String memoryDesc;

    private LocalDate memoryDate;

}
