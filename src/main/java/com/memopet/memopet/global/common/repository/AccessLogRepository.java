package com.memopet.memopet.global.common.repository;

import com.memopet.memopet.global.common.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
}
