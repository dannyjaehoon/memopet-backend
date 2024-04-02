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
    @JsonProperty("parent_comment_id")
    private Long parentCommentId; // 부모 댓글 ID
    @JsonProperty("commenter_id")
    private Long commenterId; // 댓글 작성자 (pet_id)
    @JsonProperty("memory_id")
    private Long memoryId;
    @JsonProperty("pet_id")
    private Long petId;
    @JsonProperty("pet_own_id")
    private Long petOwnId;
    private int depth;
    private String comment;
    @JsonProperty("comment_group")
    private int commentGroup;

}
