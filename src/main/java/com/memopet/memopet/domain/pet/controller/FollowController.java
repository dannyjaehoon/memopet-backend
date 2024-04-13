package com.memopet.memopet.domain.pet.controller;

import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.service.FollowService;
import com.memopet.memopet.domain.pet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;
    private final PetService petService;

    /**
     * 팔로우
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping("")
    public FollowResponseDto FollowAPet(@RequestBody @Valid FollowRequestDto followRequestDTO) {
        return followService.followAPet(followRequestDTO);
    }

    /**
     * 리스트 조회- 1:팔로워 2:팔로우
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("")
    public FollowListResponseDto followList(FollowListRequestDto followListRequestDto){
        System.out.println(" getFollowType: " + followListRequestDto.getFollowType());
        System.out.println(" getPetId : " + followListRequestDto.getPetId());
        return followService.followList(followListRequestDto);
    }

    /**
     * 팔로우 취소
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("")
    public FollowResponseDto unfollow(@RequestParam("petId") Long petId, @RequestParam("followingPetId") Long followingPetId, Authentication authentication) {
//        boolean validatePetResult = petService.validatePetRequest(authentication.getName(), petId);
//        if (!validatePetResult) {
//            return new FollowResponseDto('0',"Pet not available or not active.");
//        }
        return followService.unfollow(petId,followingPetId);
    }

    /**
     * 팔로워 삭제
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/follower")
    public FollowResponseDto deleteFollower(@PathVariable @Param("followingPetId")Long petId,
                                            @PathVariable @Param("petId") Long followerPetId,
                                            Authentication authentication) {
//        boolean validatePetResult = petService.validatePetRequest(authentication.getName(), petId);
//        if (!validatePetResult) {
//            return new FollowResponseDto('0',"Pet not available or not active.");
//        }

        FollowResponseDto result= followService.unfollow(followerPetId, petId);
        if (result.getDecCode()=='0'){
            return result;
        }
        return new FollowResponseDto('1', "Deleted follower pet successfully");
    }


}
