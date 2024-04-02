package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchMemoryCommentResponseDto {

    @JsonProperty("memory_image_url_id")
    private Long memoryImageUrlId;
    @JsonProperty("memory_image_url")
    private String memoryImageUrl;
    @JsonProperty("memory_id")
    private Long memoryId;
    @JsonProperty("memory_title")
    private String memoryTitle;
    @JsonProperty("memory_desc")
    private String memoryDescription;

}
