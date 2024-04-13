package com.memopet.memopet.domain.pet.controller;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.dto.BlockListRequestDto;
import com.memopet.memopet.domain.pet.dto.BlockListResponseDto;
import com.memopet.memopet.domain.pet.dto.BlockRequestDto;
import com.memopet.memopet.domain.pet.dto.BlockedResponseDto;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.domain.pet.repository.SpeciesRepository;
import com.memopet.memopet.domain.pet.service.BlockedService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/block")
@Validated
public class BlockedController {
    private final BlockedService blockedService;


    /**
     * 차단 리스트
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("")
    public BlockListResponseDto blockedPetList(BlockListRequestDto blockListRequestDto, Authentication authentication) {

        return blockedService.blockedPetList(blockListRequestDto, authentication.getName());
    }

    /**
     * 차단
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping("")
    public BlockedResponseDto BlockAPet(@RequestBody @Valid BlockRequestDto blockRequestDTO, Authentication authentication) {
        return blockedService.blockApet(blockRequestDTO, authentication.getName());
    }

    /**
     * 차단 취소
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("")
    public BlockedResponseDto CancelBlocking(@RequestParam("blockerPetId")Long petId, @RequestParam("blockedPetId") Long blockedPetId, Authentication authentication) {
        return blockedService.unblockAPet(petId, blockedPetId,authentication.getName());
    }



}
