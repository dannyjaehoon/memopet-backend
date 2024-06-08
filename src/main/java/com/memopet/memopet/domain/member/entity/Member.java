package com.memopet.memopet.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.global.common.entity.FirstCreatedEntity;
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
    private static final long serialVersionUID = 174726374856727L;

    @Id @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String email;
    private String password;
    private String phoneNum;
    private String deactivationReasonComment;
    private String deactivationReason;
    private int loginFailCount;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    private LocalDateTime deletedDate;

    @Embedded //해당 클라스에 @Embeddable을 붙여줘야됨
    private Address address;

    @JsonIgnore
    @Column(nullable = false)
    private boolean activated;

    @Column(nullable = false)
    private String roles;


    @BatchSize(size = 10) // Batch size를 지정한다
    @Builder.Default
    @OneToMany(mappedBy = "member", fetch=FetchType.LAZY)
    private List<Pet> pets = new ArrayList<>();

    private String provider; //어떤 OAuth인지(google, naver 등)

    private String provideId; // 해당 OAuth 의 key(id)

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RefreshTokenEntity> refreshTokens;

    /********** 변경감지용 메서드 **************/
    // todo 변경하면 확인할 수 있도록 AuditLog 테이블을 추가해 주시는게 좋을것 같아요. (변경전, 변경후, 변경일시, 변경자 등의) 정보가 들어가야 합니다.
    public void increaseLoginFailCount(int loginFailCount) {
        this.loginFailCount = loginFailCount;
    }
    public void changeActivity(boolean isActivated) {
        this.activated = isActivated;
    }

    public void changeMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }
    public void changePassword(String password) {
        this.password = password;
    }

    public void deactivateMember(LocalDateTime deletedDate, String deactivationReason, String deactivationReasonComment, boolean activated) {
        this.deletedDate =deletedDate;
        this.deactivationReason = deactivationReason;
        this.deactivationReasonComment = deactivationReasonComment;
        this.activated = activated;
    }

    public void changeUsername(String username) {
        this.username = username;
    }

    public void changePhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
