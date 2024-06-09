package com.memopet.memopet.domain.member.repository;

import com.memopet.memopet.domain.member.dto.MemberInfoRequestDto;

public interface CustomMemberRepository{

    void UpdateMemberInfo(MemberInfoRequestDto memberInfoRequestDto);
}
