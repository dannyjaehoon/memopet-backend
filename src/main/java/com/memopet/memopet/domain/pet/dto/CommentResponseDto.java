package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {

    private Long memoryImageUrlId1;

    private String memoryImageUrl1;

    private Long memoryId;

    private Long commentId;

    private String comment;

    private LocalDateTime commentCreatedDate;

}
