package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetAndMemoryCommentResponseDto {


    private Long petId;

    private String petName;

    private String petProfileUrl;

    private Long commentId;

    private Long commenterId;

    private String comment;

    private LocalDateTime commentCreatedDate;

    private int replyCount;

}
