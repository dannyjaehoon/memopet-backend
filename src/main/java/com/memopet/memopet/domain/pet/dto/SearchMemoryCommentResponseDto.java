package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchMemoryCommentResponseDto {


    private Long memoryImageUrlId;
    private String memoryImageUrl;
    private Long memoryId;
    private String memoryTitle;
    private String memoryDescription;

}
