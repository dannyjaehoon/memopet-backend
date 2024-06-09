package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long>  {

    @Query("select r from Report r where r.reporterPetId = :reporterPetId and reportedPetId = :reportedPetId")
    Optional<Report> findByReportedIdAndReporterId(@Param("reporterPetId") Long reporterPetId, @Param("reportedPetId") Long reportedPetId);
}
