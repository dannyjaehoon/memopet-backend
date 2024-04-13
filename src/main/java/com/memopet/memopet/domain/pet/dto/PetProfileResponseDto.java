package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetProfileResponseDto {
    private List<PetListResponseDto> petList;
    @JsonProperty("dec_code")
    private char decCode;
    private String message;

}
