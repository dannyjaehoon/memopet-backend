package com.memopet.memopet.pet;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.RefreshTokenEntity;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.entity.Gender;
import com.memopet.memopet.domain.pet.entity.Pet;
import com.memopet.memopet.domain.pet.entity.PetStatus;
import com.memopet.memopet.domain.pet.entity.Species;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.domain.pet.repository.SpeciesRepository;
import com.memopet.memopet.global.common.repository.RecentSearchRepository;
import com.memopet.memopet.global.common.service.RecentSearchService;
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
@Rollback(value = false)
public class RecentSearchTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SpeciesRepository speciesRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    RecentSearchService recentSearchService;

    @Autowired
    RecentSearchRepository recentSearchRepository;

    @Test
    public void SaveRecentSearches() throws Exception {
        //given
        Pet pet1 = petRepository.getReferenceById(1L);
//        recentSearchService.addRecentSearch("meep",pet1.getId());
//        recentSearchService.addRecentSearch("쁍쀼쀼븁",pet1.getId());
//
//        RecentSearch recentSearch= recentSearchRepository.findByPet(pet1);
//        for (String text:recentSearch.getSearchText()) {
//            System.out.println("text = " + text);
//        }
//        //when
//        Assertions.assertThat(recentSearch.getSearchText().size()).isEqualTo(2);
//        if (!recentSearchRepository.existsByPetId(pet1)) {
//            RecentSearch recentSearch = RecentSearch.builder()
//                    .pet(pet1).build();
//        }
//        Assertions.assertThat(recentSearchRepository.existsByPetId(pet1));


    }
    @BeforeEach
    public void fromBeginning() throws Exception {
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

    }
}
