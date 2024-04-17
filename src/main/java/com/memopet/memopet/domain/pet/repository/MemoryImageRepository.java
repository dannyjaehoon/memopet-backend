package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.entity.Memory;
import com.memopet.memopet.domain.pet.entity.MemoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import java.util.Collection;

public interface MemoryImageRepository  extends JpaRepository<MemoryImage, Long>,CustomMemoryImageRepository {

    @Query(value = "select * from memory_image where memory_id = ?1 and deleted_date IS NULL", nativeQuery = true)
    List<MemoryImage> findByMemoryId(Long memoryId);

    @Query(value = "select * from memory_image where memory_id in ?1 and deleted_date IS NULL", nativeQuery = true)
    List<MemoryImage> findByMemoryIds(List<Long> memoryIds);

    @Query(value = "select * from memory_image where memory_id in ?1 and deleted_date IS NULL", nativeQuery = true)
    List<MemoryImage> findOneByMemoryIds(List<Long> memoryIds);

    @Query(value = "select * from memory_image where memory_id = ?1 and deleted_date IS NULL limit 1", nativeQuery = true)
    Optional<MemoryImage> findOneById(Long id);

}
