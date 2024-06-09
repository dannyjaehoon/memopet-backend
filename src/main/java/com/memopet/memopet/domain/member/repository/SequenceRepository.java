package com.memopet.memopet.domain.member.repository;

import com.memopet.memopet.domain.member.entity.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SequenceRepository extends JpaRepository<Sequence, String> {
}
