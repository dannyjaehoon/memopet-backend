package com.memopet.memopet.global.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // fixme 아래 @Value 를 나열하는 것 보다는 @ConfigurationProperties(prefix = "spring.rabbitmq") 이걸 활용한 클래스를 하나 만들어서 사용하는 것이 좋을 것 같습니다.

    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    @Value("${spring.rabbitmq.queue.name}")
    private String queueName;

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    /**
     * 지정된 큐 이름으로 Queue 빈을 생성
     *
     * @return Queue 빈 객체
     */
    @Bean
    public Queue queue() {
        return new Queue(queueName);
    }

    private static final String FANOUT_EXCHANGE_NAME = "pubsub-exchange";
    private static final String QUEUE_NAME_SUB1 = "sub1";
    private static final String QUEUE_NAME_SUB2 = "sub2";


    // Subscriber용 큐 2개 생성
    @Bean
    public Queue subQueue1() {
        return new Queue(QUEUE_NAME_SUB1, false);
    }
    @Bean
    public Queue subQueue2() {
        return new Queue(QUEUE_NAME_SUB2, false);
    }

    // FanoutExchange 생성
    @Bean
    public FanoutExchange pubsubExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    // 각 큐에 binding 설정
    @Bean
    public Binding pubsubBinding1(FanoutExchange pubsubExchange, Queue subQueue1) {
        return BindingBuilder.bind(subQueue1).to(pubsubExchange);
    }
    @Bean
    public Binding pubsubBinding2(FanoutExchange pubsubExchange, Queue subQueue2) {
        return BindingBuilder.bind(subQueue2).to(pubsubExchange);
    }

    /**
     * RabbitMQ 연결을 위한 ConnectionFactory 빈을 생성하여 반환
     *
     * @return ConnectionFactory 객체
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
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
