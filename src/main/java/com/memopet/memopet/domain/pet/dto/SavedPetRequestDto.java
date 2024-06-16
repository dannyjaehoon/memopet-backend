package com.memopet.memopet.domain.pet.dto;

import com.memopet.memopet.domain.pet.entity.Gender;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavedPetRequestDto {

    @Email
    private String phoneNum;
    private String petName;
    private String petDesc;
    private String petSpecM;
    private String petSpecS;
    private Gender petGender;
    private String petProfileFrame;
    private String birthDate;
    private String deathDate;
    private String petFavs;
    private String petFavs2;
    private String petFavs3;
}
