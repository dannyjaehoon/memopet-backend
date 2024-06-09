package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikePostORDeleteRequestDto {


    private Long memoryId;

    private Long petId;

    private Long myPetId;
}
