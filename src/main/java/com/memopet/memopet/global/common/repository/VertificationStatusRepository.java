package com.memopet.memopet.global.common.repository;


import com.memopet.memopet.global.common.entity.Meta;
import com.memopet.memopet.global.common.entity.VerificationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface VertificationStatusRepository  extends JpaRepository<VerificationStatusEntity,Long> {

}
