package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarmCommentUpdateRequestDto {

    private Long commentId;
    private String comment;
}
