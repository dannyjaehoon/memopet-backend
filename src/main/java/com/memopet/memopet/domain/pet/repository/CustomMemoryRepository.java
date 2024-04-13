package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.MemoryUpdateRequestDto;

public interface CustomMemoryRepository {

    void updateMemoryInfo(MemoryUpdateRequestDto memoryUpdateRequestDto);
}
