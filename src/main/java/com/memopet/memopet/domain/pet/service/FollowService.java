package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.entity.NotificationType;
import com.memopet.memopet.domain.pet.entity.Follow;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.entity.PetStatus;
import com.memopet.memopet.domain.pet.repository.FollowRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public FollowListResponseDto followList(FollowListRequestDto followListRequestDto) {

        Optional<Pet> pet = petRepository.findById(followListRequestDto.getPetId());
        if (!pet.isPresent()) {
            return FollowListResponseDto.builder()
                    .errorDescription("Pet not available or not active.")
                    .decCode('0')
                    .build();
        }

        FollowListResponseDto followListResponseDto;

        PageRequest pageRequest = PageRequest.of(followListRequestDto.getCurrentPage()-1, followListRequestDto.getDataCounts());

        switch (followListRequestDto.getFollowType()) {
            case 1:
                Slice<PetFollowingResponseDto> followerSlice = followRepository.findFollowerPetsByPetId(followListRequestDto.getPetId(),pageRequest);
                followListResponseDto= FollowListResponseDto.builder()
                        .followList(followerSlice.getContent())
                        .hasNext(followerSlice.hasNext())
                        .currentPage(followerSlice.getNumber()+1)
                        .dataCounts(followerSlice.getContent().size())
                        .decCode('1').build();
                break;
            case 2:
                Slice<PetFollowingResponseDto> followingSlice = followRepository.findFollowingPetsById(followListRequestDto.getPetId(),pageRequest);
                followListResponseDto= FollowListResponseDto.builder()
                        .followList(followingSlice.getContent())
                        .hasNext(followingSlice.hasNext())
                        .currentPage(followingSlice.getNumber()+1)
                        .dataCounts(followingSlice.getContent().size())
                        .decCode('1').build();
                break;
            default:
                followListResponseDto= FollowListResponseDto.builder()
                        .errorDescription("Unexpected value: " + followListRequestDto.getFollowType())
                        .decCode('0').build();
                break;
        }

        return followListResponseDto;
    }


    /**
     * 팔로우 취소
     */
    @Transactional(readOnly = false)
    public FollowResponseDto unfollow(Long petId, Long followingPetId) {
        Optional<Pet> pet = petRepository.findById(followingPetId);

        if(pet.isEmpty()) throw new BadRequestRuntimeException("팔로잉 펫");
        Pet followingPet = pet.get();
        if (!followRepository.existsByPetIdAndFollowingPetId(petId, followingPet)) {
            return new FollowResponseDto('0', "Following relation doesn't exist.");
        }
        followRepository.deleteByPetIdAndFollowingPetId(petId, followingPet);
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

        if (followRepository.existsByPetIdAndFollowingPetId(followRequestDTO.getPetId(), followingPet)) {
            return new FollowResponseDto('0',"Following relationship already exists");
        }

        Follow follow = Follow.builder()
                .petId(followRequestDTO.getPetId())
                .following(followingPet)
                .build();
        followRepository.save(follow);

        Optional<Pet> pet = petRepository.findById(followRequestDTO.getPetId());

        if(!pet.isPresent()) return new FollowResponseDto('0',"Followed Pet Info not found");

        // 팔로우를 했을때 팔로우를 당한 프로필에 알림을 보낸다.
        notificationService.saveNotificationInfo(NotificationType.FOLLOW_ALARM,pet.get(), followingPet.getId());

        return new FollowResponseDto('1', "Followed the pet successfully");
    }


}
