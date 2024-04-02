package com.memopet.memopet.domain.pet.controller;


import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.service.MemoryService;
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

//    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
//    @DeleteMapping("/memory")
//    public CommentDeleteResponseDto memory(@RequestBody CommentDeleteRequestDto memoryDeleteRequestDto) {
//
//
//        return commentDeleteResponseDto;
//    }

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; //Define the size limit constants-10MB limit


    /**
     * 추억 생성
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping(value = "/memory")
    public MemoryPostResponseDto postAMemory(@RequestPart List<MultipartFile> files, @Valid @RequestBody MemoryPostRequestDto memoryPostRequestDTO) {
        if (files.size() > 10) {
            return MemoryPostResponseDto.builder().decCode('0').build();
        }
        // Check file size and types
        if (fileValidation(files)) return MemoryPostResponseDto.builder().decCode('0').build();

        boolean isPosted = memoryService.postMemoryAndMemoryImages(files, memoryPostRequestDTO);
        return MemoryPostResponseDto.builder().decCode(isPosted ? '1' : '0').build();
    }


    /**
     * (추억)-파일 검증
     */
    private boolean fileValidation(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (file.getSize() > MAX_FILE_SIZE_BYTES || file.isEmpty()) {
                return true;
            }
            String contentType = file.getContentType();
            if (!isValidFileType(contentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (추억)-파일 타입 검증
     */
    private boolean isValidFileType(String contentType) {
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg") || contentType.equals("image/png"));
    }
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/main-memories")
    public LikedMemoryResponseDto mainMemories(LikedMemoryRequestDto likedMemoryRequestDto) {
        LikedMemoryResponseDto likedMemoryResponseDto = memoryService.findMainMemoriesByPetId(likedMemoryRequestDto);
        return likedMemoryResponseDto;
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

//    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
//    @PatchMapping(value="/memory")
//    public MemoryUpdateResponseDto updateMemory(@RequestPart List<MultipartFile> files, @RequestPart(value = "memoryUpdateRequestDto") @Valid MemoryUpdateRequestDto memoryUpdateRequestDto) {
//        MemoryUpdateResponseDto memoryUpdateResponseDto = memoryService.updateMemoryInfo(memoryUpdateRequestDto, files);
//        return memoryUpdateResponseDto;
//    }
}
