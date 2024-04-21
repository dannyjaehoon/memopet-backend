package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.entity.Blocked;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.repository.BlockedRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public BlockListResponseDto blockedPetList(BlockListRequestDto blockListRequestDto, String email) {
//        boolean validatePetResult=petService.validatePetRequest(email, blockerPetId);
//        if (!validatePetResult) {
//            return BlockListWrapper.builder()
//                    .message("Pet not available or not active.")
//                    .decCode('0')
//                    .build();
//        }

        PageRequest pageRequest = PageRequest.of(blockListRequestDto.getCurrentPage()-1, blockListRequestDto.getDataCounts());

        try {
            Slice<BlockedListResponseDto> result = blockedRepository.findBlockedPets(blockListRequestDto.getPetId(), pageRequest);
            return BlockListResponseDto.builder()
                    .petList(result.getContent())
                    .hasNext(result.hasNext())
                    .currentPage(result.getNumber()+1)
                    .dataCounts(result.getContent().size())
                    .decCode('1')
                    .build();
        } catch (Exception e) {
            throw new BadRequestRuntimeException(e.getMessage());
        }

    }

    public HashMap<Long, Integer> findBlockList(Long petId) {
        Optional<Pet> pet = petRepository.findById(petId);
        // 사용자가 차단한 펫 id 가져오기
        BlockedAndBlockerListResponseDto blockedAndBlockerListResponseDto = blockedPetList(petId);
        List<Blocked> blockedPetList = blockedAndBlockerListResponseDto.getPetList();
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
    public BlockedAndBlockerListResponseDto blockedPetList(Long blockerPetId) {
        try {
            List<Blocked> blockedPets = blockedRepository.findBlockedPets(blockerPetId);
            return BlockedAndBlockerListResponseDto.builder()
                    .petList(blockedPets)
                    .decCode('1')
                    .build();
        } catch (Exception e) {
            return BlockedAndBlockerListResponseDto.builder()
                    .decCode('0')
                    .message("Error: "+ e.getMessage())
                    .build();
        }
    }

    /**
     * 차단
     */
    public BlockedResponseDto blockApet(BlockRequestDto blockRequestDTO, String email) {
        //boolean validatePetResult=petService.validatePetRequest(email, blockRequestDTO.getBlockerPetId());
//        if (!validatePetResult) {
//            return BlockeResponseDto.builder()
//                    .message("Pet not available or not active.")
//                    .decCode('0')
//                    .build();
//        }
        try {
            Pet blockedPet = validateBlockRequest(blockRequestDTO);

            Blocked blocked = Blocked.builder()
                    .blockedPet(blockedPet)
                    .blockerPetId(blockRequestDTO.getBlockerPetId())
                    .build();
            blockedRepository.save(blocked);
            return BlockedResponseDto.builder()
                    .message("Successfully Blocked A pet.")
                    .decCode('1')
                    .build();
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                throw new BadRequestRuntimeException(e.getMessage());
            } else {
                throw new BadRequestRuntimeException("Error: Unexpected error occurred");
            }
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
    public BlockedResponseDto unblockAPet(Long blockerPetId, Long blockedPetId,String email) {
//        boolean validatePetResult=petService.validatePetRequest(email, blockerPetId);
//        if (!validatePetResult) {
//            return BlockeResponseDto.builder()
//                    .message("Pet not available or not active.")
//                    .decCode('0')
//                    .build();
//        }
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
            return BlockedResponseDto.builder()
                    .decCode('1')
                    .message("Unblocked a pet successfully")
                    .build();
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                throw new BadRequestRuntimeException(e.getMessage());
            } else {
                throw new BadRequestRuntimeException("Error: Unexpected error occurred");
            }
        }

    }
}