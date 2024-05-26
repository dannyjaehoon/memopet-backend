package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentMainMemoriesRequestDto {

    private Long petId;

    private int currentPage;

    private int dataCounts;
}
