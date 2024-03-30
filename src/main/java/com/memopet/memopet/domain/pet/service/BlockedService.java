package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.dto.BlockRequestDto;
import com.memopet.memopet.domain.pet.dto.BlockListWrapper;
import com.memopet.memopet.domain.pet.dto.BlockeResponseDto;
import com.memopet.memopet.domain.pet.dto.BlockedListResponseDto;
import com.memopet.memopet.domain.pet.entity.Blocked;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.entity.PetStatus;
import com.memopet.memopet.domain.pet.repository.BlockedRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockedService {

    private final BlockedRepository blockedRepository;
    private final PetRepository petRepository;
    private final PetService petService;
    private final MemberRepository memberRepository;
    /**
     * 차단한 펫 리스트
     */
    @Transactional(readOnly = true)
    public BlockListWrapper blockedPetList(Pageable pageable, Long blockerPetId, String email) {
        boolean validatePetResult=petService.validatePetRequest(email, blockerPetId);
        if (!validatePetResult) {
            return BlockListWrapper.builder()
                    .message("Pet not available or not active.")
                    .decCode('0')
                    .build();
        }
        try {
            Page<BlockedListResponseDto> result = blockedRepository.findBlockedPets(blockerPetId, pageable);
            return BlockListWrapper.builder()
                    .petList(result)
                    .decCode('1')
                    .build();
        } catch (Exception e) {
            return BlockListWrapper.builder()
                    .decCode('0')
                    .message("Error: "+ e.getMessage())
                    .build();
        }

    }


    /**
     * 차단
     */
    public BlockeResponseDto blockApet(BlockRequestDto blockRequestDTO, String email) {
        boolean validatePetResult=petService.validatePetRequest(email, blockRequestDTO.getBlockerPetId());
        if (!validatePetResult) {
            return BlockeResponseDto.builder()
                    .message("Pet not available or not active.")
                    .decCode('0')
                    .build();
        }
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
    public BlockeResponseDto unblockAPet(Long blockerPetId, Long blockedPetId,String email) {
        boolean validatePetResult=petService.validatePetRequest(email, blockerPetId);
        if (!validatePetResult) {
            return BlockeResponseDto.builder()
                    .message("Pet not available or not active.")
                    .decCode('0')
                    .build();
        }
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