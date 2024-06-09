package com.memopet.memopet.global.config;

import com.memopet.memopet.global.configproperties.ConfigRabbitMQ;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQEmailSenderFanOutConfig {

    private final ConfigRabbitMQ configRabbitMQ;
    public static final String EMAIL_FANOUT_EXCHANGE_NAME = "pubsub-email-fanout-exchange";
    public static final String EMAIL_DIRECT_EXCHANGE_NAME = "pubsub-email-direct-exchange";
    public static final String EMAIL_MAIN_QUEUE_1 = "main_queue_1";
    public static final String EMAIL_MAIN_QUEUE_2 = "main_queue_2";
    public static final String EMAIL_RETRY_QUEUE = "retry_queue";
    public static final String EMAIL_FAILED_QUEUE = "failed_queue";

    // Subscriber용 큐 2개 생성

    @Bean
    Queue mainQueue1() {
        return new Queue(EMAIL_MAIN_QUEUE_1);
    }
    @Bean
    Queue mainQueue2() {
        return new Queue(EMAIL_MAIN_QUEUE_2);
    }
    @Bean
    Queue retryQueue() {
        return new Queue(EMAIL_RETRY_QUEUE);
    }

    @Bean
    Queue failedQueue() {
        return new Queue(EMAIL_FAILED_QUEUE);
    }

    // FanoutExchange 생성
    @Bean
    public FanoutExchange emailFanoutExchange() {
        return new FanoutExchange(EMAIL_FANOUT_EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange emailDirectExchange() {
        return new DirectExchange(EMAIL_DIRECT_EXCHANGE_NAME);
    }

    // 각 큐에 binding 설정
    @Bean
    public Binding pubsubBinding1(FanoutExchange emailFanoutExchange, Queue mainQueue1) {
        return BindingBuilder.bind(mainQueue1).to(emailFanoutExchange);
    }
    @Bean
    public Binding pubsubBinding2(FanoutExchange emailFanoutExchange, Queue mainQueue2) {
        return BindingBuilder.bind(mainQueue2).to(emailFanoutExchange);
    }
    @Bean
    Binding retryBinding(DirectExchange emailDirectExchange, Queue retryQueue) {
        return BindingBuilder.bind(retryQueue).to(emailDirectExchange).with(EMAIL_RETRY_QUEUE);
    }

    @Bean
    Binding failedBinding(DirectExchange emailDirectExchange, Queue failedQueue) {
        return BindingBuilder.bind(failedQueue).to(emailDirectExchange).with(EMAIL_FAILED_QUEUE);
    }
    /**
     * RabbitMQ 연결을 위한 ConnectionFactory 빈을 생성하여 반환
     *
     * @return ConnectionFactory 객체
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(configRabbitMQ.getHost());
        connectionFactory.setPort(Integer.parseInt(configRabbitMQ.getPort()));
        connectionFactory.setUsername(configRabbitMQ.getUsername());
        connectionFactory.setPassword(configRabbitMQ.getPassword());
        return connectionFactory;
    }

    /**
     * RabbitTemplate을 생성하여 반환
     *
     * @param connectionFactory RabbitMQ와의 연결을 위한 ConnectionFactory 객체
     * @return RabbitTemplate 객체
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // JSON 형식의 메시지를 직렬화하고 역직렬할 수 있도록 설정
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Jackson 라이브러리를 사용하여 메시지를 JSON 형식으로 변환하는 MessageConverter 빈을 생성
     *
     * @return MessageConverter 객체
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
