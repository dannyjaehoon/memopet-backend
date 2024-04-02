package com.memopet.memopet.domain.pet.entity;


import com.memopet.memopet.global.common.entity.FirstCreatedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends FirstCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver")
    private Pet receiver;

    @Column(name = "sender", nullable = false)
    private Long sender;

    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "read_yn", nullable = false)
    private int readYn;

    public void updateReadYN(int readYn) {
        this.readYn = readYn;
    }
}
