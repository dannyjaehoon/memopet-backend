package com.memopet.memopet.pet;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.RefreshTokenEntity;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.dto.FollowRequestDto;
import com.memopet.memopet.domain.pet.dto.FollowResponseDto;
import com.memopet.memopet.domain.pet.entity.Gender;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.entity.PetStatus;
import com.memopet.memopet.domain.pet.entity.Species;
import com.memopet.memopet.domain.pet.repository.FollowRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.domain.pet.repository.SpeciesRepository;
import com.memopet.memopet.domain.pet.service.FollowService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
public class FollowTest {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    SpeciesRepository speciesRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    FollowService followService;

    @Autowired
    FollowRepository followRepository;
    @Autowired
    EntityManager entityManager;
//    @BeforeEach
//    private void init() {
//
//
//
//        Member member = Member.builder()
//                .username("Test")
//                .password(passwordEncoder.encode("Test1agfagdasgdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
//                .email("jae4@gmail.com")
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
//        Pet pett = petRepository.save(pet);
//
//        //맴버2- member, species, pet//////////////////////
//        Member member1 = Member.builder()
//                .username("this")
//                .password(passwordEncoder.encode("gdasgdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
//                .email("jae5@gmail.com")
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
//                .email("jae6@gmail.com")
//                .phoneNum(passwordEncoder.encode("222s2888888"))
//                .roles("ROLE_USER")
//                .activated(true)
//                .build();
//        Member findmember3 = memberRepository.save(member3);
//        Species species3 = Species.builder()
//                .largeCategory("포유류")
//                .midCategory("고양이")
//                .smallCategory("삼냥이")
//                .build();
//        Species findSpecies3 = speciesRepository.save(species3);
//        Pet pet3 = Pet.builder()
//                .member(findmember3)
//                .petStatus(PetStatus.ACTIVE)
//                .petName("멍뭉이")
//                .gender(Gender.M)
//                .species(species1)
//                .petBirth(LocalDate.of(2024,01,29))
//                .petFavs("singing")
//                .petDesc(
//                        "meep")
//                .petProfileUrl("whatthe").build();
//        petRepository.save(pet3);
//
//
//    }
//
//    @Test
//    public void FollowAPet() throws Exception {
//        //given
//        FollowRequestDto followRequestDTO = new FollowRequestDto(1L, 2L);
//        char result = followService.followAPet(followRequestDTO).getDecCode();
//        Assertions.assertThat(result).isEqualTo('1');
//        FollowRequestDto followRequestDto1 = new FollowRequestDto(1L, 3L);
//        char result2 = followService.followAPet(followRequestDto1).getDecCode();
//        Assertions.assertThat(result2).isEqualTo('1');
//
//        FollowRequestDto followRequestDto2 = new FollowRequestDto(1L, 3L);
//        char result3 = followService.followAPet(followRequestDto1).getDecCode();
//        Assertions.assertThat(result3).isEqualTo('0');
//
//
//    }
//
//    @Test
//    public void followersAndFollowings() throws Exception {
//
//        FollowRequestDto followRequestDto4 = new FollowRequestDto(2L, 3L);
//        followService.followAPet(followRequestDto4);
//        boolean result=followRepository.existsByPetIdAndFollowingPetId(2L, 3L);
//        Assertions.assertThat(result).isTrue();
//
//
//        boolean result2=followRepository.existsByPetIdAndFollowingPetId(3L, 3L);
//        Assertions.assertThat(result2).isFalse();
//
//
//    }
//    @Test
//    public void unfollow() throws Exception {
//        //given
//        FollowRequestDto followRequestDto4 = new FollowRequestDto(2L, 3L);
//        followService.followAPet(followRequestDto4);
//
//        FollowRequestDto followRequestDto2 = new FollowRequestDto(1L, 3L);
//        followService.followAPet(followRequestDto2);
//
////        when
//        boolean result = followRepository.existsByPetIdAndFollowingPetId(1L, 3L); //질저장되었는지 확인
//        Assertions.assertThat(result).isTrue();
//
//        //then
//        followRepository.deleteByPetIdAndFollowingPetId(followRequestDto2.getPetId(), followRequestDto2.getFollowingPetId()); //언팔로우
//        boolean result1 = followRepository.existsByPetIdAndFollowingPetId(1L, 3L); //디비에 없는지 확인
//        Assertions.assertThat(result1).isFalse();
//    }
//    @Test
//    public void unfollowFromService() throws Exception {
//        //given
//        FollowRequestDto followRequestDto4 = new FollowRequestDto(2L, 3L);
//        followService.followAPet(followRequestDto4);
//
//        FollowRequestDto followRequestDto2 = new FollowRequestDto(1L, 3L);
//        followService.followAPet(followRequestDto2);
//
//        //then
//        followService.unfollow(1L,3L);
//        boolean result1 = followRepository.existsByPetIdAndFollowingPetId(1L, 3L); //디비에 없는지 확인
//        Assertions.assertThat(result1).isFalse();
//    }



}
