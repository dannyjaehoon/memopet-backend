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
public class Blocked {

    @Id @GeneratedValue
    @Column(name = "blockedId")
    private Long id;


    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "blockedPet", referencedColumnName = "pet_id",nullable = false)
    private Pet blockedPet; //blockerPet이랑 blockedPet 바꿈.

    @Column(nullable = false)
    private Long blockerPetId;

    @CreatedDate @Column( nullable = false)
    private LocalDateTime createdDate;

}
