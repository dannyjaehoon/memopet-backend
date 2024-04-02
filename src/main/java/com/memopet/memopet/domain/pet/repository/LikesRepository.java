package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.LikesPerPetDto;
import com.memopet.memopet.domain.pet.entity.Likes;
import com.memopet.memopet.domain.pet.entity.Memory;
import com.memopet.memopet.domain.pet.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LikesRepository extends JpaRepository<Likes, Long> {


    @Query(value="select pet_id from likes where liked_own_pet_id = ?1", nativeQuery = true)
    List<Long> findLikesListByPetId(Long petId);

    @Query(value="select pet_id AS petId, COUNT(pet_id) AS likes from likes where pet_id in ?1 group by pet_id", nativeQuery = true)
    List<LikesPerPetDto> findLikesByPetIds(Set<Long> pet_ids);

    @Query("select l from Likes l where l.pet = :petId and l.likedOwnPetId = :myPetId and l.memoryId = :memoryId")
    Optional<Likes> findByPetIdAndLikedOwnPetIdAndMemoryID(@Param("petId") Pet pet, @Param("myPetId") long myPetId,@Param("memoryId") Memory memoryId );

    @Query(value="select * from likes where pet_id = ?1", nativeQuery = true)
    List<Likes> findLikesByPetId(Long id);
}
