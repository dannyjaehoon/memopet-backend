package com.memopet.memopet.domain.pet.entity;

import com.memopet.memopet.global.common.entity.FirstCreatedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Memory extends FirstCreatedEntity {
    @Id @GeneratedValue
    @Column(name = "memoryId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petId", nullable = false)
    private Pet pet;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate memoryDate;

    private String memoryDescription;

    private int likeCount;

    private LocalDateTime deletedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Audience audience;

    public void updateDeleteDate(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

    public void updateLikesCount(int count) {this.likeCount =count;}

}


