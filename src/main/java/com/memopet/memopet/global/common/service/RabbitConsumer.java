package com.memopet.memopet.global.common.service;

import com.memopet.memopet.global.common.dto.EmailMessageDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RabbitConsumer {

    private final JavaMailSender emailSender;
    private final EmailService emailService;

    /**
     * PubSub Consumer 1
     */
    @RabbitListener(queues = "#{subQueue1.name}")
    public void consumeSub1(EmailMessageDto emailMessageDto){
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

            /**
             *todo 만약 이부분에서 에러가 발생하면 어떻게 처리할지 고민해보세요.
             * 현재 여기서 에러가 발생하면 RabbitMQ로 nack 이 발생하여 다시 큐에 들어가게 됩니다.
             * 만약 매번 같은 오류라면 무한루프가 발생할 수 있습니다. 이 부분에 대해서 고민해봤으면 합니다.
             */
            throw new RuntimeException("이메일 서버 문제");
        }

        //실제 메일 전송
        emailSender.send(message);
    }



    /**
     * PubSub Consumer 2
     */
    @RabbitListener(queues = "#{subQueue2.name}")
    public void consumeSub2(EmailMessageDto emailMessageDto){
        log.info("[Receiver]: {}, AuthCode: {}", emailMessageDto.getEmail(), emailMessageDto.getAuth());
    }
}