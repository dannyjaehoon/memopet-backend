package com.memopet.memopet.domain.pet.controller;


import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.service.MemoryService;
import com.memopet.memopet.domain.pet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemoryController {

    private final MemoryService memoryService;

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/memory")
    public MemoryResponseDto memory(MemoryRequestDto memoryRequestDto) {
        MemoryResponseDto memoryResponseDto = memoryService.findMemoryByMemoryId(memoryRequestDto);
        return memoryResponseDto;
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/liked-memories")
    public LikedMemoryResponseDto likedMemories(LikedMemoryRequestDto likedMemoryRequestDto) {
        LikedMemoryResponseDto likedMemoryResponseDto = memoryService.findLikedMemoriesByPetId(likedMemoryRequestDto);
        return likedMemoryResponseDto;
    }

    /**
     * 추억 생성
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping(value = "/memory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MemoryPostResponseDto postAMemory(@RequestPart List<MultipartFile> files, @Valid @RequestPart MemoryPostRequestDto memoryPostRequestDTO) {
        if (files.size() > 10) {
            return MemoryPostResponseDto.builder().decCode('0').build();
        }

        boolean isPosted = memoryService.postMemoryAndMemoryImages(files, memoryPostRequestDTO);
        return MemoryPostResponseDto.builder().decCode(isPosted ? '1' : '0').build();
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/recent-memories")
    public RecentMainMemoriesResponseDto mainMemories(RecentMainMemoriesRequestDto recentMainMemoriesRequestDto) {
        RecentMainMemoriesResponseDto recentMainMemoriesResponseDto = memoryService.findMainMemoriesByPetId(recentMainMemoriesRequestDto);
        return recentMainMemoriesResponseDto;
    }


    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/month-memories")
    public MonthMemoriesResponseDto monthMemories(MonthMemoriesRequestDto monthMemoriesRequestDto) {
        MonthMemoriesResponseDto monthMemoriesResponseDto = memoryService.findMonthMemoriesByPetId(monthMemoriesRequestDto);
        return monthMemoriesResponseDto;
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/memory")
    public MemoryDeleteResponseDto deleteMemory(@RequestBody MemoryDeleteRequestDto memoryDeleteRequestDto) throws Exception {
        MemoryDeleteResponseDto memoryDeleteResponseDto  = memoryService.deleteMemory(memoryDeleteRequestDto);
        return memoryDeleteResponseDto;
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PatchMapping(value="/memory")
    public MemoryUpdateResponseDto updateMemory(@RequestPart List<MultipartFile> files, @RequestPart(value = "memoryUpdateRequestDto") @Valid MemoryUpdateRequestDto memoryUpdateRequestDto) {
        MemoryUpdateResponseDto memoryUpdateResponseDto = memoryService.updateMemoryInfo(memoryUpdateRequestDto, files);
        return memoryUpdateResponseDto;
    }
}
