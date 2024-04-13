package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.PetListResponseDto;
import com.memopet.memopet.domain.pet.entity.PetStatus;
import com.memopet.memopet.domain.pet.entity.QPet;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.memopet.memopet.domain.pet.entity.QPet.pet;

@Repository
public class CustomPetRepositoryImpl implements CustomPetRepository{
    private final JPAQueryFactory queryFactory;


    public CustomPetRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<PetListResponseDto> findPetsById(Long petId) {

        List<PetListResponseDto> content = queryFactory.select(
                        Projections.constructor(PetListResponseDto.class,
                                pet.id,
                                pet.petName,
                                pet.petProfileUrl))
                .from(QPet.pet)
                .where(pet.member.id.eq(
                        JPAExpressions.select(pet.member.id)
                                .from(pet)
                                .where(pet.id.eq(petId))
                        ))
                .where(pet.deletedDate.isNull())
                .orderBy(new CaseBuilder()
                        .when(pet.petStatus.eq(PetStatus.ACTIVE)).then(0)
                        .otherwise(1)
                        .asc())
                .fetch();
        return content;

    }



    @Override
    @Transactional
    public boolean switchPetProfile(Long petId) {

        long updatedCount = queryFactory.update(pet)
                .set(pet.petStatus,
                        Expressions.cases()
                                .when(pet.id.eq(petId)).then(PetStatus.ACTIVE)
                                .when(pet.petStatus.eq(PetStatus.ACTIVE)).then(PetStatus.DEACTIVE)
                                .otherwise(pet.petStatus))
                .where(pet.member.id.eq(JPAExpressions.select(pet.member.id).from(pet).where(pet.id.eq(petId))
                        .where(pet.deletedDate.isNull())
                ))
                .execute();
        return updatedCount > 0;
    }

    @Override
    public boolean deleteAPet(UUID memberId, Long petId) {
        long updatedCount = queryFactory.update(pet)
                .set(pet.deletedDate, LocalDateTime.now())
                .where(pet.id.eq(petId))
                .where(pet.member.id.eq(memberId))
                .where(pet.deletedDate.isNull())
                .execute();
        return updatedCount > 0;
    }


}
