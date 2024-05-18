package com.memopet.memopet.global.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQDirectConfig {

    public static final String ACCESS_LOG_Direct_EXCHANGE_NAME = "accessLog.exchange";
    public static final String ACCESS_LOG_DIRECT_QUEUE_NAME = "accessLog.queue";


    /**
     * 1. Exchange 구성합니다.
     * "accessLog.exchange" 라는 이름으로 Direct Exchange 형태로 구성하였습니다.
     *
     * @return DirectExchange
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(ACCESS_LOG_Direct_EXCHANGE_NAME);
    }

    /**
     * 2. 큐를 구성합니다.
     * "accessLog.queue"라는 이름으로 큐를 구성하였습니다.
     *
     * @return Queue
     */
    @Bean
    Queue directQueue() {
        return new Queue(ACCESS_LOG_DIRECT_QUEUE_NAME, false);
    }


    /**
     * 3. 큐와 DirectExchange를 바인딩합니다.
     * "hello.key"라는 이름으로 바인딩을 구성하였습니다.
     *
     * @param directExchange
     * @param directQueue
     * @return Binding
     */
    @Bean
    Binding directBinding(DirectExchange directExchange, Queue directQueue) {
        return BindingBuilder.bind(directQueue).to(directExchange).with("hello.key");
    }
}
