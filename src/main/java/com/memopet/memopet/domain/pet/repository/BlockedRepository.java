package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.BlockedListResponseDto;
import com.memopet.memopet.domain.pet.entity.Blocked;
import com.memopet.memopet.domain.pet.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockedRepository extends JpaRepository<Blocked, Long>, CustomBlockRepository {
//        @Query("SELECT b.blockedPet FROM Blocked b WHERE b.blockerPetId = :blockerPetId")
//        Page<Pet> findBlockedPetsByBlockerPetId(@Param("blockerPetId") Long blockerPetId, Pageable pageable);

        @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM Blocked b WHERE b.blockerPetId = :blockerPetId AND b.blockedPet.id = :blockedPetId) THEN true ELSE false END")
        boolean existsByPetIds(@Param("blockerPetId") Long blockerPetId, @Param("blockedPetId") Long blockedPetId);

        Blocked findByBlockerPetIdAndBlockedPet(@Param("blockerPetId")Long blockerPetId, @Param("blockedPet")Pet blockedPet);

        @Query("select b from Blocked b where b.blockerPetId = :blockerPetId")
        List<Blocked> findBlockedPets(@Param("blockerPetId") Long blockerPetId);
        @Query("select b from Blocked b where b.blockedPet = :blockedPet")
        List<Blocked> findBlockerPets(@Param("blockedPet") Pet blockedPet);

}
