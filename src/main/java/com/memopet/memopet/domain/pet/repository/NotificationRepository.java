package com.memopet.memopet.domain.pet.repository;


import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.pet.entity.Notification;
import com.memopet.memopet.domain.pet.entity.Pet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiver(Member member);

    @Query("select n from Notification n where n.receiver = :petId and n.readYn = 1")
    Slice<Notification> findUnReadNotiByReceiverId(Pet petId, Pageable pageable);
}