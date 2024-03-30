package com.memopet.memopet.pet;

import com.memopet.memopet.domain.member.controller.AuthController;
import com.memopet.memopet.domain.member.dto.LoginRequestDto;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.entity.RefreshTokenEntity;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.dto.BlockRequestDto;
import com.memopet.memopet.domain.pet.dto.BlockListWrapper;
import com.memopet.memopet.domain.pet.dto.BlockedListResponseDto;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.BlockedRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.domain.pet.repository.SpeciesRepository;
import com.memopet.memopet.domain.pet.service.BlockedService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;


@SpringBootTest
@Transactional
public class BlockedTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SpeciesRepository speciesRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    BlockedRepository blockedRepository;

    @Autowired
    BlockedService blockedService;

    @Autowired
    EntityManager entityManager;




//    @Test
//    public void blockedList() throws Exception {
//        Pet blockedPet = petRepository.getReferenceById(1L);
//        Pet blockerPet = petRepository.getReferenceById(3L);
//        Pet blockedPet2 = petRepository.getReferenceById(2L);
//        Assertions.assertThat(blockerPet.getId()).isEqualTo(3L);
//
//        Blocked blocked = Blocked.builder()
//                .blockedPet(blockedPet)
//                .blockerPetId(blockerPet.getId())
//                .build();
//        Blocked blocked1 = blockedRepository.save(blocked);
//
//        Blocked blocked2 = Blocked.builder()
//                .blockedPet(blockedPet2)
//                .blockerPetId(blockerPet.getId())
//                .build();
//        Blocked blocked22 = blockedRepository.save(blocked2);
//
//        Pageable pageable = PageRequest.of(0, 20);
//
////        //then
//        BlockListWrapper block = blockedService.blockedPetList(pageable,blockerPet.getId());
//        System.out.println("block.getTotalPages() = " + block.getPetList().getTotalPages());
//        System.out.println("block.getTotalElements() = " + block.getPetList().getTotalElements());
//        Assertions.assertThat(block.getPetList().getTotalPages()).isEqualTo(1);
//        Assertions.assertThat(block.getPetList().getTotalElements()).isEqualTo(2);
//        System.out.println("block.getContent().get(0).getPetName() = " + block.getPetList().getContent().get(0).getPetName());
//        System.out.println("block.getContent().get(1).getPetName() = " + block.getPetList().getContent().get(1).getPetName());
//
//
//    }
//    @Test
//    public void BlockAPet() throws Exception {
//        Pet blockedPet = petRepository.getReferenceById(1L);
//        Pet blockerPet = petRepository.getReferenceById(2L);
//        Pet blockedPet2 = petRepository.getReferenceById(3L);
//
//
//        //생성 - 같은 펫이 같은 펫 차단하면 예외처리되는거 확인.
//        BlockRequestDto blockRequestDTO = new BlockRequestDto(2L, 1L);
//        blockedService.blockApet(blockRequestDTO, authentication.getName());
//        BlockRequestDto blockDTO2 = new BlockRequestDto(2L, 3L);
//        blockedService.blockApet(blockDTO2, authentication.getName());
//        //삭제 - 삭제되는거 확인+ 이미 삭제한 펫 다시 차단 못하는것도 확인.
//        blockedService.unblockAPet(2L,3L);
//        //내가 블럭한 사람들 리스트
//        blockedService.blockApet(blockDTO2,authentication.getName());
//        BlockRequestDto blockDTO4 = new BlockRequestDto(2L, 4L);
//        BlockRequestDto blockDTO5 = new BlockRequestDto(2L, 5L);
//        BlockRequestDto blockDTO6 = new BlockRequestDto(2L, 6L);
//        blockedService.blockApet(blockDTO4,authentication.getName());
//        blockedService.blockApet(blockDTO5,authentication.getName());
//        blockedService.blockApet(blockDTO6,authentication.getName());
//
//        Pageable pageable = PageRequest.of(0, 3);
//        BlockListWrapper lists=blockedService.blockedPetList(pageable, 2L,authentication.getName());
//        for (BlockedListResponseDto list : lists.getPetList()) {
//            System.out.println("list.getPetId() = " + list.getPetId());
//            System.out.println("list.getPetName() = " + list.getPetName());
//            System.out.println("list.getPetDesc() = " + list.getPetDesc());
//            System.out.println("list.getPetProfileUrl() = " + list.getPetProfileUrl());
//        }
//        Assertions.assertThat(lists.getPetList().getTotalElements()).isEqualTo(5);
//        Assertions.assertThat(lists.getPetList().getTotalPages()).isEqualTo(2);
//
//        Pageable pageable1 = PageRequest.of(0, 2);
//        BlockListWrapper lists1=blockedService.blockedPetList(pageable1, 2L, authentication.getName());
//        for (BlockedListResponseDto list : lists1.getPetList()) {
//            System.out.println("list.getPetId() = " + list.getPetId());
//            System.out.println("list.getPetName() = " + list.getPetName());
//            System.out.println("list.getPetDesc() = " + list.getPetDesc());
//            System.out.println("list.getPetProfileUrl() = " + list.getPetProfileUrl());
//        }
//
//        Assertions.assertThat(lists1.getPetList().getTotalElements()).isEqualTo(5);
//        Assertions.assertThat(lists1.getPetList().getTotalPages()).isEqualTo(3);
//
//    }
//
//
//    @Test
//    public void listblockTest() throws Exception {
//
//        BlockRequestDto blockDTO2 = new BlockRequestDto(1L, 2L);
//        BlockRequestDto blockDTO3 = new BlockRequestDto(1L, 3L);
//        BlockRequestDto blockDTO4 = new BlockRequestDto(1L, 4L);
//        BlockRequestDto blockDTO5 = new BlockRequestDto(1L, 5L);
//        BlockRequestDto blockDTO6 = new BlockRequestDto(1L, 6L);
//        blockedService.blockApet(blockDTO2,authentication.getName());
//        blockedService.blockApet(blockDTO3,authentication.getName());
//        blockedService.blockApet(blockDTO4,authentication.getName());
//        blockedService.blockApet(blockDTO5,authentication.getName());
//        blockedService.blockApet(blockDTO6,authentication.getName());
//
//
//        Page<BlockedListResponseDto> blockList = blockedRepository.findBlockedPets(1L, Pageable.ofSize(20));
//        for (BlockedListResponseDto block: blockList) {
//            System.out.println("block.getBlockedPet().getId() = " + block.getPetId());
//            System.out.println("block.getBlockedPet().getPetName() = " + block.getPetName());
//        }
//        Assertions.assertThat(blockList.getTotalElements()).isEqualTo(5);
        //given
//        BlockListWrapper listDTOS=blockedController.blockedPetList(null,1L);
//        //when
//        for (BlockedListResponseDTO listDTO : listDTOS.getPetList()) {
//            System.out.println("listDTO.getPetId() = " + listDTO.getPetId());
//            System.out.println("listDTO.getPetName() = " + listDTO.getPetName());
//        }

    }
//    @PostConstruct
//    private void init() {
//
//
//        Member member = Member.builder()
//                .username("Test")
//                .password(passwordEncoder.encode("Test1agfagdasgdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
//                .email("jae777@gmail.com")
//                .phoneNum(passwordEncoder.encode("01052888888"))
//                .roles("ROLE_USER")
//                .activated(true)
//                .build();
//        Member findmember = memberRepository.save(member);
//
//        RefreshTokenEntity myToken= (RefreshTokenEntity) findmember.getRefreshTokens();
//        System.out.println("myToken = " + myToken);
//
//        Species species = Species.builder()
//                .largeCategory("포유류")
//                .midCategory("개")
//                .smallCategory("치와와")
//                .build();
//        Species findSpecies = speciesRepository.save(species);
//        Pet pet = Pet.builder()
//                .member(findmember)
//                .petStatus(PetStatus.ACTIVE)
//                .petName("몬뭉이")
//                .gender(Gender.F)
//                .species(findSpecies)
//                .petBirth(LocalDate.of(2020,1,1))
//                .petFavs("낮잠")
//                .petDesc(
//                        "삶은 단순하고 아름다울 때,\n" +
//                                "간단한 산책이 세상을 매료시킨다.\n" +
//                                "눈부신 햇살, 싱그러운 바람,\n" +
//                                "삶의 아름다움은 작은 순간에 담겨 있다.")
//                .petProfileUrl("dfdf").build();
//        //second pet-참순이
//        Species species2 = Species.builder()
//                .largeCategory("포유류")
//                .midCategory("개")
//                .smallCategory("치와와")
//                .build();
//        Species findSpecies2 = speciesRepository.save(species2);
//        Pet pet2 = Pet.builder()
//                .member(findmember)
//                .petStatus(PetStatus.DEACTIVE)
//                .petName("참순이")
//                .gender(Gender.F)
//                .species(findSpecies2)
//                .petBirth(LocalDate.of(2020, 1, 1))
//                .petFavs("장난치기")
//                .petDesc(
//                        "니가 손을 달라고 한다면 나는 네게 내 뒷다리를 주겠당.")
//                .petProfileUrl("dfdf").build();
//        petRepository.save(pet2);
//
//        //third pet-참돌이
//        Species species3 = Species.builder()
//                .largeCategory("포유류")
//                .midCategory("개")
//                .smallCategory("진돗개")
//                .build();
//        Species findSpecies3 = speciesRepository.save(species3);
//        Pet pet3 = Pet.builder()
//                .member(findmember)
//                .petStatus(PetStatus.DEACTIVE)
//                .petName("참돌이")
//                .gender(Gender.M)
//                .species(findSpecies3)
//                .petBirth(LocalDate.of(2020, 1, 1))
//                .petFavs("낮잠")
//                .petDesc(
//                        "으르렁!!!")
//                .petProfileUrl("dfdf").build();
//        petRepository.save(pet3);
//
//        //4번 미피
//        Species species4 = Species.builder()
//                .largeCategory("포유류")
//                .midCategory("개")
//                .smallCategory("치와와")
//                .build();
//        Species findSpecies4 = speciesRepository.save(species4);
//        Pet pet4 = Pet.builder()
//                .member(findmember)
//                .petStatus(PetStatus.DEACTIVE)
//                .petName("미피")
//                .gender(Gender.F)
//                .species(findSpecies4)
//                .petBirth(LocalDate.of(2024, 1, 1))
//                .petFavs("짖기")
//                .petDesc(
//                        "MEEP!!!!!")
//                .petProfileUrl("MEEPy").build();
//        petRepository.save(pet4);
//
//        //5번 루피
//        Species species5 = Species.builder()
//                .largeCategory("포유류")
//                .midCategory("비버")
//                .smallCategory("비버")
//                .build();
//        Species findSpecies5 = speciesRepository.save(species5);
//        Pet pet5 = Pet.builder()
//                .member(findmember)
//                .petStatus(PetStatus.DEACTIVE)
//                .petName("루피")
//                .gender(Gender.F)
//                .species(findSpecies5)
//                .petBirth(LocalDate.of(2024, 1, 1))
//                .petFavs("짖기")
//                .petDesc(
//                        "루피!!!!!")
//                .petProfileUrl("루피").build();
//        petRepository.save(pet5);
//
//
//        //맴버2- member, species, pet//////////////////////
//        Member member1 = Member.builder()
//                .username("this")
//                .password(passwordEncoder.encode("gdasgdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
//                .email("jae111@gmail.com")
//                .phoneNum(passwordEncoder.encode("2222888888"))
//                .roles("ROLE_USER")
//                .activated(true)
//                .build();
//        Member findmember1 = memberRepository.save(member1);
//        Species species1 = Species.builder()
//                .largeCategory("포유류")
//                .midCategory("고양이")
//                .smallCategory("삼냥이")
//                .build();
//        Species findSpecies1 = speciesRepository.save(species1);
//        Pet pet1 = Pet.builder()
//                .member(findmember1)
//                .petStatus(PetStatus.ACTIVE)
//                .petName("애옹이")
//                .gender(Gender.F)
//                .species(species1)
//                .petBirth(LocalDate.of(2022,01,29))
//                .petFavs("깊은잠")
//                .petDesc(
//                        "피곤하당")
//                .petProfileUrl("sdfdgsg").build();
//        petRepository.save(pet1);
//        //맴버3- member, species, pet//////////////////////
//        Member member3 = Member.builder()
//                .username("third")
//                .password(passwordEncoder.encode("gdasgsdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
//                .email("jae100@gmail.com")
//                .phoneNum(passwordEncoder.encode("222s2888888"))
//                .roles("ROLE_USER")
//                .activated(true)
//                .build();
//        Member findmember31 = memberRepository.save(member3);
//        Species species31 = Species.builder()
//                .largeCategory("포유류")
//                .midCategory("고양이")
//                .smallCategory("삼냥이")
//                .build();
//        Species findSpecies31 = speciesRepository.save(species31);
//        Pet pet31 = Pet.builder()
//                .member(findmember31)
//                .petStatus(PetStatus.ACTIVE)
//                .petName("멍뭉이")
//                .gender(Gender.M)
//                .species(species1)
//                .petBirth(LocalDate.of(2024,01,29))
//                .petFavs("singing")
//                .petDesc(
//                        "meep")
//                .petProfileUrl("whatthe").build();
//        petRepository.save(pet31);

//    }


