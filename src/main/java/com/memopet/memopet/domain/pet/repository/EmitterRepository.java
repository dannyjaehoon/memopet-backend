package com.memopet.memopet.domain.pet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;


public interface EmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    void saveEventCache(String emitterId, Object event);

    Map<String, SseEmitter> findAllEmitterStartWithByPetId(String petId);
    Map<String,Object> findAllEventCacheStartWithByPetId(String petId);

    void deleteById(String emitterId);
    void deleteAllEmitterStartWithId(String emitterId);
}
