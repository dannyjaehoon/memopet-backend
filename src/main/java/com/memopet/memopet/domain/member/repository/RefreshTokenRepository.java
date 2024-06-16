package com.memopet.memopet.domain.member.repository;

import com.memopet.memopet.domain.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query(value = "select rt.* FROM refresh_token rt where rt.access_token = :accessToken and rt.revoked = false",nativeQuery = true)
    Optional<RefreshToken> findByAccessToken(String accessToken);

    @Query(value = "select rt.* FROM refresh_token rt where rt.member_social_id = :memberId", nativeQuery = true)
    Optional<RefreshToken> findByMemberId(Long memberId);
}
