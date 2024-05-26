package com.memopet.memopet.domain.member.repository;

import com.memopet.memopet.domain.member.dto.MemberInfoRequestDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import static com.memopet.memopet.domain.member.entity.QMember.member;

@Repository
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    private final JPAQueryFactory queryFactory;
    private final PasswordEncoder passwordEncoder;

    public CustomMemberRepositoryImpl(EntityManager entityManager, PasswordEncoder passwordEncoder) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void UpdateMemberInfo(MemberInfoRequestDto memberInfoRequestDto) {

        JPAUpdateClause clause  = queryFactory
                .update(member);
        if(memberPasswordEq(passwordEncoder.encode(memberInfoRequestDto.getPassword())) != null) {
            clause.set(member.password, passwordEncoder.encode(memberInfoRequestDto.getPassword()));
        }
        if(memberUsernameEq(memberInfoRequestDto.getUsername()) != null) {
            clause.set(member.username, memberInfoRequestDto.getUsername());
        }
        if(memberPhoneNumEq(memberInfoRequestDto.getPhoneNum()) != null) {
            clause.set(member.phoneNum, memberInfoRequestDto.getPhoneNum());
        }
        clause.where(member.email.eq(memberInfoRequestDto.getEmail()));
        clause.execute();
    }

    private BooleanExpression memberPhoneNumEq(String phoneNum) {
        return phoneNum !=null? member.phoneNum.eq(phoneNum) : null;
    }

    private BooleanExpression memberUsernameEq(String username) {
        return username !=null? member.username.eq(username) : null;
    }

    private BooleanExpression memberPasswordEq(String password) {
        return password !=null? member.password.eq(password) : null;
    }
}
