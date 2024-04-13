package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoryResponseDto {
    @JsonProperty("memory_image_url_id_1")
    private Long memoryImageUrlId1;
    @JsonProperty("memory_image_url_1")
    private String memoryImageUrl1;
    @JsonProperty("memory_image_url_id_2")
    private Long memoryImageUrlId2;
    @JsonProperty("memory_image_url_2")
    private String memoryImageUrl2;
    @JsonProperty("memory_image_url_id_3")
    private Long memoryImageUrlId3;
    @JsonProperty("memory_image_url_3")
    private String memoryImageUrl3;
    @JsonProperty("memory_id")
    private Long memoryId;
    @JsonProperty("memory_title")
    private String memoryTitle;
    @JsonProperty("memory_desc")
    private String memoryDescription;
    @JsonProperty("memory_date")
    private LocalDate memoryDate;

}
