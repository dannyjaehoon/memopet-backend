package com.memopet.memopet.pet;

import com.memopet.memopet.domain.member.controller.AuthController;
import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.RefreshTokenEntity;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.controller.MemoryController;
import com.memopet.memopet.domain.pet.dto.MemoryPostRequestDto;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.MemoryImageRepository;
import com.memopet.memopet.domain.pet.repository.MemoryRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.domain.pet.repository.SpeciesRepository;
import com.memopet.memopet.domain.pet.service.MemoryService;
import jakarta.annotation.PostConstruct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
public class MemoryAndImageTest {
    @Autowired
    MemoryService memoryService;

    @Autowired
    MemoryRepository memoryRepository;

    @Autowired
    MemoryController memoryController;

    @Autowired
    MemoryImageRepository memoryImageRepository;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SpeciesRepository speciesRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    AuthController authController;
    @PostConstruct
    private void init() {


        Member member = Member.builder()
                .username("Test")
                .password(passwordEncoder.encode("Test1agfagdasgdasgdgasydgasgdygasyugdsyugayudgasuydugasudgsauyg23"))
                .email("jae7@gmail.com")
                .phoneNum(passwordEncoder.encode("01052888888"))
                .roles("ROLE_USER")
                .activated(true)
                .build();
        Member findmember = memberRepository.save(member);

        RefreshTokenEntity myToken = (RefreshTokenEntity) findmember.getRefreshTokens();
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
                .petBirth(LocalDate.of(2020, 1, 1))
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
                .email("jae8@gmail.com")
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
                .petBirth(LocalDate.of(2022, 01, 29))
                .petFavs("깊은잠")
                .petDesc(
                        "피곤하당")
                .petProfileUrl("sdfdgsg").build();
        petRepository.save(pet1);
    }

@Test
public void PostAMemoryOnly() throws Exception {
    MemoryPostRequestDto memoryPostRequestDTO = new MemoryPostRequestDto(1L, "삼일절!! 집콕여행", LocalDateTime.now(), "날씨가 매우 추워서 양말을 신어야했다 :( 이것이 바로 꽃샘추위", Audience.FRIEND);
    Memory MemoryId=memoryService.createAMemory(memoryPostRequestDTO);
    System.out.println("MemoryId = " + MemoryId);
    MemoryPostRequestDto memoryPostRequestDto2 = new MemoryPostRequestDto(1L, "비오는 날", LocalDateTime.of(2024,2,20,6,4), "추워서 뼈가 시림", Audience.ALL);
    Memory MemoryId2=memoryService.createAMemory(memoryPostRequestDto2);
    System.out.println("MemoryId = " + MemoryId2);
    MemoryPostRequestDto memoryPostRequestDto3 = new MemoryPostRequestDto(1L, "카사독 마지막날", LocalDateTime.of(2024,02,29,12,0), "사요나라 휴먼뜨 :(", Audience.ME);
    Memory MemoryId3=memoryService.createAMemory(memoryPostRequestDto3);
    System.out.println("MemoryId = " + MemoryId3);
    Long petId = 1L;
    Pageable pageable = Pageable.ofSize(20);
//    Page<Memory> memories = memoryRepository.findMemoriesByPetId(petId, pageable);
//    Assertions.assertThat(memories.getTotalElements()).isEqualTo(3);
//    MemoryPostRequestDto memoryPostRequestDto4 = new MemoryPostRequestDto(2L, "카사독 마지막날", LocalDateTime.of(2024,02,29,12,0), "사요나라 휴먼뜨 :(", Audience.ME);
//    Memory MemoryId4=memoryService.createAMemory(memoryPostRequestDto4);
//    System.out.println("MemoryId = " + MemoryId4);
//
//    Assertions.assertThat(memoryRepository.findAll().size()).isEqualTo(4);

}

//@Test
//public void memoryAndImages() throws Exception {
//
//    MemoryPostRequestDto memoryPostRequestDTO = new MemoryPostRequestDto(1L, "삼일절!! 집콕여행", LocalDateTime.now(), "날씨가 매우 추워서 양말을 신어야했다 :( 이것이 바로 꽃샘추위", Audience.FRIEND);
//
//    List<MultipartFile> files = Arrays.asList(
//            new MockMultipartFile("file1", "meeeeep.jpg", "image/jpeg", "some image".getBytes()),
//            new MockMultipartFile("file2", "anotherimage.jpg", "image/jpeg", "another image".getBytes())
//    );
//    boolean result= memoryService.postMemoryAndMemoryImages(files, memoryPostRequestDTO);
//    Assertions.assertThat(result).isTrue();
//
//    MemoryPostRequestDto memoryPostRequestDto1 = new MemoryPostRequestDto(2L, "삼일절!! 집콕여행", LocalDateTime.now(), "날씨가 매우 추워서 양말을 신어야했다 :( 이것이 바로 꽃샘추위", Audience.FRIEND);
//
//    List<MultipartFile> files2 = Arrays.asList(
//            new MockMultipartFile("file1", "meeeeep.jpg", "image/jpeg", "some image".getBytes()),
//            new MockMultipartFile("file2", "anotherimage.jpg", "image/jpeg", "another image".getBytes())
//    );
//    boolean result2= memoryService.postMemoryAndMemoryImages(files2, memoryPostRequestDto1);
//    Assertions.assertThat(result2).isTrue();
//    Assertions.assertThat(memoryRepository.findByPetId(1L).size()).isEqualTo(2);
//    Assertions.assertThat(memoryRepository.findByPetId(2L).size()).isEqualTo(2);
//
//
//    }
//    @Test
//    public void MoreThan10Files() throws Exception {
//        //given
//        MemoryRequestDTO memoryRequestDTO = new MemoryRequestDTO(1L, "삼일절!! 집콕여행", LocalDateTime.now(), "날씨가 매우 추워서 양말을 신어야했다 :( 이것이 바로 꽃샘추위", Audience.FRIEND);
//
//        List<MultipartFile> files = Arrays.asList(
//                new MockMultipartFile("file1", "meeeeep.jpg", "image/jpeg", "some image".getBytes()),
//                new MockMultipartFile("file2", "anotherimage.jpg", "image/jpeg", "another image".getBytes()),
//                new MockMultipartFile("file3", "anotherimage.jpg", "image/jpeg", "another image".getBytes()),
//                new MockMultipartFile("file4", "anotherimage.jpg", "image/jpeg", "another image".getBytes()),
//                new MockMultipartFile("file5", "anotherimage.jpg", "image/jpeg", "another image".getBytes()),
//                new MockMultipartFile("file6", "anotherimage.jpg", "image/jpeg", "another image".getBytes()),
//                new MockMultipartFile("file7", "anotherimage.jpg", "image/jpeg", "another image".getBytes()),
//                new MockMultipartFile("file8", "anotherimage.jpg", "image/jpeg", "another image".getBytes()),
//                new MockMultipartFile("file9", "anotherimage.jpg", "image/jpeg", "another image".getBytes()),
//                new MockMultipartFile("file10", "anotherimage.jpg", "image/jpeg", "another image".getBytes()),
//                new MockMultipartFile("file11", "anotherimage.jpg", "image/jpeg", "another image".getBytes())
//        );
//         MemoryResponseDTO result= memoryController.postAMemory(files, memoryRequestDTO);
//        Assertions.assertThat(result.getDecCode()).isEqualTo('0');
//    }
//    @Test
//    public void title60words() throws Exception {
//        //given
//        MemoryRequestDTO memoryRequestDTO = new MemoryRequestDTO(1L, "1123456789012345678901234567890123456789012345678901234567890", LocalDateTime.now(), "날씨가 매우 추워서 양말을 신어야했다 :( 이것이 바로 꽃샘추위", Audience.FRIEND);
//        List<MultipartFile> files = Arrays.asList(
//                new MockMultipartFile("file1", "meeeeep.jpg", "image/jpeg", "some image".getBytes()),
//                new MockMultipartFile("file2", "anotherimage.jpg", "image/jpeg", "another image".getBytes())
//        );
//        ConstraintViolationException exception= org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class,() ->{
//            memoryController.postAMemory(files, memoryRequestDTO);
//        });
//        System.out.println("exception = " + exception.getMessage());
//
//    }
//    @Test
//    public void ValidFileContentType() throws Exception {
//        //given
//        MemoryRequestDTO memoryRequestDTO = new MemoryRequestDTO(1L, "5678901234567890123456789012345678901234567890", LocalDateTime.now(), "날씨가 매우 추워서 양말을 신어야했다 :( 이것이 바로 꽃샘추위", Audience.FRIEND);
//        List<MultipartFile> files = Arrays.asList(
//                new MockMultipartFile("file1", "meeeeep.jpg", "meep", "some image".getBytes()),
//                new MockMultipartFile("file2", "anotherimage.jpg", "image/jpeg", "another image".getBytes())
//        );
//            MemoryResponseDTO result= memoryController.postAMemory(files, memoryRequestDTO);
//        Assertions.assertThat(result.getDecCode()).isEqualTo('0');
//        Assertions.assertThat(memoryRepository.findAll().size()).isEqualTo(0);
//        Assertions.assertThat(memoryImageRepository.findAll().size()).isEqualTo(0); }
//    @Test
//    public void fileSize10mb() throws Exception {
//        //given
//        byte[] largeFileContent = new byte[20 * 1024 * 1024];
//        MemoryRequestDTO memoryRequestDTO = new MemoryRequestDTO(1L, "5678901234567890123456789012345678901234567890", LocalDateTime.now(), "날씨가 매우 추워서 양말을 신어야했다 :( 이것이 바로 꽃샘추위", Audience.FRIEND);
//        List<MultipartFile> files = List.of(
//                new MockMultipartFile("file2", "anotherimage.jpg", "image/jpeg", largeFileContent)
//        );
//        MemoryResponseDTO result= memoryController.postAMemory(files, memoryRequestDTO);
//        Assertions.assertThat(result.getDecCode()).isEqualTo('0');
//   //given
//        byte[] fileContentSize = new byte[10 * 1024 * 1024];
//        MemoryRequestDTO memoryRequestDTO2 = new MemoryRequestDTO(1L, "5678901234567890123456789012345678901234567890", LocalDateTime.now(), "날씨가 매우 추워서 양말을 신어야했다 :( 이것이 바로 꽃샘추위", Audience.FRIEND);
//        List<MultipartFile> files2 = List.of(
//                new MockMultipartFile("file2", "anotherimage.jpg", "image/jpeg", fileContentSize)
//        );
//        MemoryResponseDTO result2= memoryController.postAMemory(files2, memoryRequestDTO2);
//        Assertions.assertThat(result2.getDecCode()).isEqualTo('1');
//
//    }
    @Test
    public void createMemoryImagesServiceFunction() throws Exception {
        //given
        MemoryPostRequestDto memoryPostRequestDTO = new MemoryPostRequestDto(1L, "1123456789012345678901234567890123456789012345678901234567890", LocalDateTime.now(), "날씨가 매우 추워서 양말을 신어야했다 :( 이것이 바로 꽃샘추위", Audience.FRIEND);
        Memory memory = memoryService.createAMemory(memoryPostRequestDTO);
        List<MultipartFile> files = Arrays.asList(
                new MockMultipartFile("file1", "meeeeep.jpg", "image/jpeg", "some image".getBytes()),
                new MockMultipartFile("file2", "anotherimage.jpg", "image/jpeg", "another image".getBytes())
        );
        //when
        boolean result= memoryService.createMemoryImages(memory, files);
        //then
        Assertions.assertThat(result).isTrue();
    }

}

