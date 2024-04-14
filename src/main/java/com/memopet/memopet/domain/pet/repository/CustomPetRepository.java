package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.PetListResponseDto;
import com.memopet.memopet.domain.pet.dto.PetUpdateInfoRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CustomPetRepository {
    List<PetListResponseDto> findPetsById(Long id);

    boolean switchPetProfile(Long petId);

    boolean deleteAPet(UUID memberId, Long petId);

    void updateMemoryInfo(String petImgUrl, String backgroundImgUrl, PetUpdateInfoRequestDto petUpdateInfoRequestDto);
}
