package com.memopet.memopet.global.filter;

import com.memopet.memopet.global.common.dto.AccessLogDto;
import com.memopet.memopet.global.common.service.AccessLogRabbitPublisher;
import com.memopet.memopet.global.common.service.ThreadLocalService;
import com.memopet.memopet.global.common.service.UserAgentService;
import com.memopet.memopet.global.common.utils.BusinessUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.memopet.memopet.global.common.utils.Utils.toJson;

@Slf4j
@RequiredArgsConstructor
public class AccessLogFilter implements Filter {

    private final BusinessUtil businessUtill;
    private final ThreadLocalService threadLocalService;
    private final UserAgentService userAgentService;
    private final AccessLogRabbitPublisher accessLogRabbitPublisher;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Filter Init");
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis(); // 시작시간
        LocalDateTime requestDate = LocalDateTime.now();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String userAgentStr = httpRequest.getHeader("User-Agent");
        UserAgent userAgent = userAgentService.parseUserAgent(userAgentStr);

        AccessLogDto accessLogDto = AccessLogDto.builder()
                .host(httpRequest.getRemoteHost())
                .clientIp(httpRequest.getRemoteAddr())
                .userAgent(userAgentStr)
                .os(businessUtill.getOsInfo(httpRequest))
                .deviceClass(userAgent.getValue("DeviceClass"))
                .deviceName(userAgent.getValue("DeviceName"))
                .osName(userAgent.getValue("OperatingSystemName"))
                .osVersion(userAgent.getValue("OperatingSystemVersion"))
                .agentName(userAgent.getValue("AgentName"))
                .agentClass(userAgent.getValue("AgentClass"))
                .uri(httpRequest.getRequestURI())
                .method(httpRequest.getMethod())
                .request(httpRequest.toString())
                .response(response.toString())
                .requestAt(requestDate)
                .referer(httpRequest.getHeader("Referer"))
                .build();

        threadLocalService.putAccessLog(accessLogDto);

        chain.doFilter(request,response);

        long timeGap = System.currentTimeMillis() - startTime;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        AccessLogDto accessLogDtoAfterService = threadLocalService.getAccessLog();

        accessLogDtoAfterService.setElapsed(timeGap);
        accessLogDtoAfterService.setResponseAt(LocalDateTime.now());
        accessLogDtoAfterService.setStatus(String.valueOf(httpServletResponse.getStatus()));
        accessLogDtoAfterService.setResponse(String.valueOf(httpServletResponse));

        accessLogRabbitPublisher.pubsubMessage(accessLogDtoAfterService);
        log.info("Filter 끝");
        threadLocalService.removeThreadLocal();
    }

    @Override
    public void destroy() {
        log.info("Filter destroy");
        Filter.super.destroy();
    }
}
