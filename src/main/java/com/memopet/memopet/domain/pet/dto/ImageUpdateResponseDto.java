package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageUpdateResponseDto {
    private Long memoryImageUrlId;
    private String memoryImageUrl;
}
