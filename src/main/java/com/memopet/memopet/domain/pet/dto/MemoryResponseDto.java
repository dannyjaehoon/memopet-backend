package com.memopet.memopet.domain.pet.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoryResponseDto {

    private Long memoryImageUrlId1;

    private String memoryImageUrl1;

    private Long memoryImageUrlId2;

    private String memoryImageUrl2;

    private Long memoryImageUrlId3;

    private String memoryImageUrl3;

    private Long memoryId;

    private String memoryTitle;

    private String memoryDescription;

    private LocalDate memoryDate;

}
