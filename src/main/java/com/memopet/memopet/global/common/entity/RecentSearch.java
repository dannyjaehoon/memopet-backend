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
    @Column(name = "recent_search_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "search_text", nullable = false)
    private List<String> searchTexts;

    @CreatedDate
    @Column(name = "created_date",updatable = false)
    private LocalDateTime createdDate;


    public void updateSearchText(List<String> searchTexts) {
        this.searchTexts = searchTexts;
    }
}
