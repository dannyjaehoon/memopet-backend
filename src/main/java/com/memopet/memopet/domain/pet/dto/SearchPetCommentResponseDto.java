package com.memopet.memopet.domain.pet.dto;

import lombok.*;

import java.time.LocalDate;

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
