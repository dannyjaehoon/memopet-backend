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
    @GetMapping("/{petId}/{followType}")
    public FollowListWrapper followList(@PageableDefault(size = 20,page = 0)Pageable pageable,
                                        @PathVariable Long petId,
                                        @PathVariable int followType, Authentication authentication){
        return followService.followList(pageable,petId,followType,authentication.getName());
    }

    /**
     * 팔로우 취소
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/{petId}/{followingPetId}")
    public FollowResponseDto unfollow(@PathVariable Long petId, @PathVariable Long followingPetId, Authentication authentication) {
        boolean validatePetResult = petService.validatePetRequest(authentication.getName(), petId);
        if (!validatePetResult) {
            return new FollowResponseDto('0',"Pet not available or not active.");
        }
        return followService.unfollow(petId,followingPetId,authentication.getName());
    }

    /**
     * 팔로워 삭제
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/follower/{petId}/{followerPetId}")
    public FollowResponseDto deleteFollower(@PathVariable @Param("followingPetId")Long petId,
                                            @PathVariable @Param("petId") Long followerPetId,
                                            Authentication authentication) {
        boolean validatePetResult = petService.validatePetRequest(authentication.getName(), petId);
        if (!validatePetResult) {
            return new FollowResponseDto('0',"Pet not available or not active.");
        }

        FollowResponseDto result= followService.unfollow(followerPetId, petId,authentication.getName());
        if (result.getDecCode()=='0'){
            return result;
        }
        return new FollowResponseDto('1', "Deleted follower pet successfully");
    }


}
