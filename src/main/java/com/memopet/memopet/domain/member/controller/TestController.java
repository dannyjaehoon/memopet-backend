package com.memopet.memopet.domain.member.controller;

import com.memopet.memopet.global.common.service.AligoSmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {
    private final AligoSmsService aligoSmsService;
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

    @GetMapping("/send-sms")
    public String sendSms(@RequestParam String recipientPhone, @RequestParam String message) {
        return aligoSmsService.sendSms(recipientPhone, message);
    }
}
