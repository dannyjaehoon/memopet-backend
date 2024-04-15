package com.memopet.memopet.domain.pet.entity;


import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.global.common.entity.FirstCreatedEntity;
import com.memopet.memopet.global.common.entity.LastModifiedEntity;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@QueryEntity
public class Follow extends FirstCreatedEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "petId")
    private Long petId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Pet following;

}
