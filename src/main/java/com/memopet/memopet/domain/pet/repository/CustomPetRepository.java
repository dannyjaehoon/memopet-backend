package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.PetListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CustomPetRepository {
    List<PetListResponseDto> findPetsById(Long id);

    boolean switchPetProfile(Long petId);

    boolean deleteAPet(UUID memberId, Long petId);
}
