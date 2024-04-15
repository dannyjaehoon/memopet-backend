package com.memopet.memopet.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="REFRESH_TOKENS")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue
    private Long id;
    // Increase the length to a value that can accommodate your actual token lengths
    @Column(nullable = false, length = 10000)
    private String refreshToken;

    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "memberId",referencedColumnName = "id")
    private Member member;

}
