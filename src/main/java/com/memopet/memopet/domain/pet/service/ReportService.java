package com.memopet.memopet.domain.pet.service;


import com.memopet.memopet.domain.pet.dto.BlockRequestDto;
import com.memopet.memopet.domain.pet.dto.BlockeResponseDto;
import com.memopet.memopet.domain.pet.dto.ReportPostRequestDto;
import com.memopet.memopet.domain.pet.dto.ReportPostResponseDto;
import com.memopet.memopet.domain.pet.entity.Report;
import com.memopet.memopet.domain.pet.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final BlockedService blockedService;

    @Transactional(readOnly = false)
    public ReportPostResponseDto registerReport(ReportPostRequestDto reportPostRequestDto) {

        if(reportPostRequestDto.getReportCategory() == null || reportPostRequestDto.getReportCategory().equals("")) return ReportPostResponseDto.builder().decCode('0').errMsg("신고 카타고리 정보가 없습니다.").build();
        if(reportPostRequestDto.getReported() == null) return ReportPostResponseDto.builder().decCode('0').errMsg("신고당한 펫 id 정보가 없습니다.").build();
        if(reportPostRequestDto.getReporter() == null) return ReportPostResponseDto.builder().decCode('0').errMsg("신고자 정보가 없습니다.").build();

        Optional<Report> reportOptional = reportRepository.findByReportedIdAndReporterId(reportPostRequestDto.getReporter(), reportPostRequestDto.getReported());

        if(reportOptional.isPresent()) return ReportPostResponseDto.builder().decCode('0').errMsg("이미 신고한 이력이 있습니다.").build();

        Long commentId = reportPostRequestDto.getCommentId();
        Report report = Report.builder()
                .reportCategory(reportPostRequestDto.getReportCategory())
                .reportedPetId(reportPostRequestDto.getReported())
                .reporterPetId(reportPostRequestDto.getReporter())
                .reportReason(reportPostRequestDto.getReportReason())
                .commentId(commentId != null ? commentId : null)
                .createdDate(LocalDateTime.now()).build();

        // 첫번째는 블락하는 사람, 두번째는 블락 당하는 사람
        BlockRequestDto blockRequestDto = new BlockRequestDto(reportPostRequestDto.getReporter(), reportPostRequestDto.getReported());
        BlockeResponseDto blockeResponseDto = blockedService.blockApet(blockRequestDto);

        if(blockeResponseDto.getDecCode() == 0) return ReportPostResponseDto.builder().decCode('0').errMsg("차단 등록 오류").build();
        reportRepository.save(report);
        return ReportPostResponseDto.builder().decCode('1').errMsg("").build();
    }

}
