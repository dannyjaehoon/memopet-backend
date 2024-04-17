package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.MemoryUpdateRequestDto;
import com.memopet.memopet.domain.pet.entity.Audience;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.memopet.memopet.domain.pet.entity.QMemory.memory;
import static com.memopet.memopet.domain.pet.entity.QMemoryImage.memoryImage;

@Repository
public class CustomMemoryImageRepositoryImpl implements CustomMemoryImageRepository{

    private final JPAQueryFactory queryFactory;

    public CustomMemoryImageRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public void updateDeletedDate(List<Long> memoryImageIds) {
            queryFactory
                .update(memoryImage)
                .set(memoryImage.deletedDate, LocalDateTime.now())
                .where(memoryImage.id.in(memoryImageIds))
                .execute();
    }
}
