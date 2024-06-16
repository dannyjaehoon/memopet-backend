package com.memopet.memopet.domain.member.service;


import com.memopet.memopet.domain.member.dto.DeactivateMemberResponseDto;
import com.memopet.memopet.domain.member.dto.MemberInfoRequestDto;
import com.memopet.memopet.domain.member.dto.MemberInfoResponseDto;
import com.memopet.memopet.domain.member.dto.MemberProfileResponseDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberSocial;
import com.memopet.memopet.domain.member.entity.RefreshToken;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.member.repository.MemberSocialRepository;
import com.memopet.memopet.domain.member.repository.RefreshTokenRepository;
import com.memopet.memopet.domain.pet.entity.Comment;
import com.memopet.memopet.domain.pet.entity.Memory;
import com.memopet.memopet.domain.pet.entity.MemoryImage;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.repository.CommentRepository;
import com.memopet.memopet.domain.pet.repository.MemoryImageRepository;
import com.memopet.memopet.domain.pet.repository.MemoryRepository;
import com.memopet.memopet.global.common.service.S3Uploader;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
@Slf4j
public class MemberService  {

    private final MemberRepository memberRepository;
    private final MemberSocialRepository memberSocialRepository;
    private final CommentRepository commentRepository;
    private final MemoryRepository memoryRepository;
    private final MemoryImageRepository memoryImageRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Uploader s3Uploader;

    private final EntityManager em;

    /**
     * to deactivate the member status
     * @param email
     * @param deactivationReason
     * @param deactivationReasonComment
     * @return
     */
    @Transactional(readOnly = false)
    public DeactivateMemberResponseDto deactivateMember(String email, String deactivationReason, String deactivationReasonComment) {
        Optional<MemberSocial> memberByEmail = memberSocialRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");

        MemberSocial memberSocial = memberByEmail.get();

        Optional<Member> memberByMemberId = memberRepository.findMemberByMemberId(memberSocial.getMemberId());
        Member member = memberByMemberId.get();
        // deactivate the member entity
        member.deactivateMember(LocalDateTime.now(),deactivationReason,deactivationReasonComment, false);

        List<MemberSocial> memberSocials = memberSocialRepository.findMemberByMemberId(member.getMemberId());


        memberSocials.forEach(memorySocial -> {
            memorySocial.deactivateMemberSocial(LocalDateTime.now());

            // expired the refreshtoken
            Optional<RefreshToken> byMemberIdToken = refreshTokenRepository.findByMemberId(memorySocial.getId());
            if(byMemberIdToken.isPresent()) {
                RefreshToken refreshToken = byMemberIdToken.get();
                refreshToken.setRevoked(true);

                refreshTokenRepository.save(refreshToken);
            }

        });

        // find pet info and insert deleted_date
        List<Pet> pets = member.getPets();
        List<Long> petIds = new ArrayList<>();
        for (Pet pet : pets) {
            pet.updateDeletedDate(LocalDateTime.now());
            petIds.add(pet.getId());
        }

        // memory
        List<Memory> memories = memoryRepository.findByPetIds(petIds);
        List<Long> memoryImageIds = new ArrayList<>();
        for (Memory memory : memories) {
            List<MemoryImage> memoryImages = memoryImageRepository.findByMemoryId(memory.getId());

            // delete uploaded images from aws s3.
            for(MemoryImage memoryImage : memoryImages) {
                memoryImageIds.add(memoryImage.getId());
                s3Uploader.deleteS3(memoryImage.getImageUrl());
            }
            memoryImageRepository.updateDeletedDate(memoryImageIds);

            memory.updateDeleteDate(LocalDateTime.now());
        }

        // comment deactivate
        List<Comment> commentsByPetIds = commentRepository.findCommentsByPetIds(pets);
        for (Comment comment : commentsByPetIds) {
            comment.updateDeleteDate(LocalDateTime.now());
        }

        return DeactivateMemberResponseDto.builder().dscCode("1").build();
    }

    public MemberProfileResponseDto getMemberProfile(String email) {

        Optional<MemberSocial> memberByEmail = memberSocialRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");

        MemberSocial memberSocial = memberByEmail.get();

        return MemberProfileResponseDto.builder().email(memberSocial.getEmail()).username(memberSocial.getUsername()).phoneNum(memberSocial.getPhoneNum()).build();
    }

    public MemberInfoResponseDto changeMemberInfo(MemberInfoRequestDto memberInfoRequestDto) {
        Optional<MemberSocial> memberByEmail = memberSocialRepository.findMemberByEmail(memberInfoRequestDto.getEmail());
        if(memberByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");

        memberRepository.UpdateMemberInfo(memberInfoRequestDto);

        em.flush();
        em.clear();

        Optional<MemberSocial> savedMemberByEmail = memberSocialRepository.findMemberByEmail(memberInfoRequestDto.getEmail());
        MemberSocial memberSocial = savedMemberByEmail.get();

        return MemberInfoResponseDto.builder().username(memberSocial.getUsername()).phoneNum(memberSocial.getPhoneNum()).email(memberSocial.getEmail()).build();
    }

    public Optional<MemberSocial> getMemberByEmail(String email) {
        return memberSocialRepository.findMemberByEmail(email);
    }
}
