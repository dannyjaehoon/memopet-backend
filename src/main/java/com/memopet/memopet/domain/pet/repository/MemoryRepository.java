package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.MemoryResponseDto;
import com.memopet.memopet.domain.pet.entity.Memory;
import com.memopet.memopet.domain.pet.entity.Pet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemoryRepository extends JpaRepository<Memory, Long> {

    @Override
    @Query(value = "select * from memory where memory_id = ?1 and deleted_date IS NULL", nativeQuery = true)
    Optional<Memory> findById(Long memoryId);

    @Query(value = "select * from memory where memory_id in ?1 and deleted_date IS NULL", nativeQuery = true)
    List<Memory> findByMemoryIds(List<Long> memory_ids);
    @Query(value = "select * from memory where memory_id in ?1 and created_date >= ?2 and deleted_date IS NULL order by created_date desc", nativeQuery = true)
    List<Memory> findByRecentMemoryIds(List<Long> memory_ids, LocalDateTime localDateTime);
    @Query(value = "select * from memory where pet_id in ?1 and deleted_date IS NULL", nativeQuery = true)
    List<Memory> findByPetIds(List<Long> pets);

    @Query(value="select * from memory where pet_id = ?1 and deleted_date IS NULL order by created_date desc limit 1", nativeQuery = true)
    Optional<Memory> findTheRecentMomoryByPetId(Long id);

    @Query(value="select * from memory where pet_id = ?1 and created_date between ?2 and ?3 and deleted_date IS NULL order by created_date desc", nativeQuery = true)
    Page<Memory> findMonthMomoriesByPetId(Long petId, LocalDateTime firstDayOfMonth, LocalDateTime lastDayOfMonth, Pageable pageable);


    Collection<Object> findByPetId(Long petId);
}
