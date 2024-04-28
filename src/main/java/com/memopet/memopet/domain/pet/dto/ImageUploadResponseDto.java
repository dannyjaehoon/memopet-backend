package com.memopet.memopet.domain.pet.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadResponseDto {

    private String memoryImageUrl;

}
