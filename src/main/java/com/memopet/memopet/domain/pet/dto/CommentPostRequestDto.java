package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentPostRequestDto {

    private Long parentCommentId; // 부모 댓글 ID

    private Long commenterId; // 댓글 작성자 (pet_id)

    private Long memoryId;

    private Long petId;

    private Long petOwnId;
    private int depth;
    private String comment;

    private int commentGroup;

}
