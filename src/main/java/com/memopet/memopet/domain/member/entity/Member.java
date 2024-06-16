package com.memopet.memopet.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.global.common.entity.FirstCreatedEntity;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends FirstCreatedEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String memberId;
    @Column(nullable = false)
    private String username;
    private String phoneNum;
    private String deactivationReasonComment;
    private String deactivationReason;
    private LocalDateTime deletedDate;
    @Column(nullable = false)
    private boolean agreeYn;
    @Column(nullable = false)
    private LocalDateTime agreeDate;
    @OneToMany(mappedBy = "member", fetch=FetchType.LAZY)
    private List<Pet> pets = new ArrayList<>();



    /********** 변경감지용 메서드 **************/

    public void deactivateMember(LocalDateTime deletedDate, String deactivationReason, String deactivationReasonComment, boolean activated) {
        this.deletedDate =deletedDate;
        this.deactivationReason = deactivationReason;
        this.deactivationReasonComment = deactivationReasonComment;
    }
    public void setMemberId(String memberId) {
        this.memberId =memberId;
    }
}
