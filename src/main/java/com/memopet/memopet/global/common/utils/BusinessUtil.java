package com.memopet.memopet.global.common.utils;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessUtil {

    private final PetRepository petRepository;
    private final MemberRepository memberRepository;

    public Member getValidEmail(String email) {
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");
        return memberByEmail.get();
    }

    public void isAccountLock(String email) {
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");

        Member member = memberByEmail.get();
        if(member.getMemberStatus().equals(MemberStatus.LOCKED)) {
            throw new BadRequestRuntimeException("Your account is locked because of 5 failed Login attempts");
        }
    }

    public void isValidEmail(String email) {
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");
    }
}
