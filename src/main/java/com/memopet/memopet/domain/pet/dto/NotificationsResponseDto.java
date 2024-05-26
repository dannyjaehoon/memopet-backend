package com.memopet.memopet.domain.pet.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsResponseDto {

    private int currentPage;

    private int dataCounts;

    private boolean hasNext;

    private List<NotificationResponseDto> notifications;
}
