package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.member.dto.ResponseDto;
import com.memopet.memopet.domain.pet.dto.ImageUpdateRequestDto;
import com.memopet.memopet.domain.pet.dto.ImageUpdateResponseDto;
import com.memopet.memopet.domain.pet.dto.ImageUploadResponseDto;
import com.memopet.memopet.domain.pet.entity.Memory;
import com.memopet.memopet.domain.pet.entity.MemoryImage;
import com.memopet.memopet.domain.pet.repository.MemoryImageRepository;
import com.memopet.memopet.domain.pet.repository.MemoryRepository;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final S3Uploader s3Uploader;
    private final MemoryRepository memoryRepository;
    private final MemoryImageRepository memoryImageRepository;
    @Transactional(readOnly = false)
    public ImageUploadResponseDto uploadImage(MultipartFile file) {
        String memoryImageUrl = s3Uploader.uploadFileToS3(file, "static/memory-image");

        return ImageUploadResponseDto.builder().memoryImageUrl(memoryImageUrl).build();
    }

    /**
     * (추억 생성)-사진을 DB에 저장 한다.
     */
    private static MemoryImage getMemoryImage(MultipartFile file, String storedMemoryImgUrl, Memory memory) {
        return MemoryImage.builder()
                .imageUrl(storedMemoryImgUrl)
                .memory(memory)
                .imageFormat(file.getContentType())
                .imageSize(String.valueOf(file.getSize()))
                .imageLogicalName(UUID.randomUUID().toString())
                .imagePhysicalName(file.getOriginalFilename())
                .build();
    }

    @Transactional(readOnly = false)
    public ResponseDto delete(Long imageUrlId) {
        Optional<MemoryImage> memoryImageOptional = memoryImageRepository.findById(imageUrlId);
        if(memoryImageOptional.isEmpty()) throw new BadRequestRuntimeException("Memory Image Not Found");
        MemoryImage memoryImage = memoryImageOptional.get();
        memoryImage.updateDeletedDate(LocalDateTime.now());

        s3Uploader.deleteS3(memoryImage.getImageUrl());
        return ResponseDto.builder().dscCode("1").build();
    }

    @Transactional(readOnly = false)
    public ImageUpdateResponseDto update(MultipartFile file, ImageUpdateRequestDto imageUpdateRequestDto) {
        Optional<MemoryImage> memoryImageOptional = memoryImageRepository.findById(imageUpdateRequestDto.getImageUrlId());
        if(memoryImageOptional.isEmpty()) throw new BadRequestRuntimeException("Memory Image Not Found");
        MemoryImage memoryImage = memoryImageOptional.get();
        Optional<Memory> memoryOptional = memoryRepository.findById(Long.valueOf(imageUpdateRequestDto.getMemoryId()));
        if(memoryOptional.isEmpty()) throw new BadRequestRuntimeException("Memory Not Found");
        Memory memory = memoryOptional.get();

        // 저장되어있는 이미지 삭제
        delete(memoryImage.getId());
        
        // 받은 이미지 s3로 업로드
        ImageUploadResponseDto imageUploadResponseDto = uploadImage(file);
        String memoryImageUrl = imageUploadResponseDto.getMemoryImageUrl();

        // s3에 저장한 이미지 정보를 db에 저장한다.
        MemoryImage savedMemoryImage = getMemoryImage(file, memoryImageUrl, memory);
        memoryImageRepository.save(savedMemoryImage);

        return ImageUpdateResponseDto.builder().memoryImageUrl(savedMemoryImage.getImageUrl()).memoryImageUrlId(savedMemoryImage.getId()).build();
    }

}
