package com.memopet.memopet.domain.member.repository;

import com.memopet.memopet.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID>, CustomMemberRepository {

    @Query("select m from Member m where m.memberId = :memberId and deletedDate IS NULL")
    Optional<Member> findMemberByMemberId(String memberId);

    @Query("select m from Member m where m.phoneNum = :phoneNum and deletedDate IS NULL")
    Optional<Member> findMemberByPhoneNum(String phoneNum);
}
