package com.memopet.memopet.global.common.service;

import com.memopet.memopet.global.common.dto.EmailMessageDto;
import com.rabbitmq.client.Channel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

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
    /**
     * Queue(아직있음) --> (message1) --> Consumer
     * Consumer --> (ack) --> Queue (삭제)
     * Consumer --> (nack) --> Queue (그대로둠.)
     * Queue(아직있음) --> (message1) --> Consumer
     *
     * PubSub Consumer 1
     */
    @RabbitListener(queues = EMAIL_MAIN_QUEUE_1)
    public void consumeSub1(EmailMessageDto emailMessageDto) {
        try {
            if(isSendAlready(emailMessageDto)){ // 멱등성 체크
                return;
            }
            sendEmail(emailMessageDto);

        } catch (Throwable e) {
            log.error(e.getMessage());
            try {
                // 실패한 메시지를 재시도 큐로 전송
                int retryCount = emailMessageDto.getRetryCount();
                emailMessageDto.setRetryCount(retryCount+1);
                rabbitTemplate.convertAndSend(EMAIL_DIRECT_EXCHANGE_NAME, EMAIL_RETRY_QUEUE, emailMessageDto);
                // 메시지를 NACK 처리하여 재시도 큐로 이동
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    @RabbitListener(queues = EMAIL_RETRY_QUEUE)
    public void consumeRetrySub(EmailMessageDto emailMessageDto) {
        log.info("EMAIL_RETRY_QUEUE start");
        int retryCount = emailMessageDto.getRetryCount();

        try {
            if(isSendAlready(emailMessageDto)) return; // 멱등성 체크
            sendEmail(emailMessageDto);
        } catch (Throwable e) {
            log.info("EMAIL_RETRY_QUEUE failed");
            log.error(e.getMessage());
            try {
                // retry : try 5 times then save it in failed_queue
                if (retryCount < MAX_RETRY_COUNT) {
                    log.info("consumeRetrySub retrycount : " + emailMessageDto.getRetryCount());

                    emailMessageDto.setRetryCount(retryCount+1);
                    rabbitTemplate.convertAndSend(EMAIL_DIRECT_EXCHANGE_NAME, EMAIL_RETRY_QUEUE, emailMessageDto);

                } else {
                    log.info("failed queue start");
                    emailMessageDto.setReason(e.getMessage());  // fixme: RabbitMQ 에서 수용가능한 사이즈를 알고 써야하는 지점이 생겼다. ! 왜냐면 모르는 지점. RabbitMQ에서는 256kb까지만 수용가능하다.
                    rabbitTemplate.convertAndSend(EMAIL_DIRECT_EXCHANGE_NAME, EMAIL_FAILED_QUEUE, emailMessageDto);

                    String messageStr = "failed to send an email to email:" + emailMessageDto.getEmail() +", id:" +  emailMessageDto.getId();
                    telegramSender.sendFailureNotification(messageStr);    // fixme 여기서 BadRequestRuntimeException 발생시키면 retry queue로 다시 들어가게 됨
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    // todo: DB에서 확인한다.
    private boolean isSendAlready(EmailMessageDto emailMessageDto) {
        return false;
    }

    private void sendEmail(EmailMessageDto emailMessageDto) {
        String setFrom = "jaelee9212@naver.com"; //email-config에 설정한 자신의 이메일 주소(보내는 사람)
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