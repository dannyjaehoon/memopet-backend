package com.memopet.memopet.domain.pet.controller;

import com.memopet.memopet.domain.member.dto.ResponseDto;
import com.memopet.memopet.domain.pet.dto.ImageUpdateRequestDto;
import com.memopet.memopet.domain.pet.dto.ImageUpdateResponseDto;
import com.memopet.memopet.domain.pet.dto.ImageUploadResponseDto;
import com.memopet.memopet.domain.pet.service.ImageUploadService;
import com.memopet.memopet.global.common.dto.RestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImageUploadController {
    private final ImageUploadService imageUploadService;


    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping(value="/image-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestResult imageUpload(@RequestPart MultipartFile file) {
        Map<String, Object> dataMap = new LinkedHashMap<>();

        ImageUploadResponseDto response = imageUploadService.uploadImage(file);
        dataMap.put("imageUploadInfo",response);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping(value="/image-delete")
    public RestResult imageDelete(@RequestParam("imageUrlId") Long imageUrlId ) {
        Map<String, Object> dataMap = new LinkedHashMap<>();

        ResponseDto response = imageUploadService.delete(imageUrlId);
        dataMap.put("imageDeleteInfo",response);

        return new RestResult(dataMap);
    }
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PatchMapping(value="/image-update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestResult imageUpdate(@RequestPart MultipartFile file, @RequestPart ImageUpdateRequestDto imageUpdateRequestDto) {
        Map<String, Object> dataMap = new LinkedHashMap<>();

        ImageUpdateResponseDto response = imageUploadService.update(file, imageUpdateRequestDto);
        dataMap.put("imageUpdateInfo",response);

        return new RestResult(dataMap);
    }
}
