package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.entity.Blocked;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.repository.BlockedRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockedService {

    private final BlockedRepository blockedRepository;
    private final PetRepository petRepository;
    /**
     * 차단한 펫 리스트
     */
    @Transactional(readOnly = true)
    public BlockListWrapper blockedPetList(Pageable pageable, Long blockerPetId) {
        try {
            Page<BlockedListResponseDto> result = blockedRepository.findBlockedPets(blockerPetId, pageable);
            return BlockListWrapper.builder()
                    .petList(result)
                    .decCode('1')
                    .build();
        } catch (Exception e) {
            return BlockListWrapper.builder()
                    .decCode('0')
                    .errorDescription("Error: "+ e.getMessage())
                    .build();
        }

    }

    public HashMap<Long, Integer> findBlockList(Long petId) {
        Optional<Pet> pet = petRepository.findById(petId);
        // 사용자가 차단한 펫 id 가져오기
        BlockListResponseDto blockListResponseDto = blockedPetList(petId);
        List<Blocked> blockedPetList = blockListResponseDto.getPetList();
        HashMap<Long,Integer> blockMap = new HashMap<>();

        for(Blocked blockedPet : blockedPetList) {
            if(blockMap.getOrDefault(blockedPet.getBlockedPet().getId(),0) != 0) continue;
            blockMap.put(blockedPet.getBlockedPet().getId(),1);
        }
        // 프로필 차단된 리스트
        // 사용자 2가 사용자 1을 차단했을때 사용자 1 은 사용자 2의 정보를 볼수없다.
        // 블락커의 정보가 필요함
        List<Blocked> blockerList = blockedRepository.findBlockerPets(pet.get());

        for(Blocked blockerPet : blockerList) {
            if(blockMap.getOrDefault(blockerPet.getBlockerPetId(),0) != 0) continue;
            blockMap.put(blockerPet.getBlockerPetId(),1);
        }

        return blockMap;
    }

    // 자기를 블락한 프로필과 자기가 블락한 프로필 둘다 가져옴
    @Transactional(readOnly = true)
    public BlockListResponseDto blockedPetList(Long blockerPetId) {
        try {
            List<Blocked> blockedPets = blockedRepository.findBlockedPets(blockerPetId);
            return BlockListResponseDto.builder()
                    .petList(blockedPets)
                    .decCode('1')
                    .build();
        } catch (Exception e) {
            return BlockListResponseDto.builder()
                    .decCode('0')
                    .errorDescription("Error: "+ e.getMessage())
                    .build();
        }

    }

    /**
     * 차단
     */
    public BlockeResponseDto blockApet(BlockRequestDto blockRequestDTO) {
        try {
            Pet blockedPet = validateBlockRequest(blockRequestDTO);

            Blocked blocked = Blocked.builder()
                    .blockedPet(blockedPet)
                    .blockerPetId(blockRequestDTO.getBlockerPetId())
                    .build();
            blockedRepository.save(blocked);
            return BlockeResponseDto.builder()
                    .message("Successfully Blocked A pet.")
                    .decCode('1')
                    .build();
        } catch (Exception e) {
            String errorMessage;
            if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                errorMessage = "Error: " + e.getMessage();
            } else {
                errorMessage = "Error: Unexpected error occurred";
            }
            return BlockeResponseDto.builder()
                    .decCode('0')
                    .message(errorMessage)
                    .build();
        }
    }


    /**
     * 차단 메서드 에 사용 되는 차단 검증 메서드
     */
    private Pet validateBlockRequest(BlockRequestDto blockRequestDTO) {
        if (blockRequestDTO.getBlockedPetId().equals(blockRequestDTO.getBlockerPetId())) {
            throw new IllegalArgumentException("A pet cannot block itself");
        }
        if (!petRepository.existsById(blockRequestDTO.getBlockerPetId())) {
            throw new IllegalArgumentException("Pet not found");
        }
        Pet blockedPet = petRepository.findByIdAndDeletedDateIsNull(blockRequestDTO.getBlockedPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found"));

        if (blockedRepository.existsByPetIds(blockRequestDTO.getBlockerPetId(), blockRequestDTO.getBlockedPetId())) {
            throw new IllegalStateException("Blocking relationship already exists");
        }
        return blockedPet;
    }


    /**
     * 차단 취소
     */
    public BlockeResponseDto unblockAPet(Long blockerPetId, Long blockedPetId) {
        try {
            // Check if the blocking relationship already exists
            if (!blockedRepository.existsByPetIds(blockerPetId, blockedPetId)) {
                throw new IllegalStateException("Blocking relationship does not exist");
            }
            // Find the blocked entity based on the provided blocker and blocked pet IDs
            Blocked blockedEntity = blockedRepository.findByBlockerPetIdAndBlockedPet(
                    blockerPetId, petRepository.findById(blockedPetId)
                            .orElseThrow(() -> new IllegalArgumentException("Blocked Pet not found")));

            blockedRepository.delete(blockedEntity);
            return BlockeResponseDto.builder()
                    .decCode('1')
                    .message("Unblocked a pet successfully")
                    .build();
        } catch (Exception e) {
            String errorMessage;
            if (e instanceof IllegalStateException || e instanceof IllegalArgumentException) {
                return BlockeResponseDto.builder()
                        .message("error: " + e.getMessage())
                        .decCode('0')
                        .build();
            } else {
                return BlockeResponseDto.builder()
                        .message("Error: Unexpected error occurred")
                        .build();
            }
        }

    }
}