package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchPetCommentResponseDto {

    @JsonProperty("pet_id")
    private Long petId;
    @JsonProperty("pet_nm")
    private String petName;
    @JsonProperty("pet_desc")
    private String petDesc;
    @JsonProperty("pet_profile_url")
    private String petProfileUrl;
    @JsonProperty("pet_death_date")
    private LocalDate petDeathDate;
    @JsonProperty("follow_yn")
    private int followYn;

}
