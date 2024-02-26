package com.memopet.memopet.pet;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.RefreshTokenEntity;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.controller.BlockedController;
import com.memopet.memopet.domain.pet.dto.BlockDTO;
import com.memopet.memopet.domain.pet.dto.BlockedListDTO;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.BlockedRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.domain.pet.repository.SpeciesRepository;
import com.memopet.memopet.domain.pet.service.BlockedService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest
@Transactional
@Rollback(value = false)
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


    @PostConstruct
    void init() {


        Member member = Member.builder()
                .username("Test")
                .password(passwordEncoder.encode("Test1agfagdasgdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
                .email("jae@gmail.com")
                .phoneNum(passwordEncoder.encode("01052888888"))
                .roles("ROLE_USER")
                .activated(true)
                .build();
        Member findmember = memberRepository.save(member);

        RefreshTokenEntity myToken= (RefreshTokenEntity) findmember.getRefreshTokens();
        System.out.println("myToken = " + myToken);

        Species species = Species.builder()
                .largeCategory("포유류")
                .midCategory("개")
                .smallCategory("치와와")
                .build();
        Species findSpecies = speciesRepository.save(species);
        Pet pet = Pet.builder()
                .member(findmember)
                .petStatus(PetStatus.ACTIVE)
                .petName("몬뭉이")
                .gender(Gender.F)
                .species(findSpecies)
                .petBirth(LocalDate.of(2020,1,1))
                .petFavs("낮잠")
                .petDesc(
                        "삶은 단순하고 아름다울 때,\n" +
                                "간단한 산책이 세상을 매료시킨다.\n" +
                                "눈부신 햇살, 싱그러운 바람,\n" +
                                "삶의 아름다움은 작은 순간에 담겨 있다.")
                .petProfileUrl("dfdf").build();
        Pet pett = petRepository.save(pet);

        //맴버2- member, species, pet//////////////////////
        Member member1 = Member.builder()
                .username("this")
                .password(passwordEncoder.encode("gdasgdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
                .email("jae@gmail.com")
                .phoneNum(passwordEncoder.encode("2222888888"))
                .roles("ROLE_USER")
                .activated(true)
                .build();
        Member findmember1 = memberRepository.save(member1);
        Species species1 = Species.builder()
                .largeCategory("포유류")
                .midCategory("고양이")
                .smallCategory("삼냥이")
                .build();
        Species findSpecies1 = speciesRepository.save(species1);
        Pet pet1 = Pet.builder()
                .member(findmember1)
                .petStatus(PetStatus.ACTIVE)
                .petName("애옹이")
                .gender(Gender.F)
                .species(species1)
                .petBirth(LocalDate.of(2022,01,29))
                .petFavs("깊은잠")
                .petDesc(
                        "피곤하당")
                .petProfileUrl("sdfdgsg").build();
         petRepository.save(pet1);
         //맴버3- member, species, pet//////////////////////
        Member member3 = Member.builder()
                .username("third")
                .password(passwordEncoder.encode("gdasgsdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
                .email("jae@gmail.com")
                .phoneNum(passwordEncoder.encode("222s2888888"))
                .roles("ROLE_USER")
                .activated(true)
                .build();
        Member findmember3 = memberRepository.save(member3);
        Species species3 = Species.builder()
                .largeCategory("포유류")
                .midCategory("고양이")
                .smallCategory("삼냥이")
                .build();
        Species findSpecies3 = speciesRepository.save(species3);
        Pet pet3 = Pet.builder()
                .member(findmember3)
                .petStatus(PetStatus.ACTIVE)
                .petName("멍뭉이")
                .gender(Gender.M)
                .species(species1)
                .petBirth(LocalDate.of(2024,01,29))
                .petFavs("singing")
                .petDesc(
                        "meep")
                .petProfileUrl("whatthe").build();
         petRepository.save(pet3);
    }

    @Test
    public void blockedList() throws Exception {
        Pet blockedPet = petRepository.getReferenceById(1L);
        Pet blockerPet = petRepository.getReferenceById(2L);
        Pet blockedPet2 = petRepository.getReferenceById(3L);
        Assertions.assertThat(blockerPet.getId()).isEqualTo(2L);

        Blocked blocked = Blocked.builder()
                .blockedPet(blockedPet)
                .blockerPetId(blockerPet.getId())
                .build();
        Blocked blocked1 = blockedRepository.save(blocked);

        Blocked blocked2 = Blocked.builder()
                .blockedPet(blockedPet2)
                .blockerPetId(blockerPet.getId())
                .build();
        Blocked blocked22 = blockedRepository.save(blocked2);

        Pageable pageable = PageRequest.of(0, 20);

//        //then
        Page<BlockedListDTO> block = blockedService.blockedPetList(pageable,blockerPet.getId());
        System.out.println("block.getTotalPages() = " + block.getTotalPages());
        System.out.println("block.getTotalElements() = " + block.getTotalElements());
        Assertions.assertThat(block.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(block.getTotalElements()).isEqualTo(2);
        System.out.println("block.getContent().get(0).getPetName() = " + block.getContent().get(0).getPetName());
        System.out.println("block.getContent().get(1).getPetName() = " + block.getContent().get(1).getPetName());

       

    }
    @Test
    public void BlockAPet() throws Exception {
        Pet blockedPet = petRepository.getReferenceById(1L);
        Pet blockerPet = petRepository.getReferenceById(2L);
        Pet blockedPet2 = petRepository.getReferenceById(3L);

        //생성 - 같은 펫이 같은 펫 차단하면 예외처리되는거 확인.
        BlockDTO blockDTO = new BlockDTO(2L, 1L);
        blockedService.blockApet(blockDTO);
        BlockDTO blockDTO2 = new BlockDTO(2L, 3L);
        blockedService.blockApet(blockDTO2);
        //삭제 - 삭제되는거 확인+ 이미 삭제한 펫 다시 차단 못하는것도 확인.
        BlockDTO unblockDTO = new BlockDTO(2L, 3L);
        blockedService.unblockAPet(unblockDTO);
        //내가 블럭한 사람들 리스트
        blockedService.blockApet(blockDTO2);
        BlockDTO blockDTO4 = new BlockDTO(2L, 4L);
        BlockDTO blockDTO5 = new BlockDTO(2L, 5L);
        BlockDTO blockDTO6 = new BlockDTO(2L, 6L);
        blockedService.blockApet(blockDTO4);
        blockedService.blockApet(blockDTO5);
        blockedService.blockApet(blockDTO6);

        Pageable pageable = PageRequest.of(0, 3);
        Page<BlockedListDTO> lists=blockedService.blockedPetList(pageable, 2L);
        for (BlockedListDTO list : lists) {
            System.out.println("list.getPetId() = " + list.getPetId());
            System.out.println("list.getPetName() = " + list.getPetName());
            System.out.println("list.getPetDesc() = " + list.getPetDesc());
            System.out.println("list.getPetProfileUrl() = " + list.getPetProfileUrl());
        }
        Assertions.assertThat(lists.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(lists.getTotalPages()).isEqualTo(2);

        Pageable pageable1 = PageRequest.of(0, 2);
        Page<BlockedListDTO> lists1=blockedService.blockedPetList(pageable1, 2L);
        for (BlockedListDTO list : lists1) {
            System.out.println("list.getPetId() = " + list.getPetId());
            System.out.println("list.getPetName() = " + list.getPetName());
            System.out.println("list.getPetDesc() = " + list.getPetDesc());
            System.out.println("list.getPetProfileUrl() = " + list.getPetProfileUrl());
        }

        Assertions.assertThat(lists1.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(lists1.getTotalPages()).isEqualTo(3);


//        }

    }

    @Autowired BlockedController blockedController;
    @Test
    public void listblockTest() throws Exception {

        BlockDTO blockDTO2 = new BlockDTO(1L, 2L);
        BlockDTO blockDTO3 = new BlockDTO(1L, 3L);
        BlockDTO blockDTO4 = new BlockDTO(1L, 4L);
        BlockDTO blockDTO5 = new BlockDTO(1L, 5L);
        BlockDTO blockDTO6 = new BlockDTO(1L, 6L);
        blockedService.blockApet(blockDTO2);
        blockedService.blockApet(blockDTO3);
        blockedService.blockApet(blockDTO4);
        blockedService.blockApet(blockDTO5);
        blockedService.blockApet(blockDTO6);
        //given
        Page<BlockedListDTO> listDTOS=blockedController.blockedPetList(null,1L);
        //when
        for (BlockedListDTO listDTO : listDTOS) {
            System.out.println("listDTO.getPetId() = " + listDTO.getPetId());
            System.out.println("listDTO.getPetName() = " + listDTO.getPetName());
        }
        
        //then
    
    }
}

