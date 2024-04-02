package com.memopet.memopet.domain.pet.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        log.info("emitterId : " +  emitterId);
        emitters.put(emitterId,sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String emitterId, Object event) {
        eventCache.put(emitterId,event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByPetId(String petId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(petId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithByPetId(String petId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(petId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(String emitterId) {
        emitters.remove(emitterId);

    }

    @Override
    public void deleteAllEmitterStartWithId(String memberId) {
        emitters.forEach(
                (key,emitter) -> {
                    if(key.startsWith(memberId)){
                        emitters.remove(key);
                    }
                }
        );
    }

}
