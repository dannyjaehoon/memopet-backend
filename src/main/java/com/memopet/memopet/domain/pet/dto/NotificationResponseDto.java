package com.memopet.memopet.domain.pet.dto;

import com.memopet.memopet.domain.pet.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto {


    private Long notificationId;

    private Long receiver;

    private Long sender;

    private NotificationType notificationType;

    private LocalDateTime createdDate;
}
