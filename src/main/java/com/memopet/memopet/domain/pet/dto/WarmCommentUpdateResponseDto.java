package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarmCommentUpdateResponseDto {

    private char decCode;
    private String errorMsg;
}
