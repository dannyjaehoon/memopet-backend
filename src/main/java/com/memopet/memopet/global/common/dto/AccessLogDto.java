package com.memopet.memopet.global.common.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessLogDto {
    private Long seq;
    private Long memberId;
    private String email;
    private String threadId;
    private String host;
    private String authorization;
    private String method;
    private String uri;
    private String service;
    private String os;
    private String deviceClass;
    private String agentName;
    private String agentClass;
    private String clientIp;
    private long elapsed;
    private String request;
    private String response;
    private String status;
    private String deviceName;
    private String osName;
    private String osVersion;
    private String userAgent;
    private String referer;
    private String errorId;
    private LocalDateTime requestAt;
    private LocalDateTime responseAt;
    private String requestId;
}
