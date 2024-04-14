package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchPetCommentResponseDto {

    private Long petId;
    private String petName;
    private String petDesc;
    private String petProfileUrl;
    private LocalDate petDeathDate;
    private int followYn;
}
