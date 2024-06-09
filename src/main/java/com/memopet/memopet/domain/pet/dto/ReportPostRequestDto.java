package com.memopet.memopet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportPostRequestDto {

    private String reportCategory;
    private String reportReason;
    private Long commentId;
    private Long reporter; // 신고한 프로필 (pet_id)
    private Long reported; // 신고당한 프로필 (pet_id)

}
