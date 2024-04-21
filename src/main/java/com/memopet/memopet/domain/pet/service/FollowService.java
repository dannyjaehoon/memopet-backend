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
            throw new BadRequestRuntimeException("Pet not available or not active.");
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
                throw new BadRequestRuntimeException("Unexpected value: " + followListRequestDto.getFollowType());
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
            throw new BadRequestRuntimeException("Following relationship doesn't exist.");
        }
        followRepository.deleteByPetIdAndFollowingPetId(petId, followingPet);
        return new FollowResponseDto('1', "Unfollowed the pet successfully");
    }


    /**
     * 팔로우
     * tip: 팔로우를 했을때 팔로우를 당한 프로필에 알림을 보내는데, 이때 알림을 보내는건 좋은데, 알림을 받는 사람이 누구인지 알수가 없는데, 이부분은 어떻게 처리하는지 궁금합니다.
     * tip: 누가 팔로우하는지를 알 필요가 없는걸까요?
     * tip: 만약 로그인한 사람이 팔로워 라면 여기로 Member 를 넘겨야 합니다. 다른 메소드들도 마찬가지인데요 Member 를 ArgumentResolver 를 통해서 받아오는게 좋습니다.
     */
    public FollowResponseDto followAPet(FollowRequestDto followRequestDTO) {

        if (followRequestDTO.getPetId().equals(followRequestDTO.getFollowingPetId())) {
            throw new BadRequestRuntimeException("A pet cannot follow itself");

        }
        Pet followingPet = petRepository.findByIdAndDeletedDateIsNull(followRequestDTO.getFollowingPetId())
                .orElse(null);

        if (followingPet == null) {
            throw new BadRequestRuntimeException("Pet not found");
        }

        if (followRepository.existsByPetIdAndFollowingPetId(followRequestDTO.getPetId(), followingPet)) {
            throw new BadRequestRuntimeException("Following relationship already exists");
        }

        Follow follow = Follow.builder()
                .petId(followRequestDTO.getPetId())
                .following(followingPet)
                .build();
        followRepository.save(follow);

        Optional<Pet> pet = petRepository.findById(followRequestDTO.getPetId());

        if(!pet.isPresent()) throw new BadRequestRuntimeException("Followed Pet Info not found");

        // 팔로우를 했을때 팔로우를 당한 프로필에 알림을 보낸다.
        notificationService.saveNotificationInfo(NotificationType.FOLLOW_ALARM, pet.get(), followingPet.getId());

        return new FollowResponseDto('1', "Followed the pet successfully");
    }


}
