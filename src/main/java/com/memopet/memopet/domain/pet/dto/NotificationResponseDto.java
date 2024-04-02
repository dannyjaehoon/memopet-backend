package com.memopet.memopet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memopet.memopet.domain.pet.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto {

    @JsonProperty("notification_id")
    private Long notificationId;
    @JsonProperty("receiver")
    private Long receiver;
    @JsonProperty("sender")
    private Long sender;
    @JsonProperty("notification_type")
    private NotificationType notificationType;
    @JsonProperty("created_date")
    private LocalDateTime createdDate;
}
