package com.memopet.memopet.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQImageUploadDirectConfig {

    public static final String IMAGE_UPLOAD_Direct_EXCHANGE_NAME = "image.upload.exchange";
    public static final String IMAGE_UPLOAD_DIRECT_QUEUE_NAME = "image.upload.queue";


    /**
     * 1. Exchange 구성합니다.
     * "image.upload..exchange" 라는 이름으로 Direct Exchange 형태로 구성하였습니다.
     *
     * @return DirectExchange
     */
    @Bean
    DirectExchange imageUploadDirectExchange() {
        return new DirectExchange(IMAGE_UPLOAD_Direct_EXCHANGE_NAME);
    }

    /**
     * 2. 큐를 구성합니다.
     * "image.upload.queue"라는 이름으로 큐를 구성하였습니다.
     *
     * @return Queue
     */
    @Bean
    Queue imageUploadDirectQueue() {
        return new Queue(IMAGE_UPLOAD_DIRECT_QUEUE_NAME, false);
    }


    /**
     * 3. 큐와 DirectExchange를 바인딩합니다.
     * "image.upload.key"라는 이름으로 바인딩을 구성하였습니다.
     *
     * @param imageUploadDirectExchange
     * @param imageUploadDirectQueue
     * @return Binding
     */
    @Bean
    Binding imageUploadDirectBinding(DirectExchange imageUploadDirectExchange, Queue imageUploadDirectQueue) {
        return BindingBuilder.bind(imageUploadDirectQueue).to(imageUploadDirectExchange).with("image.upload.key");
    }
}
