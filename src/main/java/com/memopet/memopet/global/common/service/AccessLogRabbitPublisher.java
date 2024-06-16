package com.memopet.memopet.global.common.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.memopet.memopet.global.common.dto.AccessLogDto;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessLogRabbitPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange directExchange;
    private final ObjectMapper objectMapper;

    /**
     * Queue로 메시지를 발행
     *
     * @param accessLogDto 발행할 메시지의 DTO 객체
     */
    public void pubsubMessage(AccessLogDto accessLogDto) {
        //log.info("pubsubMessage received");

//        // Java 8에서 추가된 LocalDateTime 항목을 제대로 직렬화 또는 역직렬화를 못하는 현상 때문에 하는 조치
        String accessLogStr = "";
        try {
            accessLogStr = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(accessLogDto);
        } catch (JsonProcessingException e) {
            throw new BadRequestRuntimeException("Problem with converting AccessLogDto to String during the process to save accessLog");
        }

        rabbitTemplate.convertAndSend(directExchange.getName(), "hello.key", accessLogStr);
    }

}
