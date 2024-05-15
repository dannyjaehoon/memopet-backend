package com.memopet.memopet.global.common.controller;

import com.memopet.memopet.domain.pet.dto.CommentPostRequestDto;
import com.memopet.memopet.domain.pet.dto.CommentPostResponseDto;
import com.memopet.memopet.global.common.dto.*;
import com.memopet.memopet.global.common.service.RecentSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecentSearchController {

    private final RecentSearchService recentSearchService;

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/search")
    public RestResult search(SearchRequestDTO searchRequestDTO) {
        SearchResponseDTO searchResponseDTO = recentSearchService.search(searchRequestDTO);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("searchResponse", searchResponseDTO);

        return new RestResult(dataMap);
    }


    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/recent-search")
    public RestResult findRecentSearch(RecentSearchRequestDto recentSearchRequestDto) {

        RecentSearchResponseDto recentSearchResponseDto = recentSearchService.recentSearch(recentSearchRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("findRecentSearchResponse", recentSearchResponseDto);

        return new RestResult(dataMap);
    }


    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/recent-search")
    public RestResult deleteRecentSearchText(@RequestBody RecentSearchDeleteRequestDto recentSearchDeleteRequestDto) {

        RecentSearchDeleteResponseDto recentSearchDeleteResponseDto = recentSearchService.deleteRecentSearchText(recentSearchDeleteRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("deleteRecentSearchTextResponse", recentSearchDeleteResponseDto);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/recent-search/all")
    public RestResult deleteAllRecentSearchText(@RequestBody RecentSearchDeleteAllRequestDto recentSearchDeleteAllRequestDto) {

        RecentSearchDeleteAllResponseDto recentSearchDeleteAllResponseDto = recentSearchService.deleteAllRecentSearchText(recentSearchDeleteAllRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("deleteAllRecentSearchTextResponse", recentSearchDeleteAllResponseDto);

        return new RestResult(dataMap);
    }

}
