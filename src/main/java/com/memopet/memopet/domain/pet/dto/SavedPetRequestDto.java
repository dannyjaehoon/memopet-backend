package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.pet.entity.Gender;
import com.memopet.memopet.domain.pet.entity.PetStatus;
import com.memopet.memopet.domain.pet.entity.Species;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavedPetRequestDto {
    @JsonProperty("email")
    @Email
    private String email;
    @JsonProperty("pet_nm")
    private String petName;
    @JsonProperty("pet_desc")
    private String petDesc;
    @JsonProperty("pet_spec_m")
    private String petSpecM;
    @JsonProperty("pet_spec_s")
    private String petSpecS;
    @JsonProperty("pet_gender")
    private Gender petGender;
    @JsonProperty("pet_profile_url")
    private String petProfileUrl;
    @JsonProperty("back_img_url")
    private String backImgUrl;
    @JsonProperty("pet_profile_frame")
    private String petProfileFrame;
    @JsonProperty("birth_dt")
    private String birthDate;
    @JsonProperty("death_dt")
    private String petDeathDate;
    @JsonProperty("like_behaviour1")
    private String petFavs;
    @JsonProperty("like_behaviour2")
    private String petFavs2;
    @JsonProperty("like_behaviour3")
    private String petFavs3;
}
