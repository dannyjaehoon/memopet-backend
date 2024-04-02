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

    @JsonProperty("pet_id")
    private Long petId;
    @JsonProperty("pet_nm")
    private String petName;
    @JsonProperty("pet_birth_date")
    private LocalDate petBirthDate;
    @JsonProperty("pet_death_date")
    private LocalDate petDeathDate;
    @JsonProperty("pet_profile_url")
    private String petProfileUrl;
    @JsonProperty("back_img_url")
    private String backImgUrl;
    @JsonProperty("pet_favs")
    private String petFavs;
    @JsonProperty("pet_favs2")
    private String petFavs2;
    @JsonProperty("pet_favs3")
    private String petFavs3;
    @JsonProperty("pet_desc")
    private String petDesc;
    @JsonProperty("pet_profile_frame")
    private Integer petProfileFrame;
}
