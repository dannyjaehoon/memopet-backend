package com.memopet.memopet.domain.member.entity;

import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.global.common.entity.FirstCreatedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSocial extends FirstCreatedEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String memberId;
    @Column(nullable = false)
    private String username;
    private String email;
    private String password;
    private String phoneNum;
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;
    private LocalDateTime deletedDate;
    private LocalDateTime lastLoginDate;
    private int loginFailCount;
    private String provider;
    private String providerId;
    @Column(nullable = false)
    private String roles;

    /********** 변경감지용 메서드 **************/
    public void changeMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }
    public void increaseLoginFailCount(int loginFailCount) {
        this.loginFailCount = loginFailCount;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void deactivateMemberSocial(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }
}
