package com.memopet.memopet.global.common.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.memopet.memopet.domain.member.dto.MemberCreationDto;
import com.memopet.memopet.domain.member.entity.Member;
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
public class MemberCreationRabbitPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange memberDirectExchange;

    /**
     * Queue로 메시지를 발행
     *
     * @param
     */
    public String pubsubMessage() {
        log.info(" MemberCreationRabbitPublisher start");

        return (String) rabbitTemplate.convertSendAndReceive(memberDirectExchange.getName(), "member.create.key", "");
    }

}
