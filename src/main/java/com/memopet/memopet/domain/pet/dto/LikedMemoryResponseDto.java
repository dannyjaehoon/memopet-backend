package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikedMemoryResponseDto {



    private boolean hasNext;

    private int currentPage;

    private int dataCounts;
    private List<MemoryResponseDto> memoryResponseDto;

}
