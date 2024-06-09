package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetDetailInfoRequestDto {

    private Long petId;
    private Long myPetId;
}
