package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.FollowListResponseDto;
import com.memopet.memopet.domain.pet.dto.PetFollowingResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomFollowRepository {
    //Slice<PetFollowingResponseDto> findFollowingPetsById(Pageable pageable,Long petId);
    //Slice<PetFollowingResponseDto> findFollowerPetsByPetId(Pageable pageable, Long petId);
}
