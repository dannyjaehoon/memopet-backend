package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.pet.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;
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
