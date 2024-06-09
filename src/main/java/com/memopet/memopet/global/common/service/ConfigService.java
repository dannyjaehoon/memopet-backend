package com.memopet.memopet.global.common.service;

import com.memopet.memopet.global.common.entity.Config;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.repository.ConfigRepository;
import com.memopet.memopet.global.config.CachingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import static com.memopet.memopet.global.config.CachingConfig.CONFIG_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;

    public Config getConfig(String code) {
        log.info("searching config in db !! {}", code);
        return configRepository.findByCode(code).orElseThrow(() -> new BadRequestRuntimeException("관련 설정 없음 ! " + code));
    }

    // AOP 에 의해서 제어가 된다.
    @Cacheable(value = CONFIG_CACHE, key = "#code")
    public Config getConfigInCache(String code) {
        log.info("searching config in redis !! {}", code);
        return configRepository.findByCode(code).orElseThrow(() -> new BadRequestRuntimeException("관련 설정 없음 ! " + code));
    }
}