package com.memopet.memopet.global.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDTO {

    private Long petId;

    private String searchText;

    private int currentPage;

    private int dataCounts;

    private int currentPage2;

    private int dataCounts2;

    private int desCode;
}
