package com.memopet.memopet.global.common.service;


import com.memopet.memopet.global.common.dto.AccessLogDto;
import com.memopet.memopet.global.common.entity.AccessLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ThreadLocalService {

    private ThreadLocal<AccessLogDto> threadLocal = new ThreadLocal<>();

    public void putAccessLog(AccessLogDto accessLogDto) {
        this.threadLocal.set(accessLogDto);
    }

    public AccessLogDto getAccessLog() {
        return this.threadLocal.get();
    }

    public void removeThreadLocal() {
        this.threadLocal.remove();
    }

}
