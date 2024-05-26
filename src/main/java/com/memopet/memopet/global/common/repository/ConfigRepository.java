package com.memopet.memopet.global.common.repository;

import com.memopet.memopet.global.common.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigRepository extends JpaRepository<Config, Long> {

    Optional<Config> findByCode(String code);
}