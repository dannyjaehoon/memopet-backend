package com.memopet.memopet.domain.pet.controller;

import com.memopet.memopet.domain.pet.dto.MemoryDeleteRequestDto;
import com.memopet.memopet.domain.pet.dto.MemoryDeleteResponseDto;
import com.memopet.memopet.domain.pet.dto.ReportPostRequestDto;
import com.memopet.memopet.domain.pet.dto.ReportPostResponseDto;
import com.memopet.memopet.domain.pet.service.ReportService;
import com.memopet.memopet.global.common.dto.RestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping("/report")
    public RestResult registerReport(@RequestBody ReportPostRequestDto reportPostRequestDto) {
        ReportPostResponseDto reportPostResponseDto = reportService.registerReport(reportPostRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("registerReportResponse", reportPostResponseDto);

        return new RestResult(dataMap);
    }
}
