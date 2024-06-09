package com.memopet.memopet.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQMemberCreationDirectConfig {

    public static final String MEMBER_CREATION_DIRECT_EXCHANGE_NAME = "member.create.exchange";
    public static final String MEMBER_CREATION_DIRECT_QUEUE_NAME = "member.create.queue";


    /**
     * 1. Exchange 구성합니다.
     * "member.create.exchange" 라는 이름으로 Direct Exchange 형태로 구성하였습니다.
     *
     * @return DirectExchange
     */
    @Bean
    DirectExchange memberDirectExchange() {
        return new DirectExchange(MEMBER_CREATION_DIRECT_EXCHANGE_NAME);
    }

    /**
     * 2. 큐를 구성합니다.
     * "member.create.queue"라는 이름으로 큐를 구성하였습니다.
     *
     * @return Queue
     */
    @Bean
    Queue memberDirectQueue() {
        return new Queue(MEMBER_CREATION_DIRECT_QUEUE_NAME, false);
    }


    /**
     * 3. 큐와 DirectExchange를 바인딩합니다.
     * "hello.key"라는 이름으로 바인딩을 구성하였습니다.
     *
     * @param memberDirectExchange
     * @param memberDirectQueue
     * @return Binding
     */
    @Bean
    Binding memberDirectBinding(DirectExchange memberDirectExchange, Queue memberDirectQueue) {
        return BindingBuilder.bind(memberDirectQueue).to(memberDirectExchange).with("member.create.key");
    }
}
