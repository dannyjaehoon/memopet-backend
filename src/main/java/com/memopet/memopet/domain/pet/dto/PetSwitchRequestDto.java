package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetSwitchRequestDto {
    @JsonProperty("pet_id")
    Long petId;
    @JsonProperty("new_rep_pet_id")
    Long newRepPetId;


}
