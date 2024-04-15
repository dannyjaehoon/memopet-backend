package com.memopet.memopet.global.common.repository;

import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.global.common.entity.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {

     RecentSearch findByPet(Pet petId);
     boolean existsByPetId(Pet petId);
}
