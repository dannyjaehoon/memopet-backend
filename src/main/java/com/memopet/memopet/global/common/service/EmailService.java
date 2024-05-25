package com.memopet.memopet.global.common.service;

import com.memopet.memopet.global.common.dto.EmailAuthRequestDto;
import com.memopet.memopet.global.common.dto.EmailAuthResponseDto;
import com.memopet.memopet.global.common.dto.EmailMessageDto;
import com.memopet.memopet.global.common.entity.VerificationStatusEntity;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.repository.VertificationStatusRepository;
import com.memopet.memopet.global.common.utils.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final EmailRabbitPublisher emailRabbitPublisher;
    private final VertificationStatusRepository vertificationStatusRepository;

    public void sendRequestToRabbitMqForSendingEmail(long id, String email, String authNum) {
        EmailMessageDto emailMessageDto = EmailMessageDto.builder().auth(authNum).retryCount(0).email(email).id(String.valueOf(id)).build();
        emailRabbitPublisher.pubsubMessage(emailMessageDto);
    }
    @Transactional(readOnly = false)
    //실제 메일 전송
    public EmailAuthResponseDto sendEmail(String toEmail)  {
        // auth num 값 생성
        String authNum = createCode();

        long verificationEntityId = setDataExpire(authNum);

        //메일전송에 필요한 정보 설정
        sendRequestToRabbitMqForSendingEmail(verificationEntityId,toEmail,authNum);
        log.info("authNum : {}", authNum);  // tip 이게 올바른 사용법입니다.
        log.info("authNum : " + authNum);

//        return new EmailAuthResponseDto(authNum, verificationEntityId);   tip 옆처럼 차라리 생성자로 만드는게 나을수도 있기 때문에 한번더 고민이 필요(빌더패턴의 단점인 코드의 양이 많아지게 됨)
        return EmailAuthResponseDto.builder().authCode(authNum).verificationStatusId(verificationEntityId).build();
    }

    private long setDataExpire(String authKey) {
        //Redis에 3분동안 인증코드 {email, authKey} 저장
//        try {
//            redisUtil.setDataExpire(email, authKey,duration);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 에러처리 필요
//        }

        // tip 빌더패턴을 사용하는거면 이렇게 필듭별로 줄바꿈처리를 해주는게 그나마 가독성에 좋을 것 같습니다.
        VerificationStatusEntity verificationStatusEntity = VerificationStatusEntity.builder()
            .expiredAt(LocalDateTime.now().plusMinutes(3))
            .authKey(authKey)
            .build();

        VerificationStatusEntity savedEntity = vertificationStatusRepository.save(verificationStatusEntity);

        // tip 여기에서 값을 리턴하는 이유는 무엇인가요? 프론트엔드로 전달해서 추후에 값을 사용해서 매칭하는데 활용하나요?
        return savedEntity.getId();
    }

    public String getCertificationMessage(String certificationNum) {
        // tip 아래처럼 Text Block 으로 가독성 높게 만들수 있습니다.
        String message = """
            <h1 style='test-align:certer;'>[이메일 인증 코드]</h1>
            <h3 style='test-align:certer;'>인증코드 : <strong style='front-size: 32px; letter-spacing:8px;'>
            %s
            </strong></h3>
            """.formatted(certificationNum);
       return message;
    }

    //랜덤 인증 코드 생성
    public static String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for(int i=0;i<8;i++) {
            int index = random.nextInt(3);

            switch (index) {
                case 0 :
                    key.append((char) ((int)random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int)random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(9));
                    break;
            }
        }
        String authNum = key.toString();
        return authNum;
        // tip 이렇게 멤버변수에 할당하는것보다는 리턴을 받고 활용하는게 나아보입니다. 밖에서 이 메소드를 호출결과로서 랜덤값을 활용할수 있기 때문입니다.
        // tip 오히려 static method 로 유틸성에 가깝기 때문에 따로 클래스로 빼주는게 좋습니다.

    }

    public EmailAuthResponseDto checkVerificationCode(EmailAuthRequestDto emailAuthRequestDto) {
        log.info("email : " + emailAuthRequestDto.getEmail());
        log.info("code : " + emailAuthRequestDto.getConfirmCode());

        //String codeSaved = redisUtil.getValues(email);
        Optional<VerificationStatusEntity> verificationStatus = vertificationStatusRepository.findById(emailAuthRequestDto.getVerificationStatusId());

        if(verificationStatus.isEmpty()) {
            throw new BadRequestRuntimeException("verificationStatusId does not exist");
        }

        VerificationStatusEntity verificationStatusEntity = verificationStatus.get();
        log.info("code : " + verificationStatusEntity.getAuthKey());

        if(LocalDateTime.now().isAfter(verificationStatusEntity.getExpiredAt())) {
            throw new BadRequestRuntimeException("expired");
        }
        if(!emailAuthRequestDto.getConfirmCode().equals(verificationStatusEntity.getAuthKey())) {
            throw new BadRequestRuntimeException("different");
        }
        return EmailAuthResponseDto.builder().build();
    }
}
