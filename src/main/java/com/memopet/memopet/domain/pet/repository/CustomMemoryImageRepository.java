package com.memopet.memopet.domain.pet.repository;


import java.util.List;

public interface CustomMemoryImageRepository {
    void updateDeletedDate(List<Long> memoryImageIds);
}
