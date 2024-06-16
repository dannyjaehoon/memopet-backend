package com.memopet.memopet.domain.member.repository;


import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberSocial;
import com.memopet.memopet.domain.pet.entity.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberSocialRepository extends JpaRepository<MemberSocial, UUID>, CustomMemberRepository {

    @Query("select m from MemberSocial m where m.email = :email and deletedDate IS NULL")
    Optional<MemberSocial> findMemberByEmail(String email);

    @Query("select m from MemberSocial m where m.username= :username and m.phoneNum = :phoneNum and deletedDate IS NULL")
    Optional<MemberSocial> findIdByUsernameAndPhoneNum(@Param("username") String username, @Param("phoneNum") String phoneNum);

    @Query("select m from MemberSocial m where m.memberId = :memberId and deletedDate IS NULL")
    List<MemberSocial> findMemberByMemberId(String memberId);
}
