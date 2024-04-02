package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.pet.dto.NotificationDeleteResponseDto;
import com.memopet.memopet.domain.pet.dto.NotificationResponseDto;
import com.memopet.memopet.domain.pet.dto.NotificationsResponseDto;
import com.memopet.memopet.domain.pet.entity.NotificationType;
import com.memopet.memopet.domain.pet.entity.Notification;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.repository.EmitterRepository;
import com.memopet.memopet.domain.pet.repository.EmitterRepositoryImpl;
import com.memopet.memopet.domain.pet.repository.NotificationRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    // 기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final NotificationRepository notificationRepository;
    private final PetRepository petRepository;
    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();

    /**
     * 클라이언트가 구독을 위해 호출하는 메서드.
     *
     * @param id - 구독하는 클라이언트의 사용자 아이디.
     * @return SseEmitter - 서버에서 보낸 이벤트 Emitter
     */
    public SseEmitter subscribe(String id,String lastEventId) {
        SseEmitter emitter = createEmitter(id);

        //Sse 연결이 이뤄진 후, 데이터가 하나도 전송되지 않았는데 SseEmitter의 유효시간이 끝나면 503 에러가 발생함
        //따라서 notification에 저장되어있는 읽지 않은 데이터가 없는경우 연결된 id값만 보낸다.
        sendFirstUnReadNotiToClient(emitter, id);

        //Last-Event-ID는 클라이언트가 마지막으로 수신한 데이터의 Id값을 의미합니다.
        // 그러나 Id 값만을 사용한다면 언제 데이터가 보내졌는지, 유실 되었는지 알 수가 없기 때문에 시간을 emitterId에 붙여두면 데이터가 유실된 시점을
        // 알 수 있으므로 저장된 Key값 비교를 통해 유실된 데이터만 재전송 할 수 있습니다.
        // lastEventId값이 있는 경우, 저장된 데이터 캐시에서 유실된 데이터들을 다시 전송합니다.
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByPetId(String.valueOf(id));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    private void sendFirstUnReadNotiToClient(SseEmitter emitter, String id) {
        NotificationsResponseDto notificationsResponseDto = createNotificationsSlicePagable(Long.valueOf(id), 1, 50);

        if(notificationsResponseDto.getNotifications().size() == 0) {
            sendToClient(emitter, id, "EventStream Created. [petId=" + id + "]");
            return;
        }

        sendToClient(emitter, id, notificationsResponseDto);
    }


    // 알림조회
    public NotificationsResponseDto createNotificationsSlicePagable(Long petId, int currentPage, int dataCounts) {
        PageRequest pageRequest = PageRequest.of(currentPage-1, dataCounts, Sort.by("createdDate").descending());
        Optional<Pet> pet = petRepository.findById(petId);

        //slice로 안읽은 50개의 알림을 보내기
        Slice<Notification> slice = notificationRepository.findUnReadNotiByReceiverId(pet.get(), pageRequest);

        List<Notification> notifications = slice.getContent();

        List<NotificationResponseDto> notificationResponseDtos = new ArrayList<>();
        for(Notification notification : notifications) {
            notificationResponseDtos.add(NotificationResponseDto.builder()
                    .notificationId(notification.getId())
                    .receiver(notification.getReceiver().getId())
                    .sender(notification.getSender())
                    .notificationType(notification.getNotificationType())
                    .createdDate(notification.getCreatedDate())
                    .build());
        }

        return NotificationsResponseDto.builder().currentPage(slice.getNumber()+1).dataCounts(slice.getContent().size()).hasNext(slice.hasNext()).notifications(notificationResponseDtos).build();
    }

    /**
     * 서버의 이벤트를 클라이언트에게 보내는 메서드
     * 다른 서비스 로직에서 이 메서드를 사용해 데이터를 Object event에 넣고 전송하면 된다.
     *
     * @param notification - 알림 객체
     */
    public void notify(Notification notification) {
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByPetId(String.valueOf(notification.getReceiver().getId()));

        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);

                    NotificationResponseDto notificationResponseDto = NotificationResponseDto.builder()
                            .notificationId(notification.getId())
                            .notificationType(notification.getNotificationType())
                            .createdDate(notification.getCreatedDate())
                            .receiver(notification.getReceiver().getId())
                            .sender(notification.getSender())
                            .build();

                    sendToClient(emitter, key, notificationResponseDto);
                }
        );
    }

    /**
     * 클라이언트에게 데이터를 전송
     *
     * @param emitterId   - 데이터를 받을 사용자의 아이디.
     * @param data - 전송할 데이터.
     */
    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            // 에러 처리필요
        }
    }

    /**
     * 사용자 아이디를 기반으로 이벤트 Emitter를 생성
     *
     * @param id - 사용자 아이디.
     * @return SseEmitter - 생성된 이벤트 Emitter.
     */
    private SseEmitter createEmitter(String id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        String emitterId = id + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId, emitter);

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        return emitter;
    }
    @Transactional(readOnly = false)
    public Notification saveNotificationInfo(NotificationType notificationType, Pet pet, Long petIdSend) {
        Long result = 1L;

        Notification notification = Notification.builder().receiver(pet).sender(petIdSend).notificationType(notificationType).readYn(1).build();
        notificationRepository.save(notification);

        System.out.println("notification.getID : " + notification.getId());

        // 알림 보내기
        notify(notification);

        return notification;
    }

    @Transactional(readOnly = false)
    public NotificationDeleteResponseDto deleteNotificationInfo(long notificationId) {
        Long result = 1L;

        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);

        if(!notificationOptional.isPresent()) return NotificationDeleteResponseDto.builder().decCode('0').build();

        Notification notification = notificationOptional.get();
        notification.updateReadYN(0);

        return NotificationDeleteResponseDto.builder().decCode('1').build();
    }
}
