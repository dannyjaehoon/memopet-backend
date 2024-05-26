package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetAndMemoryCommentsRequestDto {

    private Long petId;
    private Long memoryId;

    private Long commentId;

    private int depth;
    private int commentGroup;
    private int currentPage;
    private int dataCounts;
}
