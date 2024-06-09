package com.memopet.memopet.domain.pet.dto;

import com.memopet.memopet.domain.pet.entity.Gender;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetResponseDto {

    private Long petId;
    private String petName;
    private String petDesc;
    private Gender petGender;
    private String petProfileUrl;
    private String backImgUrl;
    private int likes;
}


