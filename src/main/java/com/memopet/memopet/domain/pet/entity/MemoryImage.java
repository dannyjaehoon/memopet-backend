package com.memopet.memopet.domain.pet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MemoryImage {

    @Id @GeneratedValue
    @Column(name = "memoryImageId")
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "memoryId",nullable = false)
    private Memory memory;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String imageFormat;

    @Column(nullable = false)
    private String imageSize;

    @Column(nullable = false)
    private String imagePhysicalName;

    @Column(nullable = false)
    private String imageLogicalName;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime deletedDate;

    public void updateDeletedDate(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

}
