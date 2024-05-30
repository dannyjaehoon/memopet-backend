package com.memopet.memopet.domain.member.repository;

import com.memopet.memopet.domain.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    @Query(value = "SELECT rt.* FROM refresh_token rt " +
            "INNER JOIN USER_DETAILS ud ON rt.user_id = ud.id " +
            "WHERE ud.EMAIL = :userEmail and rt.revoked = false ", nativeQuery = true)
    List<RefreshToken> findAllRefreshTokenByUserEmailId(String userEmail);

    @Query(value = "select rt.* FROM refresh_token rt where rt.access_token = :accessToken",nativeQuery = true)
    Optional<RefreshToken> findByAccessToken(String accessToken);
}
