package com.memopet.memopet.global.common.repository;

import com.memopet.memopet.global.common.entity.AccessLog;
import com.memopet.memopet.global.common.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit, Long> {

}
