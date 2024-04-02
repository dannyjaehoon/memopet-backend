package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.pet.entity.Blocked;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlockListResponseDto {
    private List<Blocked> petList;
    @JsonProperty("dec_code")
    private char decCode;
    private String errorDescription;
}
