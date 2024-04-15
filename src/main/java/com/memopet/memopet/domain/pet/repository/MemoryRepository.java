package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.MemoryResponseDto;
import com.memopet.memopet.domain.pet.entity.Memory;
import com.memopet.memopet.domain.pet.entity.Pet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemoryRepository extends JpaRepository<Memory, Long>, CustomMemoryRepository {

    @Override
    @Query(value = "select * from memory where memory_id = ?1 and deleted_date IS NULL", nativeQuery = true)
    Optional<Memory> findById(Long memoryId);

    @Query(value = "select * from memory where memory_id in ?1 and deleted_date IS NULL", nativeQuery = true)
    List<Memory> findByMemoryIds(List<Long> memory_ids);

    @Query(value = "select * from memory where pet_id in ?1 and deleted_date IS NULL", nativeQuery = true)
    List<Memory> findByPetIds(List<Long> pets);

    @Query(value="select * from memory where pet_id = ?1 and deleted_date IS NULL order by created_date desc limit 1", nativeQuery = true)
    Optional<Memory> findTheRecentMomoryByPetId(Long id);

    @Query(value="select distinct m.* from (select m.* from memory as m where 1= 1 and m.audience = 'ALL' and m.created_date between ?2 and ?3 and m.deleted_date IS NULL union all select m.* from memory as m left join follow as f on m.pet_id = f.following_pet_id where 1= 1 and f.following_pet_id = ?1 and m.audience = 'FRIEND' and m.created_date between ?2 and ?3 and m.deleted_date IS NULL) m order by created_date desc",countQuery = "select count(distinct m.memory_id) from (select m.* from memory as m where 1= 1 and m.audience = 'ALL' and m.created_date between ?2 and ?3 and m.deleted_date IS NULL union all select m.* from memory as m left join follow as f on m.pet_id = f.following_pet_id where 1= 1 and f.following_pet_id = ?1 and m.audience = 'FRIEND' and m.created_date between ?2 and ?3 and m.deleted_date IS NULL) m order by created_date desc",nativeQuery = true)
    Page<Memory> findMonthMomoriesByPetId(Long petId, LocalDateTime firstDayOfMonth, LocalDateTime lastDayOfMonth,Pageable pageable);

    @Query(value = "select m.* from memory as m left join follow as f on m.pet_id = f.pet_id where 1= 1 and m.pet_id not in ?1 and f.following_pet_id = ?3 and m.audience != 'ME' and m.created_date >=?2  and m.deleted_date IS NULL order by m.created_date desc",countQuery = "select count(m.memory_id) from memory as m left join follow as f on m.pet_id = f.pet_id where 1= 1 and m.pet_id not in ?1 and f.following_pet_id = ?3 and m.audience != 'ME' and m.created_date >=?2  and m.deleted_date IS NULL order by m.created_date desc", nativeQuery = true)
    Page<Memory> findByRecentMemoryIdsWithPagination(List<Long> blockedPetIds, LocalDateTime localDateTime,Long id, PageRequest pageRequest);
    @Query(value = "select m.* from memory as m left join follow as f on m.pet_id = f.pet_id where 1= 1 and f.following_pet_id = ?2 and m.audience != 'ME' and m.created_date >=?1  and m.deleted_date IS NULL order by m.created_date desc",countQuery = "select count(m.memory_id) from memory as m left join follow as f on m.pet_id = f.pet_id where 1= 1 and f.following_pet_id = ?2 and m.audience != 'ME' and m.created_date >=?1  and m.deleted_date IS NULL order by m.created_date desc",nativeQuery = true)
    Page<Memory> findByRecentMemoryIdsWithPaginationWithoutBlockedPetList(LocalDateTime localDateTime, Long id, PageRequest pageRequest);


    @Query(value = "select distinct m.* from memory as m where m.pet_id not in ?1 and m.audience != 'ME' and (m.memory_title like %?2% or m.memory_desc like %?2%) and m.deleted_date IS NULL", nativeQuery = true)
    Slice<Memory> findMemoryBySearchText(List<Long> petIds, String searchText, Long petId, PageRequest pageRequest);

    @Query(value = "select distinct m.* from memory as m where 1= 1 and m.memory_id in :memoryIds and m.audience != 'ME' and m.deleted_date IS NULL order by created_date desc", nativeQuery = true)
    Slice<Memory> findByMemoryIdsWithSlice(@Param("memoryIds") List<Long> memoryIds, PageRequest pageRequest);


}