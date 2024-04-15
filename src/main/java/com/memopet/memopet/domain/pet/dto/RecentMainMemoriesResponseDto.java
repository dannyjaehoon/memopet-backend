package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentMainMemoriesResponseDto {

    private int totalPage;
    private int currentPage;
    private int dataCounts;
    private List<MemoryResponseDto> memoryResponseDto;

}
