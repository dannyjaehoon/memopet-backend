package com.memopet.memopet.domain.pet.entity;


import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.global.common.entity.FirstCreatedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Pet extends FirstCreatedEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_species_id")
    private Species species;

    @Column(name = "pet_name", nullable = false)
    private String petName;

    @Column(name = "pet_birth", nullable = false)
    private LocalDate petBirth;

    @Column(name = "pet_death_date")
    private LocalDate petDeathDate;

    @Column(name = "pet_profile_url", nullable = false)
    private String petProfileUrl;

    @Column(name = "pet_profile_frame", nullable = false)
    private int petProfileFrame;

    @Column(name = "back_img_url")
    private String backImgUrl;

    @Column(name = "pet_favs", nullable = false)
    private String petFavs;

    @Column(name = "pet_favs2")
    private String petFavs2;

    @Column(name = "pet_favs3")
    private String petFavs3;

    @Column(name = "pet_desc", nullable = false)
    private String petDesc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetStatus petStatus;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    /********** 변경감지용 메서드 **************/
    public void updateDeletedDate(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

    public void updateDesc(String petDesc) {
        this.petDesc = petDesc;
    }

    public void updateFav(String petFavs, int dec) {
        if(dec == 1) this.petFavs = petFavs;
        if(dec == 2) this.petFavs2 = petFavs;
        if(dec == 3) this.petFavs3 = petFavs;
    }

    public void updatePetStatus(PetStatus petStatus) {
        this.petStatus = petStatus;
    }
    public void updateName(String petName) {
        this.petName = petName;
    }

    public void updateBirthDate(LocalDate petBirthDate) {
        this.petBirth = petBirthDate;
    }

    public void updateDeathDate(LocalDate petDeathDate) {
        this.petDeathDate = petDeathDate;
    }

    public void updatePetProfileFrame(Integer petProfileFrame) {
        this.petProfileFrame = petProfileFrame;
    }

    public void updateProfileUrl(String storedPetImgName) {
        this.petProfileUrl = storedPetImgName;
    }

    public void updateBackgroundUrl(String storedBackgroundImgName) {
        this.backImgUrl = storedBackgroundImgName;
    }
}
