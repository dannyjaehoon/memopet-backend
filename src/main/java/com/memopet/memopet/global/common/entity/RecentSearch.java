package com.memopet.memopet.global.common.entity;


import com.memopet.memopet.domain.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecentSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petId", nullable = false)
    private Pet pet;

    @Column(nullable = false)
    private List<String> searchTexts;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    public void updateSearchText(List<String> searchTexts) {
        this.searchTexts = searchTexts;
    }
}
