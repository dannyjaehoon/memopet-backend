package com.memopet.memopet.domain.pet.controller;


import com.memopet.memopet.domain.pet.dto.BlockListRequestDto;
import com.memopet.memopet.domain.pet.dto.BlockListResponseDto;
import com.memopet.memopet.domain.pet.dto.BlockRequestDto;
import com.memopet.memopet.domain.pet.dto.BlockedResponseDto;
import com.memopet.memopet.domain.pet.service.BlockedService;
import com.memopet.memopet.global.common.dto.RestResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/block")
@Validated
public class BlockedController {
    private final BlockedService blockedService;


    /**
     * 차단 리스트
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("")
    public RestResult blockedPetList(BlockListRequestDto blockListRequestDto, Authentication authentication) {
        BlockListResponseDto blockListResponseDto = blockedService.blockedPetList(blockListRequestDto, authentication.getName());
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("blockedPetListResponse", blockListResponseDto);

        return new RestResult(dataMap);
    }

    /**
     * 차단
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping("")
    public RestResult BlockAPet(@RequestBody @Valid BlockRequestDto blockRequestDTO, Authentication authentication) {
        BlockedResponseDto blockedResponseDto = blockedService.blockApet(blockRequestDTO, authentication.getName());
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("blockPetResponse", blockedResponseDto);

        return new RestResult(dataMap);
    }

    /**
     * 차단 취소
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("")
    public RestResult CancelBlocking(@RequestParam("blockerPetId")Long petId, @RequestParam("blockedPetId") Long blockedPetId, Authentication authentication) {
        BlockedResponseDto blockedResponseDto = blockedService.unblockAPet(petId, blockedPetId, authentication.getName());
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("cancelBlockingPetResponse", blockedResponseDto);

        return new RestResult(dataMap);
    }
}
