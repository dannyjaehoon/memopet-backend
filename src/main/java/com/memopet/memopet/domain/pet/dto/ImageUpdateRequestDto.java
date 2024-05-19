package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageUpdateRequestDto {
    private Long imageUrlId;
    private String memoryId;
}