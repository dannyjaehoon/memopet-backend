package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.PetFollowingResponseDto;
import com.memopet.memopet.domain.pet.entity.Follow;
import com.memopet.memopet.domain.pet.entity.Pet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> , CustomFollowRepository {
    @Query("SELECT CASE WHEN EXISTS (SELECT f FROM Follow f WHERE f.petId = :petId AND f.following = :followingPetId) THEN true ELSE false END")
    boolean existsByPetIdAndFollowingPetId(@Param("petId") Long petId, @Param("followingPetId") Pet followingPetId);

    @Query("DELETE FROM Follow f where f.petId = :petId AND f.following = :followingPetId")
    void deleteByPetIdAndFollowingPetId(@Param("petId") Long petId, @Param("followingPetId") Pet followingPet);

    @Query("select f from Follow f where f.following = :petId")
    List<Follow> findByPetId(@Param("petId") Pet pet);


    // 팔로우 리스트 조회
    // 조회해야되는 값, 팔로우 한 프로필 정보 + 그 프로필의 팔로우 수 +  팔로우 관계 (무조건 true)
    @Query(value = """
        select count(f.pet_id) as followCnt
                  , max(p.pet_id) as petId
                  , max(p.pet_name) as petName
                  , max(p.pet_desc) as petDesc
                  , max(p.pet_profile_url) as petProfileUrl
                  , 1 as followYn
           from pet as p
                  left join follow as f
                  on p.pet_id = f.pet_id
          where p.pet_id in ( 
                            select p.pet_id
                             from pet as p
                             left join follow as f
                             on p.pet_id = f.pet_id
                             where f.following_pet_id = ?1
                               and p.deleted_date IS NULL
                            )
            group by p.pet_id """
            , nativeQuery = true)
    Slice<PetFollowingResponseDto> findFollowingPetsById( Long petId,Pageable pageable );
    // 팔로워 리스트 조회
    // 조회해야되는 값, 팔로우 한 프로필 정보 + 그 프로필의 팔로우 수 + 팔로우 관계
    @Query(value = "select count(f.pet_id) as followCnt\n" +
            "          , max(p.pet_id) as  petId\n" +
            "          , max(p.pet_name) as petName\n" +
            "          , max(p.pet_desc) as petDesc\n" +
            "          , max(p.pet_profile_url) as petProfileUrl\n" +
            "          , COALESCE((select 1 from follow as f where f.following_pet_id = 2 and f.pet_id= p.pet_id),0) as followYn\n" +
            "\t   from pet as p\n" +
            "          left join follow as f\n" +
            "          on p.pet_id = f.pet_id\n" +
            "\t  where p.pet_id in ( \t\n" +
            "\t\t\t\t\tselect f.following_pet_id\n" +
            "\t\t\t\t\t from pet as p\n" +
            "\t\t\t\t\t left join follow as f\n" +
            "\t\t\t\t\t on p.pet_id = f.pet_id\n" +
            "\t\t\t\t\t where f.pet_id = 2\n" +
            "                      and p.deleted_date IS NULL\n" +
            "\t\t\t\t\t)\n" +
            "\t    group by p.pet_id", nativeQuery = true)
    Slice<PetFollowingResponseDto> findFollowerPetsByPetId( Long petId,Pageable pageable);


}
