package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetAndMemoryCommentReplyResponseDto {

    private Long petId;

    private String petName;

    private String petProfileUrl;

    private Long commentId;

    private Long commenterId;

    private String comment;

    private LocalDateTime commentCreatedDate;
}
