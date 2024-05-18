package com.memopet.memopet.domain.member.controller;

import com.memopet.memopet.global.common.service.EmailRabbitPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {
    //private final EmailRabbitPublisher emailRabbitPublisher;

//    @PostMapping("/pubsub")
//    public void pubsubMessage() {
//        EmailMessageDto rabbitMessage = EmailMessageDto.builder().id("1").build();
//
//        IntStream.range(0, 100).forEachOrdered(n -> {
//            rabbitMessage.setId(String.valueOf(n));
//            rabbitPublisher.pubsubMessage(rabbitMessage);
//        });
//    }

    @GetMapping("/test")
    public void test() {
        log.trace("TRACE!!");
        log.debug("DEBUG!!");
        log.info("INFO!!");
        log.warn("WARN!!");
        log.error("ERROR!!");
    }
}
