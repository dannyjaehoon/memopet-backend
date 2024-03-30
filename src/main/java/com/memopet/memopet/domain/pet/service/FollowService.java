package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.entity.Follow;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.entity.PetStatus;
import com.memopet.memopet.domain.pet.repository.FollowRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final PetRepository petRepository;
    private final PetService petService;

    /**
     * 리스트 조회- 1:팔로워 2:팔로우
     */
    public FollowListWrapper followList(Pageable pageable, Long petId, int followType, String email) {
        boolean validatePetResult = petService.validatePetRequest(email, petId);
        if (!validatePetResult) {
            return FollowListWrapper.builder()
                    .errorDescription("Pet not available or not active.")
                    .decCode('0')
                    .build();
        }


        FollowListWrapper wrapper;

        switch (followType) {
            case 1:
                wrapper= FollowListWrapper.builder()
                        .followList(followRepository.findFollowerPetsByPetId(pageable, petId))
                        .decCode('1').build();
                break;
            case 2:


                wrapper= FollowListWrapper.builder()
                        .followList(followRepository.findFollowingPetsById(pageable, petId))
                        .decCode('1').build();
                break;
            default:
                wrapper= FollowListWrapper.builder()
                        .decCode('0')
                        .errorDescription("Unexpected value: " + followType)
                        .build();
                break;
        }

        return wrapper;
    }


    /**
     * 팔로우 취소
     */
    public FollowResponseDto unfollow(Long petId, Long followingPetId, String email) {

        if (!followRepository.existsByPetIdAndFollowingPetId(petId, followingPetId)) {
            return new FollowResponseDto('0', "Following relation doesn't exist.");
        }
        followRepository.deleteByPetIdAndFollowingPetId(petId, followingPetId);
        return new FollowResponseDto('1', "Unfollowed the pet successfully");
    }


    /**
     * 팔로우
     */
    public FollowResponseDto followAPet(FollowRequestDto followRequestDTO) {

        if (followRequestDTO.getPetId().equals(followRequestDTO.getFollowingPetId())) {
            return new FollowResponseDto('0', "A pet cannot follow itself");

        }
        Pet followingPet = petRepository.findByIdAndDeletedDateIsNull(followRequestDTO.getFollowingPetId())
                .orElse(null);

        if (followingPet == null) {
            return new FollowResponseDto('0', "Pet not found");
        }

        if (followRepository.existsByPetIdAndFollowingPetId(followRequestDTO.getPetId(), followRequestDTO.getFollowingPetId())) {
            return new FollowResponseDto('0',"Following relationship already exists");
        }

        Follow follow = Follow.builder()
                .petId(followRequestDTO.getPetId())
                .followingPet(followingPet)
                .build();
        followRepository.save(follow);
        return new FollowResponseDto('1', "Followed the pet successfully");
    }


}
