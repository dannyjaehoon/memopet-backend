package com.memopet.memopet.pet;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.*;
import com.memopet.memopet.global.common.entity.Meta;
import com.memopet.memopet.global.common.repository.MetaRepository;
import com.memopet.memopet.global.common.repository.RecentSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;


@SpringBootTest
@Transactional
public class MemoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SpeciesRepository speciesRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    RecentSearchRepository recentSearchRepository;
    @Test
    public void speciesAndRecent_Search() throws Exception {
        //맵버
        Member member = Member.builder()
                .username("Test")
                .password(passwordEncoder.encode("Test1agfagdasgdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
                .email("jae@gmail.com")
                .phoneNum(passwordEncoder.encode("01052888888"))
                .roles("ROLE_USER")
                .activated(true)
                .build();
        Member findmember = memberRepository.save(member);
        //species-
        Species species = Species.builder()
                .largeCategory("포유류")
                .midCategory("개")
                .smallCategory("치와와")
                .build();
        Species findSpecies = speciesRepository.save(species);
        //pet에 species 포함시키기.


        //SPECIES_TEST///////////////

        Pet pet = Pet.builder()
                .member(member)
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
        Pet pet1 = petRepository.save(pet);
        Optional<Species> foundSpecies= speciesRepository.findById(pet1.getSpecies().getId());
        System.out.println("pet's species " + foundSpecies.get().getLargeCategory());


    }

    @Autowired
    MetaRepository metaRepository;
    
    @Test
    public void metaTest() throws Exception {
                Meta meta = Meta.builder()
                .tableName("테이블이름")
                .column_name("컬럼이름")
                .metaData(5)
                .metaDataName("무엇인고")
                .build();

        metaRepository.save(meta);


    }

    @Autowired
    MemoryRepository memoryRepository;
    @Autowired
    MemoryImageRepository memoryImageRepository;

    @Test
    public void memory_memoryImage() throws Exception {
        //맵버
        Member member = Member.builder()
                .username("Test")
                .password(passwordEncoder.encode("Test1agfagdasgdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
                .email("jae@gmail.com")
                .phoneNum(passwordEncoder.encode("01052888888"))
                .roles("ROLE_USER")
                .activated(true)
                .build();
        Member findmember = memberRepository.save(member);
        //species-
        Species species = Species.builder()
                .largeCategory("포유류")
                .midCategory("개")
                .smallCategory("치와와")
                .build();
        Species findSpecies = speciesRepository.save(species);
        //pet에 species 포함시키기.
        Pet pet = Pet.builder()
                .member(member)
                .petStatus(PetStatus.ACTIVE)
                .petName("몬뭉이")
                .gender(Gender.F)
                .species(species)
                .petBirth(LocalDate.of(2020,1,1))
                .petFavs("낮잠")
                .petDesc(
                        "삶은 단순하고 아름다울 때,\n" +
                                "간단한 산책이 세상을 매료시킨다.\n" +
                                "눈부신 햇살, 싱그러운 바람,\n" +
                                "삶의 아름다움은 작은 순간에 담겨 있다.")
                .petProfileUrl("dfdf").build();
        petRepository.save(pet);

        //MEMORY TEST////////////

        Memory memory = Memory.builder()
                .pet(pet)
                .title("meepy life")
                .memoryDate(LocalDateTime.now())
                .audience(Audience.ALL)
                .build();
        memoryRepository.save(memory);

        //MEMORY IMAGE TEST//////////

        MemoryImage memoryImage = MemoryImage.builder()
                .memory(memory)
                .url("https://img.newspim.com/news//2022/11/11/2211111600245171.jpg")
                .imageFormat("JPEG")
                .imageSize("800x600")
                .imageLogicalName("korean_news_image")
                .imagePhysicalName("2022-11-11-news-image.jpg")
                .build();
        memoryImageRepository.save(memoryImage);

    }

    @Autowired
    LikesRepository likesRepository;
    @Autowired
    BlockedRepository blockedRepository;
    @Test
    public void LikeAndBlock() throws Exception {
        //맴버-member, pet, species , memory//////////////////
        Member member = Member.builder()
                .username("Test")
                .password(passwordEncoder.encode("Test1agfagdasgdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
                .email("jae@gmail.com")
                .phoneNum(passwordEncoder.encode("01052888888"))
                .roles("ROLE_USER")
                .activated(true)
                .build();
        Member findmember = memberRepository.save(member);
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
        Memory memory = Memory.builder()
                .pet(pet)
                .title("meepy life")
                .memoryDate(LocalDateTime.now())
                .audience(Audience.ALL)
                .build();
        Memory memory1=memoryRepository.save(memory);

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
        Pet pet2 = petRepository.save(pet1);

        ///LIKES TEST//////////////////////

        Likes likes = Likes.builder()
                .memoryId(memory1)
                .pet(pett)
                .likedOwnPetId(pet2.getId())
                .build();
        Likes likes1 = likesRepository.save(likes);

        //////////BLOCKED//TEST/////////////////////

        Blocked blocked = Blocked.builder()
                .blockedPet(pet2)
                .blockerPetId(pett.getId())
                .build();
        Blocked blocked1 = blockedRepository.save(blocked);


    }
    



}
