package com.memopet.memopet.global.common.service;

import com.memopet.memopet.global.common.dto.EmailMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailRabbitPublisher {

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;


    /**
     * Queue로 메시지를 발행
     *
     * @param message 발행할 메시지의 DTO 객체
     */
    public void pubsubMessage(EmailMessageDto message){
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", message);
    }

}
