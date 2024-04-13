package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.MemoryUpdateRequestDto;
import com.memopet.memopet.domain.pet.entity.Audience;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.memopet.memopet.domain.pet.entity.QMemory.memory;

@Repository
public class CustomMemoryRepositoryImpl implements CustomMemoryRepository{

    private final JPAQueryFactory queryFactory;


    public CustomMemoryRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateMemoryInfo(MemoryUpdateRequestDto memoryUpdateRequestDto) {

        Audience audience = null;
        if(memoryUpdateRequestDto.getOpenRestrictionLevel() == 1)  audience = Audience.ALL;
        if(memoryUpdateRequestDto.getOpenRestrictionLevel() == 2)  audience = Audience.FRIEND;
        if(memoryUpdateRequestDto.getOpenRestrictionLevel() == 3)  audience = Audience.ME;


        JPAUpdateClause clause  = queryFactory
                .update(memory);
                if(memoryDescriptionEq(memoryUpdateRequestDto.getMemoryDescription()) != null) {
                    clause.set(memory.memoryDescription, memoryUpdateRequestDto.getMemoryDescription());
                }
                if(memoryDateEq(memoryUpdateRequestDto.getMemoryDate()) != null) {
                    clause.set(memory.memoryDate, memoryUpdateRequestDto.getMemoryDate());
                }
                if(memoryAudienceEq(audience) != null) {
                    clause.set(memory.audience, audience);
                }
                if(memoryTitleEq(memoryUpdateRequestDto.getMemoryTitle()) != null) {
                    clause.set(memory.title, memoryUpdateRequestDto.getMemoryTitle());
                }
                clause.where(memory.id.eq(memoryUpdateRequestDto.getMemoryId()));
                clause.execute();

    }

    private BooleanExpression memoryDescriptionEq(String memoryDescription) {
        return memoryDescription !=null? memory.memoryDescription.eq(memoryDescription) : null;
    }
    private BooleanExpression memoryDateEq(LocalDate memoryDate) {
        return memoryDate !=null? memory.memoryDate.eq(memoryDate) : null;
    }

    private BooleanExpression memoryAudienceEq(Audience audience) {
        return audience !=null? memory.audience.eq(audience) : null;
    }
    private BooleanExpression memoryTitleEq(String title) {
        return title !=null? memory.title.eq(title) : null;
    }


}
