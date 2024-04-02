package com.memopet.memopet.domain.pet.service;


import com.memopet.memopet.domain.pet.dto.LikePostORDeleteRequestDto;
import com.memopet.memopet.domain.pet.dto.LikePostORDeleteResponseDto;
import com.memopet.memopet.domain.pet.entity.NotificationType;
import com.memopet.memopet.domain.pet.entity.Likes;
import com.memopet.memopet.domain.pet.entity.Memory;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.repository.LikesRepository;
import com.memopet.memopet.domain.pet.repository.MemoryRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final PetRepository petRepository;
    private final MemoryRepository memoryRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = false)
    public LikePostORDeleteResponseDto postOrDeleteLike(LikePostORDeleteRequestDto likePostORDeleteRequestDto) {

        Optional<Pet> myPet = petRepository.findById(likePostORDeleteRequestDto.getMyPetId()); //좋아요를 한 프로필
        Optional<Pet> pet = petRepository.findById(likePostORDeleteRequestDto.getPetId());

        if(!myPet.isPresent()) LikePostORDeleteResponseDto.builder().decCode('0').errorMsg("좋아요를 한 프로필 정보가 없습니다.").build();
        if(!pet.isPresent()) LikePostORDeleteResponseDto.builder().decCode('0').errorMsg("좋아요를 당한 프로필 정보가 없습니다.").build();

        Optional<Memory> memoryOptional = memoryRepository.findById(likePostORDeleteRequestDto.getMemoryId());

        if(!memoryOptional.isPresent()) LikePostORDeleteResponseDto.builder().decCode('0').errorMsg("추억 정보가 없습니다.").build();
        Memory memory = memoryOptional.get();

        Optional<Likes> like = likesRepository.findByPetIdAndLikedOwnPetIdAndMemoryID(myPet.get(), pet.get().getId(), memory);
        Likes likes = Likes.builder().pet(myPet.get()).likedOwnPetId(pet.get().getId()).memoryId(memory).createdDate(LocalDateTime.now()).build();

        if(!like.isPresent()) { // 좋아요를 하지 않은 상태이면
            // 좋아요를 한다
            likesRepository.save(likes);
            // 추억 테이블의 좋아요 갯수를 하나 증가시킨다.
            memory.updateLikesCount(memory.getLikeCount()+1);
            // 알림을 보낸다.

            notificationService.saveNotificationInfo(NotificationType.LIKE_ALARM,pet.get(),myPet.get().getId());

        } else { // 좋아요를 한 상태이면

            Optional<Likes> likeOptional = likesRepository.findById(like.get().getId());
            // 좋아요를 없앤다.
            likesRepository.delete(likeOptional.get());
            // 추억테이블의 좋아요 갯수를 하나 줄인다.
            memory.updateLikesCount(memory.getLikeCount()-1);
        }

        return LikePostORDeleteResponseDto.builder().decCode('1').build();
    }
}
