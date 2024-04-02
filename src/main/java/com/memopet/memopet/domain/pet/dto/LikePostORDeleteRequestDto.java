package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikePostORDeleteRequestDto {

    @JsonProperty("memory_id")
    private Long memoryId;
    @JsonProperty("pet_id")
    private Long petId;
    @JsonProperty("my_pet_id")
    private Long myPetId;
}
