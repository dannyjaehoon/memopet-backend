package com.memopet.memopet.global.common.service;

import com.memopet.memopet.global.common.dto.EmailMessageDto;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.rabbitmq.client.Channel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.memopet.memopet.global.config.RabbitMQFanOutConfig.*;


@Service
@AllArgsConstructor
@Slf4j
public class EmailRabbitConsumer {
    private static final int MAX_RETRY_COUNT = 5;
    private final JavaMailSender emailSender;
    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;
    private final TelegramSender telegramSender;
    private final Set<String> processedMessages = new ConcurrentSkipListSet<>();

    /**
     * PubSub Consumer 1
     */
    @RabbitListener(queues = EMAIL_MAIN_QUEUE_1)
    public void consumeSub1(EmailMessageDto emailMessageDto, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        // 멱등성 체크 (idempotent)
        String messageId = emailMessageDto.getId();
        if (processedMessages.contains(messageId)) {
            try {
                // channel.basicAck 메서드는 RabbitMQ에서 메시지가 성공적으로 처리되었음을 브로커에게 알리기 위해 사용됨. 이를 통해 메시지가 큐에서 제거
                channel.basicAck(deliveryTag, false);
                return;
            } catch (IOException e) {
                throw new BadRequestRuntimeException("The message is duplicated");
            }
        }

        try {
            sendEmail(emailMessageDto);
            processedMessages.add(emailMessageDto.getId());
            telegramSender.sendFailureNotification("test");
            channel.basicAck(deliveryTag, false);
        } catch (IOException | RuntimeException e) {
            handleFailure(emailMessageDto,channel,deliveryTag);
        }
    }

    private void handleFailure(EmailMessageDto emailMessageDto, Channel channel, long deliveryTag){
        int retryCount = emailMessageDto.getRetryCount();
        if (retryCount < MAX_RETRY_COUNT) {
            log.info("Retry queue start");
            emailMessageDto.setRetryCount(retryCount+1);
            rabbitTemplate.convertAndSend(EMAIL_FANOUT_EXCHANGE_NAME, EMAIL_RETRY_QUEUE, emailMessageDto);
        } else {
            log.info("failed queue start");
            rabbitTemplate.convertAndSend(EMAIL_FANOUT_EXCHANGE_NAME, EMAIL_FAILED_QUEUE, emailMessageDto);

            String message = "failed to send an email to email:" + emailMessageDto.getEmail() +", id:" +  emailMessageDto.getId();
            telegramSender.sendFailureNotification(message);
            emailMessageDto.setRetryCount(0);
        }

        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private void sendEmail(EmailMessageDto emailMessageDto) {
        String setFrom = "jaelee9212naver.com"; //email-config에 설정한 자신의 이메일 주소(보내는 사람)
        String toEmail = emailMessageDto.getEmail(); //받는 사람
        String title = "[이메일 인증 메일]"; //제목
        MimeMessage message = null;

        try {
            message = emailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, toEmail); //보낼 이메일 설정
            message.setSubject(title); //제목 설정
            message.setFrom(setFrom); //보내는 이메일
            message.setText(emailService.getCertificationMessage(emailMessageDto.getAuth()), "utf-8", "html");
        } catch (MessagingException e) {
            log.error("email server occurs an error", e);
            throw new RuntimeException(e.getMessage());
        } catch (MailSendException e) {
            log.error("Wrong Info", e);
            throw new RuntimeException(e.getMessage());
        }

        //실제 메일 전송
        emailSender.send(message);
    }


    /**
     * PubSub Consumer 2
     */
    @RabbitListener(queues = EMAIL_MAIN_QUEUE_2)
    public void consumeSub2(EmailMessageDto emailMessageDto){
        log.info("[Receiver]: {}, AuthCode: {}", emailMessageDto.getEmail(), emailMessageDto.getAuth());
    }
}