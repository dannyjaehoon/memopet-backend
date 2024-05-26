package com.memopet.memopet.domain.pet.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetProfileResponseDto {
    private List<PetListResponseDto> petList;
    private char decCode;
    private String message;

}
