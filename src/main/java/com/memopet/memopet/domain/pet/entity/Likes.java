package com.memopet.memopet.domain.pet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Likes {

    @Id @GeneratedValue
    @Column(name = "likesId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petId", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memoryId", nullable = false)
    private Memory memory;

    @Column(nullable = false)
    private Long likedOwnPetId;

    @CreatedDate @Column(updatable = false)
    private LocalDateTime createdDate;
}
