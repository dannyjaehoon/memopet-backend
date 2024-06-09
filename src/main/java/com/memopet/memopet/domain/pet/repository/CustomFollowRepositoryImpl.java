package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.BlockedListResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class CustomFollowRepositoryImpl implements CustomFollowRepository{

    private final JPAQueryFactory queryFactory;
    private final BlockedRepository blockedRepository;

    public CustomFollowRepositoryImpl(EntityManager entityManager, BlockedRepository blockedRepository) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.blockedRepository = blockedRepository;
    }

    private Set<Long> getBlockedPetIds(Long petId) {
        Slice<BlockedListResponseDto> blockedPetsPage = blockedRepository.findBlockedPets(petId, PageRequest.of(0, Integer.MAX_VALUE));
        Set<Long> blockedPetIds = new HashSet<>();
        for (BlockedListResponseDto blockedPet : blockedPetsPage.getContent()) {
            blockedPetIds.add(blockedPet.getPetId());
        }
        return blockedPetIds;
    }

//    @Override
//    public Page<PetFollowingResponseDto> findFollowingPetsById(Pageable pageable, Long petId) {
//
//        Set<Long> blockedPetIds = getBlockedPetIds(petId);
//
//        List<FollowListResponseDto> content = queryFactory.select(
//                        Projections.constructor(FollowListResponseDto.class,
//                                follow.followingPet.id,
//                                follow.followingPet.petName,
//                                follow.followingPet.petDesc,
//                                follow.followingPet.petProfileUrl
//                        ))
//                .from(QFollow.follow)
//                .where(QFollow.follow.petId.eq(petId),
//                        follow.followingPet.deletedDate.isNull()
//                                .and(follow.followingPet.id.notIn(blockedPetIds)))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//        JPAQuery<Long> countQuery = queryFactory
//                .select(follow.count())
//                .from(follow)
//                .where(follow.petId.eq(petId).and(follow.followingPet.deletedDate.isNull()));
//        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
//}
//
//    @Override
//    public Page<PetFollowingResponseDto> findFollowerPetsByPetId(Pageable pageable, Long petId) {
//
//        Set<Long> blockedPetIds = getBlockedPetIds(petId);
//
//        List<FollowListResponseDto> content = queryFactory.select(
//                        Projections.constructor(FollowListResponseDto.class,
//                                pet.id,
//                                pet.petName,
//                                pet.petDesc,
//                                pet.petProfileUrl
//                        ))
//                .from(QFollow.follow)
//                .leftJoin(pet).on(follow.petId.eq(pet.id))
//                .where(follow.followingPet.id.eq(petId).and(pet.deletedDate.isNull())
//                        .and(follow.petId.notIn(blockedPetIds)))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        JPAQuery<Long> countQuery = queryFactory
//                .select(follow.count())
//                .from(follow)
//                .leftJoin(pet).on(follow.petId.eq(pet.id))
//                .where(follow.followingPet.id.eq(petId).and(pet.deletedDate.isNull()));
//
//        // Fetch the count directly and wrap it in an Optional
//        Optional<Long> totalOptional = Optional.ofNullable(countQuery.fetchOne());
//
//        // Use orElse to provide a default value if the count is null
//        long totalCount = totalOptional.orElse(0L);
//
//        return new PageImpl<>(content, pageable, totalCount);
//    }

}
