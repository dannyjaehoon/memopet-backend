package com.memopet.memopet.domain.pet.controller;

import com.memopet.memopet.domain.pet.dto.LikePostORDeleteRequestDto;
import com.memopet.memopet.domain.pet.dto.LikePostORDeleteResponseDto;
import com.memopet.memopet.domain.pet.service.LikesService;
import com.memopet.memopet.global.common.dto.RestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikesController {

    private final LikesService likesService;

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping("/likes")
    public RestResult postOrDeleteLikes(@RequestBody LikePostORDeleteRequestDto likePostORDeleteRequestDto) {
        LikePostORDeleteResponseDto likePostORDeleteResponseDto = likesService.postOrDeleteLike(likePostORDeleteRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("likeOrUnlikeResponse", likePostORDeleteResponseDto);

        return new RestResult(dataMap);
    }
}
