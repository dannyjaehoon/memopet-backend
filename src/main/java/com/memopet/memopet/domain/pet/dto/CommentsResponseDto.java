package com.memopet.memopet.domain.pet.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentsResponseDto {



    private int totalPages;

    private int currentPage;

    private int dataCounts;
    private List<CommentResponseDto> memoryResponseDto;

}
