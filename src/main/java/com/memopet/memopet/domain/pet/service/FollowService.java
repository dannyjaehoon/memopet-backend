package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.entity.NotificationType;
import com.memopet.memopet.domain.pet.entity.Follow;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.repository.FollowRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final NotificationService notificationService;
    private final PetRepository petRepository;

    /**
     * 리스트 조회- 1:팔로워 2:팔로우
     */
    public FollowListWrapper followList(Pageable pageable, Long petId, int followType) {
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
    public FollowResponseDto unfollow(Long petId, Long followingPetId) {
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
            return new FollowResponseDto('0', "Following Pet Info not found");
        }

        if (followRepository.existsByPetIdAndFollowingPetId(followRequestDTO.getPetId(), followRequestDTO.getFollowingPetId())) {
            return new FollowResponseDto('0',"Following relationship already exists");
        }

        Follow follow = Follow.builder()
                .petId(followRequestDTO.getPetId())
                .followingPet(followingPet)
                .build();
        followRepository.save(follow);

        Optional<Pet> pet = petRepository.findById(followRequestDTO.getPetId());

        if(!pet.isPresent()) return new FollowResponseDto('0',"Followed Pet Info not found");

        // 팔로우를 했을때 팔로우를 당한 프로필에 알림을 보낸다.
        notificationService.saveNotificationInfo(NotificationType.FOLLOW_ALARM,pet.get(), followingPet.getId());

        return new FollowResponseDto('1', "Followed the pet successfully");
    }
}
