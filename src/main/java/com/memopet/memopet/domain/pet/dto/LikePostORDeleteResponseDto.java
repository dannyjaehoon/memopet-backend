package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikePostORDeleteResponseDto {

    @JsonProperty("dec_code")
    private char decCode;
    @JsonProperty("error_msg")
    private String errorMsg;
}
