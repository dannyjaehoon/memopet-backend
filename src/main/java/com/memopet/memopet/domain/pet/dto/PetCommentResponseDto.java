package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetCommentResponseDto {

    private Long petId;
    private String petName;
    private String petProfileUrl;
    private Long commentId;
    private String comment;
    private LocalDateTime commentCreatedDate;

}
