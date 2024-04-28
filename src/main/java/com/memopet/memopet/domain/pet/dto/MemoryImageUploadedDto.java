package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoryImageUploadedDto {

    private String imageFormat;
    private String imagePhysicalName;
    private String imageSize;
    private String imageUrl;
}
