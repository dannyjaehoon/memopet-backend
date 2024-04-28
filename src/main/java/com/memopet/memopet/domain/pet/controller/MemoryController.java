package com.memopet.memopet.domain.pet.controller;


import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.service.MemoryService;
import com.memopet.memopet.domain.pet.service.PetService;
import com.memopet.memopet.global.common.dto.RestResult;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemoryController {

    private final MemoryService memoryService;

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/memory")
    public RestResult memory(MemoryRequestDto memoryRequestDto) {
        MemoryResponseDto memoryResponseDto = memoryService.findMemoryByMemoryId(memoryRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("memoryInfoResponse", memoryResponseDto);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/liked-memories")
    public RestResult likedMemories(LikedMemoryRequestDto likedMemoryRequestDto) {
        LikedMemoryResponseDto likedMemoryResponseDto = memoryService.findLikedMemoriesByPetId(likedMemoryRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("likedMemoryInfoResponse", likedMemoryResponseDto);

        return new RestResult(dataMap);
    }

    /**
     * 추억 생성
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping(value = "/memory")
    public RestResult postAMemory(@RequestBody MemoryPostRequestDto memoryPostRequestDTO) {
        MemoryPostResponseDto memoryPostResponseDto = memoryService.postMemoryAndMemoryImages(memoryPostRequestDTO);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("postMemoryResponse", memoryPostResponseDto);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/recent-memories")
    public RestResult mainMemories(RecentMainMemoriesRequestDto recentMainMemoriesRequestDto) {
        RecentMainMemoriesResponseDto recentMainMemoriesResponseDto = memoryService.findMainMemoriesByPetId(recentMainMemoriesRequestDto);
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("recentMemoryInfoResponse", recentMainMemoriesResponseDto);

        return new RestResult(dataMap);
    }


    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/month-memories")
    public RestResult monthMemories(MonthMemoriesRequestDto monthMemoriesRequestDto) {
        MonthMemoriesResponseDto monthMemoriesResponseDto = memoryService.findMonthMemoriesByPetId(monthMemoriesRequestDto);
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("monthMemoryInfoResponse", monthMemoriesResponseDto);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/memory")
    public RestResult deleteMemory(@RequestBody MemoryDeleteRequestDto memoryDeleteRequestDto) throws Exception {
        MemoryDeleteResponseDto memoryDeleteResponseDto  = memoryService.deleteMemory(memoryDeleteRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("memoryDeletionResponse", memoryDeleteResponseDto);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PatchMapping(value="/memory")
    public RestResult updateMemory(@RequestBody MemoryUpdateRequestDto memoryUpdateRequestDto) {
        MemoryUpdateResponseDto memoryUpdateResponseDto = memoryService.updateMemoryInfo(memoryUpdateRequestDto);
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("memoryUpdateResponse", memoryUpdateResponseDto);

        return new RestResult(dataMap);
    }
}
