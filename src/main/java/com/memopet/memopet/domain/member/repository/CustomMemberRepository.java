package com.memopet.memopet.domain.member.repository;

import com.memopet.memopet.domain.member.dto.MemberInfoRequestDto;
import com.memopet.memopet.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomMemberRepository{

    void UpdateMemberInfo(MemberInfoRequestDto memberInfoRequestDto);
}
