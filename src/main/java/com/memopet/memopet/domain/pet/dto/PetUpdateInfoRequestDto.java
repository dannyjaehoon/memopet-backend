package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.pet.entity.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetUpdateInfoRequestDto {

    private Long petId;
    private String petName;
    private LocalDate petBirthDate;
    private LocalDate petDeathDate;
    private String petProfileUrl;
    private String backImgUrl;
    private String petFavs;
    private String petFavs2;
    private String petFavs3;
    private String petDesc;
    private Integer petProfileFrame;
}
