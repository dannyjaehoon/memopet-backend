package com.memopet.memopet.global.common.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.memopet.memopet.global.common.dto.AccessLogDto;
import com.memopet.memopet.global.common.dto.ImageUploadDto;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadRabbitPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange imageUploadDirectExchange;
    private final ObjectMapper objectMapper;

    /**
     * Queue로 메시지를 발행
     *
     * @param imageUploadDto 발행할 메시지의 DTO 객체
     */
    public void pubsubMessage(ImageUploadDto imageUploadDto) {
        log.info("image upload publisher start");
        String imageUploadDtoStr = "";
        try {
            String jsonMessage = objectMapper.writeValueAsString(imageUploadDto);
            rabbitTemplate.convertAndSend(imageUploadDirectExchange.getName(), "image.upload.key", jsonMessage);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new BadRequestRuntimeException("Problem with converting imageUploadDto to String during the process to upload image");
        }


    }

}
