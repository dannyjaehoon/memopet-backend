package com.memopet.memopet.global.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.memopet.memopet.global.common.dto.AccessLogDto;
import com.memopet.memopet.global.common.entity.AccessLog;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.repository.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.memopet.memopet.global.common.utils.Utils.toJson;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessLogRabbitConsumer {
    private final AccessLogRepository accessLogRepository;
    private final ModelMapper  modelMapper;

    @RabbitListener(queues = "#{directQueue.name}")
    @Transactional(readOnly = false)
    public void consumeSub(String accessLogDtoStr) {
        log.info("consumeSub received");
        ObjectMapper mapper = new ObjectMapper();
        AccessLogDto accessLogDto;
        try {
            accessLogDto = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(accessLogDtoStr, AccessLogDto.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestRuntimeException(e.getMessage());
        }

        log.info(toJson(accessLogDto));

        AccessLog accesslog = modelMapper.map(accessLogDto, AccessLog.class);
        log.info(toJson(accesslog));
        accessLogRepository.save(accesslog);
    }
}
