package com.memopet.memopet.global.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.memopet.memopet.domain.member.dto.MemberCreationDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.RefreshToken;
import com.memopet.memopet.domain.member.mapper.MemberInfoMapper;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.member.service.AuthService;
import com.memopet.memopet.global.common.dto.AccessLogDto;
import com.memopet.memopet.global.common.dto.ImageUploadDto;
import com.memopet.memopet.global.common.entity.AccessLog;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.repository.AccessLogRepository;
import com.memopet.memopet.global.common.utils.Utils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.DataInput;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

import static com.memopet.memopet.global.config.RabbitMQMemberCreationDirectConfig.MEMBER_CREATION_DIRECT_QUEUE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberCreationRabbitConsumer {
    private final MemberRepository memberRepository;
    private final MemberInfoMapper memberInfoMapper;
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final EntityManager em;

    @RabbitListener(queues = MEMBER_CREATION_DIRECT_QUEUE_NAME)
    @Transactional(readOnly = false)
    public void consumeSub(String memberCreationDtoStr) throws JsonProcessingException {
        log.info(" MemberCreationRabbitConsumer start");

        MemberCreationDto memberCreationDto = objectMapper.readValue(memberCreationDtoStr, MemberCreationDto.class);

        log.info("step 1");
        String memberId;
        while(true) {
            memberId = generateUniqueId();
            Optional<Member> memberByMemberId = memberRepository.findMemberByMemberId(memberId);
            // looping until we get the uniqueId;
            if(!memberByMemberId.isPresent()) {
                break;
            }
        }
        log.info("step 2");
        memberCreationDto.setMemberId(memberId);

        Member member = memberInfoMapper.convertToEntity(memberCreationDto);
        log.info("step 3");
        Member savedMember = memberRepository.save(member);
        em.flush();
        em.clear();
        log.info("step 4");
        authService.saveRefreshToken(savedMember,memberCreationDto.getAccessToken());
    }

//    @RabbitListener(queues = "#{directQueue.name}")
//    public void consumeSubV2(String memberStr) {
//
//    }

    public String generateUniqueId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateTime = LocalDateTime.now().format(formatter);
        int randomNumber = new Random().nextInt(1000000); // 0 to 999999
        String formattedNumber = String.format("%06d", randomNumber); // zero-padding to 6 digits
        return dateTime + formattedNumber;
    }


}
