package com.memopet.memopet.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue
    private Long id;
    // Increase the length to a value that can accommodate your actual token lengths
    @Column(nullable = false, length = 1000)
    private String refreshToken;
    @Column(length = 1000)
    private String accessToken;
    private boolean revoked;
    @Column(updatable = false)
    private LocalDateTime expiredAt;
    @ManyToOne
    @JoinColumn(name = "memberId",referencedColumnName = "id")
    private Member member;

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
