package com.memopet.memopet.domain.pet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Column(name = "memory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "memory_title",nullable = false)
    private String title;

    @Column(name = "memory_date",nullable = false)
    private LocalDate memoryDate;

    @Column(name = "memory_desc")
    private String memoryDescription;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Audience audience;

    public void updateDeleteDate(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

    public void updateLikesCount(int count) {this.likeCount =count;}

}


