package com.memopet.memopet.domain.pet.service;



import com.memopet.memopet.domain.pet.dto.ReportPostRequestDto;
import com.memopet.memopet.domain.pet.dto.ReportPostResponseDto;
import com.memopet.memopet.domain.pet.entity.Blocked;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.entity.Report;
import com.memopet.memopet.domain.pet.repository.BlockedRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.domain.pet.repository.ReportRepository;
import com.memopet.memopet.global.common.exception.BadCredentialsRuntimeException;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PetRepository petRepository;
    private final BlockedRepository blockedRepository;

    @Transactional(readOnly = false)
    public ReportPostResponseDto registerReport(ReportPostRequestDto reportPostRequestDto) {
        if(reportPostRequestDto.getReportCategory() == null || reportPostRequestDto.getReportCategory().equals("")) throw new BadCredentialsRuntimeException("Report Category Not Found");
        if(reportPostRequestDto.getReported() == null) throw new BadCredentialsRuntimeException("Reported pet Info Not Found");
        if(reportPostRequestDto.getReporter() == null) throw new BadCredentialsRuntimeException("Reporter pet Info Not Found");

        Optional<Report> reportOptional = reportRepository.findByReportedIdAndReporterId(reportPostRequestDto.getReporter(), reportPostRequestDto.getReported());

        if(reportOptional.isPresent()) throw new BadCredentialsRuntimeException("Reporter already reported the reported Pet");

        Long commentId = reportPostRequestDto.getCommentId();
        Report report = Report.builder()
                .reportCategory(reportPostRequestDto.getReportCategory())
                .reportedPetId(reportPostRequestDto.getReported())
                .reporterPetId(reportPostRequestDto.getReporter())
                .reportReason(reportPostRequestDto.getReportReason())
                .commentId(commentId != null ? commentId : null)
                .createdDate(LocalDateTime.now()).build();


        reportRepository.save(report);

        Optional<Pet> pet = petRepository.findById(reportPostRequestDto.getReported());

        if(pet.isEmpty()) throw new BadRequestRuntimeException("Pet Not Found");
        Blocked blocked = Blocked.builder().blockedPet(pet.get()).createdDate(LocalDateTime.now()).blockerPetId(reportPostRequestDto.getReporter()).build();
        blockedRepository.save(blocked);
        return ReportPostResponseDto.builder().decCode('1').errMsg("").build();
    }
}
