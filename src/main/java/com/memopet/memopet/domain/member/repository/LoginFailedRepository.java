package com.memopet.memopet.domain.member.repository;


import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberSocial;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class LoginFailedRepository {
    
    private final EntityManager em;
    private final MemberSocialRepository memberSocialRepository;


    public void resetCount(MemberSocial member) {
        member.increaseLoginFailCount(0);

    }

    public int increment(MemberSocial member) {
        member.increaseLoginFailCount(member.getLoginFailCount() + 1);
        return member.getLoginFailCount();
    }
    public void changeMemberStatusAndActivation(MemberSocial memberSocial, MemberStatus memberStatus) {
        memberSocial.changeMemberStatus(memberStatus);
    }

}
