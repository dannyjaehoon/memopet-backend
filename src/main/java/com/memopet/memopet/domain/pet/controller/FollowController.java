package com.memopet.memopet.domain.pet.controller;

import com.memopet.memopet.domain.pet.dto.FollowListRequestDto;
import com.memopet.memopet.domain.pet.dto.FollowListResponseDto;
import com.memopet.memopet.domain.pet.dto.FollowRequestDto;
import com.memopet.memopet.domain.pet.dto.FollowResponseDto;
import com.memopet.memopet.domain.pet.service.FollowService;
import com.memopet.memopet.global.common.dto.RestResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;

    /**
     * 팔로우
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping("")
    public RestResult FollowAPet(@RequestBody @Valid FollowRequestDto followRequestDTO) {
        FollowResponseDto followResponseDto = followService.followAPet(followRequestDTO);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("followPetResponse", followResponseDto);

        return new RestResult(dataMap);
    }

    /**
     * 리스트 조회- 1:팔로워 2:팔로우
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("")
    public RestResult followList(FollowListRequestDto followListRequestDto){
        FollowListResponseDto followListResponseDto = followService.followList(followListRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("findFollowPetListResponse", followListResponseDto);

        return new RestResult(dataMap);
    }

    /**
     * 팔로우 취소
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("")
    public RestResult unfollow(@RequestParam("petId") Long petId, @RequestParam("followingPetId") Long followingPetId, Authentication authentication) {
//        boolean validatePetResult = petService.validatePetRequest(authentication.getName(), petId);
//        if (!validatePetResult) {
//            return new FollowResponseDto('0',"Pet not available or not active.");
//        }
        FollowResponseDto unfollowResponseDto = followService.unfollow(petId, followingPetId);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("findUnFollowPetListResponse", unfollowResponseDto);

        return new RestResult(dataMap);
    }

//    /**
//     * 팔로워 삭제
//     */
//    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
//    @DeleteMapping("/follower")
//    public FollowResponseDto deleteFollower(@PathVariable @Param("followingPetId")Long petId,
//                                            @PathVariable @Param("petId") Long followerPetId,
//                                            Authentication authentication) {
////        boolean validatePetResult = petService.validatePetRequest(authentication.getName(), petId);
////        if (!validatePetResult) {
////            return new FollowResponseDto('0',"Pet not available or not active.");
////        }
//
//        FollowResponseDto followResponseDto = followService.unfollow(followerPetId, petId);
//
//        Map<String, Object> dataMap = new LinkedHashMap<>();
//        dataMap.put("unfResponse", followResponseDto);
//
//        return new RestResult(dataMap);
//    }


}
