package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowListRequestDto {

    private Long petId;

    private int followType; //리스트 조회- 1:팔로워 2:팔로우

    private int currentPage;

    private int dataCounts;

}
