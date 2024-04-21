package com.memopet.memopet.domain.pet.controller;


import com.memopet.memopet.domain.pet.dto.NotificationDeleteRequestDto;
import com.memopet.memopet.domain.pet.dto.NotificationDeleteResponseDto;
import com.memopet.memopet.domain.pet.dto.NotificationsRequestDto;
import com.memopet.memopet.domain.pet.dto.NotificationsResponseDto;
import com.memopet.memopet.domain.pet.service.NotificationService;
import com.memopet.memopet.global.common.dto.RestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/notification")
    public RestResult cancelNotification(@RequestBody NotificationDeleteRequestDto notificationDeleteRequestDto) {
        NotificationDeleteResponseDto notificationDeleteResponseDto = notificationService.deleteNotificationInfo(notificationDeleteRequestDto.getNotificationId());

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("notificationCancelResponse", notificationDeleteResponseDto);

        return new RestResult(dataMap);
    }

    //@PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")


    //MemberId 값과 "Last-Event-ID"를 받아옵니다.
    //Last-Event-ID는 SSE 연결이 끊어졌을 경우, 클라이언트가 수신한 마지막 데이터의 id 값을 의미합니다.
    //항상 있는 것이 아니기 때문에 required = false 로 설정했습니다.
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam("petId") String id, @RequestHeader(value="Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(id,lastEventId);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/notification")
    public RestResult findUnReadNotifications(NotificationsRequestDto notificationsRequestDto) {

        NotificationsResponseDto notificationsResponseDto = notificationService.createNotificationsSlicePagable(notificationsRequestDto.getPetId(), notificationsRequestDto.getCurrentPage(), notificationsRequestDto.getDataCounts());


        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("findNotificationsResponse", notificationsResponseDto);

        return new RestResult(dataMap);
    }
}
