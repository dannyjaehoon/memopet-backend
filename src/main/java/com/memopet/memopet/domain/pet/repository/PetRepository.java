package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.entity.Pet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PetRepository extends JpaRepository<Pet, Long>,CustomPetRepository  {
    @Query(value="select * from pet where pet_id NOT IN (:petIds) and deleted_date IS NULL", nativeQuery = true)
    List<Pet> findByIdNotIn(@Param("petIds") Set<Long> petIds);

    @Query(value="select * from pet where pet_id in ?1 and deleted_date IS NULL", nativeQuery = true)
    List<Pet> findByIds(Set<Long> petList);
    Optional<Pet> findByIdAndDeletedDateIsNull(Long followingPetId);

    @Query(value = "select * from pet where pet_id not in ?1 and pet_name like %?2% and deleted_date IS NULL", nativeQuery = true)
    Slice<Pet> findPetBySearchText(List<Long> petId, String searchText, Pageable pageable);

    @Query(value = "select p.* from pet as p left join member as m on p.member_id = m.id where m.phoneNum = :phoneNum and m.deleted_date is null and m.deleted_date is null", nativeQuery = true)
    List<Pet> findPetInfoByPhoneNum(@Param("phoneNum") String phoneNum);


}