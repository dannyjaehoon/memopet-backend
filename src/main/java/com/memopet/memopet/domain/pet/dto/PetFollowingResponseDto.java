package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.pet.entity.Gender;
import lombok.*;


public interface PetFollowingResponseDto {

    long getPetId();
    String getPetName();
    String getPetDesc();
    String getPetProfileUrl();
    Integer getFollowCnt();

    Integer getFollowYn();

}


