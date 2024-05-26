package com.memopet.memopet.domain.pet.dto;

public interface PetFollowingResponseDto {

    long getPetId();
    String getPetName();
    String getPetDesc();
    String getPetProfileUrl();
    Integer getFollowCnt();

    Integer getFollowYn();

}


