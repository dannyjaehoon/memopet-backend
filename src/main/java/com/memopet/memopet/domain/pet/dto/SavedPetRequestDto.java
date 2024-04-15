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

    @Email
    private String email;
    private String petName;

    private String petDesc;
    private String petSpecM;
    private String petSpecS;
    private Gender petGender;
    private String petProfileUrl;
    private String backImgUrl;
    private String petProfileFrame;
    private String birthDate;
    private String petDeathDate;
    private String petFavs;
    private String petFavs2;
    private String petFavs3;
}
