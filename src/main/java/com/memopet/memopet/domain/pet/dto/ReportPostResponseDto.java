package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportPostResponseDto {


    private char decCode;
    private String errMsg;
}
