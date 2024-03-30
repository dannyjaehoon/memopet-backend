package com.memopet.memopet.domain.pet.controller;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.dto.BlockRequestDto;
import com.memopet.memopet.domain.pet.dto.BlockListWrapper;
import com.memopet.memopet.domain.pet.dto.BlockeResponseDto;
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
    @GetMapping("/{petId}")
    public BlockListWrapper blockedPetList(@PageableDefault(size = 20,page = 0) Pageable pageable, @PathVariable @Param("blockerPetId") Long petId, Authentication authentication) {

        return blockedService.blockedPetList(pageable,petId, authentication.getName());
    }

    /**
     * 차단
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping("")
    public BlockeResponseDto BlockAPet(@RequestBody @Valid BlockRequestDto blockRequestDTO, Authentication authentication) {
        return blockedService.blockApet(blockRequestDTO, authentication.getName());
    }

    /**
     * 차단 취소
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/{petId}/{blockedPetId}")
    public BlockeResponseDto CancelBlocking(@PathVariable @Param("blockerPetId")Long petId, @PathVariable Long blockedPetId, Authentication authentication) {
        return blockedService.unblockAPet(petId, blockedPetId,authentication.getName());
    }



}
