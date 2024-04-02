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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecentSearchController {

    private final RecentSearchService recentSearchService;

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/search")
    public SearchResponseDTO search(SearchRequestDTO searchRequestDTO) {

        SearchResponseDTO searchResponseDTO = recentSearchService.search(searchRequestDTO);

        return searchResponseDTO;
    }


    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/recent-search")
    public RecentSearchResponseDto findRecentSearch(RecentSearchRequestDto recentSearchRequestDto) {

        RecentSearchResponseDto recentSearchResponseDto = recentSearchService.recentSearch(recentSearchRequestDto);

        return recentSearchResponseDto;
    }


    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/recent-search")
    public RecentSearchDeleteResponseDto deleteRecentSearchText(@RequestBody RecentSearchDeleteRequestDto recentSearchDeleteRequestDto) {

        RecentSearchDeleteResponseDto recentSearchDeleteResponseDto = recentSearchService.deleteRecentSearchText(recentSearchDeleteRequestDto);

        return recentSearchDeleteResponseDto;
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/recent-search/all")
    public RecentSearchDeleteAllResponseDto deleteAllRecentSearchText(@RequestBody RecentSearchDeleteAllRequestDto recentSearchDeleteAllRequestDto) {

        RecentSearchDeleteAllResponseDto recentSearchDeleteAllResponseDto = recentSearchService.deleteAllRecentSearchText(recentSearchDeleteAllRequestDto);

        return recentSearchDeleteAllResponseDto;
    }

}
