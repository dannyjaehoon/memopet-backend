package com.memopet.memopet.domain.member.repository;

import com.memopet.memopet.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    @Query("select m from Member m where m.email = :email and deletedDate IS NULL")
    Optional<Member> findMemberByEmail(String email);

    @Query("select m from Member m where m.username= :username and m.phoneNum = :phoneNum and deletedDate IS NULL")
    Member findIdByUsernameAndPhoneNum(@Param("username") String username, @Param("phoneNum") String phoneNum);

}
