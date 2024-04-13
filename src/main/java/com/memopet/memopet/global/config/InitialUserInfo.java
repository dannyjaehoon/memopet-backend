package com.memopet.memopet.global.config;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.*;
import com.memopet.memopet.global.common.entity.RecentSearch;
import com.memopet.memopet.global.common.repository.RecentSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class InitialUserInfo implements CommandLineRunner {
    // member test data insert
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemoryRepository memoryRepository;
    private final LikesRepository likesRepository;
    private final PetRepository petRepository;
    private final SpeciesRepository speciesRepository;
    private final CommentRepository commentRepository;
    private final MemoryImageRepository memoryImageRepository;
    private final FollowRepository followRepository;
    private final RecentSearchRepository recentSearchRepository;
    @Override
    public void run(String... args) throws Exception {
        Member member1 = Member.builder()
                .username("이재훈")
                .password(passwordEncoder.encode("123"))
                .email("user@gmail.com")
                .loginFailCount(0)
                .phoneNum("01054232322")
                .memberStatus(MemberStatus.NORMAL)
                .roles("ROLE_USER")
                .activated(true)
                .build();

        Member member2 = Member.builder()
                .username("노재현")
                .password(passwordEncoder.encode("123"))
                .email("jae@gmail.com")
                .loginFailCount(0)
                .phoneNum("01094232322")
                .memberStatus(MemberStatus.NORMAL)
                .roles("ROLE_USER")
                .activated(true)
                .build();

        Member member3 = Member.builder()
                .username("김형구")
                .password(passwordEncoder.encode("123"))
                .email("admin@gmail.com")
                .loginFailCount(0)
                .phoneNum("01084232322")
                .memberStatus(MemberStatus.NORMAL)
                .roles("ROLE_ADMIN")
                .activated(true)
                .build();

        memberRepository.saveAll(List.of(member1,member2,member3));

        Species species = Species.builder().largeCategory("포유류").midCategory("개").smallCategory("마티즈").build();
        Species species1 = Species.builder().largeCategory("포유류").midCategory("개").smallCategory("시바이누").build();
        Species species2 = Species.builder().largeCategory("포유류").midCategory("개").smallCategory("세인트버나드").build();
        Species species3 = Species.builder().largeCategory("포유류").midCategory("개").smallCategory("서식스스패니얼").build();
        Species species4 = Species.builder().largeCategory("포유류").midCategory("개").smallCategory("폭스테리어").build();
        Species species5 = Species.builder().largeCategory("포유류").midCategory("개").smallCategory("테리어").build();
        Species species6 = Species.builder().largeCategory("포유류").midCategory("개").smallCategory("시추").build();
        Species species7 = Species.builder().largeCategory("포유류").midCategory("개").smallCategory("푸들").build();

        speciesRepository.saveAll(List.of(species,species1,species2,species3,species4,species5,species6,species7));
        Pet pet1 = Pet.builder()
                .petName("두리")
                .gender(Gender.F)
                .petDesc("두리는 이쁘다")
                .member(member1)
                .species(species)
                .petBirth(LocalDate.now())
                .petFavs("웃기")
                .petFavs2("먹기")
                .petFavs3("달리기")
                .petProfileFrame(0)
                .petProfileUrl("https://images.unsplash.com/photo-1517849845537-4d257902454a?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8JUVBJUIwJTlDfGVufDB8fDB8fHww")
                .backImgUrl("https://images.unsplash.com/photo-1707343843982-f8275f3994c5?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDF8MHxlZGl0b3JpYWwtZmVlZHw2fHx8ZW58MHx8fHx8")
                .petStatus(PetStatus.ACTIVE)
                .build();

        Pet pet2 = Pet.builder()
                .petName("방울이")
                .gender(Gender.F)
                .petDesc("방울이는 이쁘다")
                .member(member1)
                .species(species)
                .petBirth(LocalDate.now())
                .petFavs("웃기")
                .petFavs2("먹기")
                .petFavs3("달리기")
                .petProfileFrame(0)
                .petProfileUrl("https://images.unsplash.com/photo-1530281700549-e82e7bf110d6?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8JUVBJUIwJTlDfGVufDB8fDB8fHww")
                .backImgUrl("https://images.unsplash.com/photo-1706670542491-61eeb26510de?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzfHx8ZW58MHx8fHx8")
                .petStatus(PetStatus.DEACTIVE)
                .build();

        Pet pet3 = Pet.builder()
                .petName("조롱이")
                .gender(Gender.F)
                .petDesc("조롱이는 이쁘다")
                .member(member2)
                .species(species)
                .petBirth(LocalDate.now())
                .petFavs("떠들기")
                .petFavs2("뛰기")
                .petFavs3("달리기")
                .petProfileFrame(0)
                .petProfileUrl("https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTF8fCVFQSVCMCU5Q3xlbnwwfHwwfHx8MA%3D%3D")
                .backImgUrl("https://images.unsplash.com/photo-1706670542491-61eeb26510de?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzfHx8ZW58MHx8fHx8")
                .petStatus(PetStatus.ACTIVE)
                .build();

        Pet pet4 = Pet.builder()
                .petName("띵띵이")
                .gender(Gender.F)
                .petDesc("띵띵이는 이쁘다")
                .member(member2)
                .species(species)
                .petBirth(LocalDate.now())
                .petFavs("울기")
                .petFavs2("웃기")
                .petFavs3("때리기")
                .petProfileFrame(0)
                .petProfileUrl("https://images.unsplash.com/photo-1588943211346-0908a1fb0b01?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTJ8fCVFQSVCMCU5Q3xlbnwwfHwwfHx8MA%3D%3D")
                .backImgUrl("https://images.unsplash.com/photo-1706670542491-61eeb26510de?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwzfHx8ZW58MHx8fHx8")
                .petStatus(PetStatus.DEACTIVE)
                .build();

        petRepository.saveAll(List.of(pet1, pet2, pet3, pet4));

        Memory memory = Memory.builder().pet(pet1)
                .title("방울이와의 밤 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(1)
                .audience(Audience.ALL)
                .build();

        Memory memory1 = Memory.builder().pet(pet1)
                .title("방울이와의 낮 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(1)
                .audience(Audience.ME)
                .build();
        Memory memory2 = Memory.builder().pet(pet1)
                .title("방울이와의 오전 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(1)
                .audience(Audience.FRIEND)
                .build();
        Memory memory3 = Memory.builder().pet(pet1)
                .title("방울이와의 오후 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(1)
                .audience(Audience.ALL)
                .build();
        Memory memory4 = Memory.builder().pet(pet2)
                .title("두리와의 오전 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("두리와의 산책이 너무 즐거웠습니다.")
                .likeCount(1)
                .audience(Audience.ALL)
                .build();
        Memory memory5 = Memory.builder().pet(pet2)
                .title("두리와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("두리와의 산책이 너무 즐거웠습니다.")
                .likeCount(1)
                .audience(Audience.ALL)
                .build();
        Memory memory6 = Memory.builder().pet(pet2)
                .title("두리와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("두리와의 산책이 너무 즐거웠습니다.")
                .likeCount(1)
                .audience(Audience.ALL)
                .build();
        Memory memory7 = Memory.builder().pet(pet3)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(1)
                .audience(Audience.ALL)
                .build();
        Memory memory8 = Memory.builder().pet(pet3)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory9 = Memory.builder().pet(pet3)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory10 = Memory.builder().pet(pet3)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory11 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory12 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory13 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory14 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory15 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory16 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory17 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory18 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory19 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory20 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory21 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();
        Memory memory22 = Memory.builder().pet(pet4)
                .title("방울이와의 산책")
                .memoryDate(LocalDate.now())
                .memoryDescription("방울이와의 산책이 너무 즐거웠습니다.")
                .likeCount(0)
                .audience(Audience.ALL)
                .build();

        memoryRepository.saveAll(List.of(memory,memory1,memory2,memory3,memory4,memory5,memory6,memory7,memory8,memory9,memory10,memory11,memory12,memory13,memory14,memory15,memory16,memory17,memory18,memory19,memory20,memory21,memory22));



        MemoryImage memoryImage = MemoryImage.builder()
                .imageFormat("jpg")
                .url("https://images.rawpixel.com/image_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIzLTA4L3Jhd3BpeGVsX29mZmljZV8xNV9waG90b19vZl9hX2RvZ19ydW5uaW5nX3dpdGhfb3duZXJfYXRfcGFya19lcF9mM2I3MDQyZC0zNWJlLTRlMTQtOGZhNy1kY2Q2OWQ1YzQzZjlfMi5qcGc.jpg")
                .imagePhysicalName("추억 1 : 방울이와 찍은사진")
                .imageLogicalName(UUID.randomUUID().toString())
                .imageSize("20mb")
                .memory(memory)
                .createdDate(LocalDateTime.now())
                .build();

        MemoryImage memoryImage1 = MemoryImage.builder()
                .imageFormat("jpg")
                .url("https://thumbs.dreamstime.com/b/beagle-dog-isolated-white-background-purebred-103538194.jpg")
                .imageLogicalName(UUID.randomUUID().toString())
                .imagePhysicalName("추억 1 : 방울이와 찍은사진1")
                .imageSize("30mb")
                .memory(memory)
                .createdDate(LocalDateTime.now())
                .build();

        MemoryImage memoryImage2 = MemoryImage.builder()
                .imageFormat("jpg")
                .url("https://images.unsplash.com/photo-1551992536-ee9a450c44d2?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8JUVBJUIwJTlDJTIwJUVDJTgyJUFDJUVDJUE3JTg0fGVufDB8fDB8fHww")
                .imagePhysicalName("추억 1 : 방울이와 찍은사진")
                .imageLogicalName(UUID.randomUUID().toString())
                .imageSize("20mb")
                .memory(memory)
                .createdDate(LocalDateTime.now())
                .build();

        MemoryImage memoryImage3 = MemoryImage.builder()
                .imageFormat("jpg")
                .url("https://images.unsplash.com/photo-1521247560470-d2cbfe2f7b47?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8JUVBJUIwJTlDJTIwJUVDJTgyJUFDJUVDJUE3JTg0fGVufDB8fDB8fHww")
                .imageLogicalName(UUID.randomUUID().toString())
                .imagePhysicalName("추억 2 : 방울이와 찍은사진1")
                .imageSize("30mb")
                .memory(memory1)
                .createdDate(LocalDateTime.now())
                .build();
        MemoryImage memoryImage4 = MemoryImage.builder()
                .imageFormat("jpg")
                .url("https://images.unsplash.com/photo-1591214958544-ddcf55d9707b?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTF8fCVFQSVCMCU5QyUyMCVFQyU4MiVBQyVFQyVBNyU4NHxlbnwwfHwwfHx8MA%3D%3D")
                .imageLogicalName(UUID.randomUUID().toString())
                .imagePhysicalName("추억 2 : 방울이와 찍은사진1")
                .imageSize("30mb")
                .memory(memory1)
                .createdDate(LocalDateTime.now())
                .build();

        memoryImageRepository.saveAll(List.of(memoryImage,memoryImage1,memoryImage2,memoryImage3,memoryImage4));

        Likes likes = Likes.builder().memoryId(memory).likedOwnPetId(pet1.getId()).pet(pet2).build();
        Likes likes1 = Likes.builder().memoryId(memory1).likedOwnPetId(pet1.getId()).pet(pet2).build();
        Likes likes2 = Likes.builder().memoryId(memory2).likedOwnPetId(pet1.getId()).pet(pet2).build();

        Likes likes3 = Likes.builder().memoryId(memory4).likedOwnPetId(pet2.getId()).pet(pet3).build();
        Likes likes4 = Likes.builder().memoryId(memory5).likedOwnPetId(pet2.getId()).pet(pet3).build();
        Likes likes5 = Likes.builder().memoryId(memory6).likedOwnPetId(pet2.getId()).pet(pet4).build();
        Likes likes6 = Likes.builder().memoryId(memory7).likedOwnPetId(pet2.getId()).pet(pet4).build();


        likesRepository.saveAll(List.of(likes,likes1,likes2,likes3,likes4,likes5,likes6));

        // 댓글
        Comment comment = Comment.builder().memory(memory).pet(null).commentGroup(CommentGroup.MEMORY_COMMENT).commenterId(pet2.getId()).comment("방울이는 너무 사랑스러워요").depth(1).build();
        Comment comment1 = Comment.builder().memory(null).pet(pet1).commentGroup(CommentGroup.LAST_WORD).commenterId(pet2.getId()).comment("방울이는 좋은곳으로 갔을거에요").depth(1).build();
        Comment comment2 = Comment.builder().memory(null).pet(pet1).commentGroup(CommentGroup.LAST_WORD).commenterId(pet3.getId()).comment("방울이는 참 귀여웠는데 안타까워요").depth(1).build();
        Comment comment3 = Comment.builder().memory(null).pet(pet1).commentGroup(CommentGroup.LAST_WORD).commenterId(pet3.getId()).comment("방울이는 참 좋은 주인을 만났어요").depth(1).build();
        Comment comment4 = Comment.builder().memory(null).pet(pet1).commentGroup(CommentGroup.LAST_WORD).commenterId(pet4.getId()).comment("방울이의 명복을빕니다.").depth(1).build();
        Comment comment5 = Comment.builder().memory(null).pet(pet1).commentGroup(CommentGroup.LAST_WORD).commenterId(pet2.getId()).comment("방울이는 내 기쁨이었어요").depth(1).build();
        Comment comment6 = Comment.builder().memory(null).pet(pet1).commentGroup(CommentGroup.LAST_WORD).commenterId(pet4.getId()).comment("방울이가 보고싶어요").depth(1).build();
        Comment comment7 = Comment.builder().memory(null).pet(pet1).commentGroup(CommentGroup.LAST_WORD).commenterId(pet4.getId()).comment("방울이는 천재였어요").depth(1).build();
        Comment comment8 = Comment.builder().memory(null).pet(pet1).commentGroup(CommentGroup.LAST_WORD).commenterId(pet4.getId()).comment("방울이 ㅠㅠ").depth(1).build();
        Comment comment9 = Comment.builder().memory(null).pet(pet1).commentGroup(CommentGroup.LAST_WORD).commenterId(pet4.getId()).comment("방울이는 낭만파~").depth(1).build();

        commentRepository.saveAll(List.of(comment,comment1,comment2,comment3,comment4,comment5,comment6,comment7,comment8,comment9));

        Comment comment10 = Comment.builder().memory(memory).pet(null).parentCommentId(comment.getId()).commentGroup(CommentGroup.MEMORY_COMMENT).commenterId(pet4.getId()).comment("방울이는 너무 사랑스러워요").depth(2).build();
        Comment comment11 = Comment.builder().memory(null).pet(pet1).parentCommentId(comment1.getId()).commentGroup(CommentGroup.LAST_WORD).commenterId(pet2.getId()).comment("방울이는 너무 사랑스러워요").depth(2).build();
        Comment comment12 = Comment.builder().memory(null).pet(pet1).parentCommentId(comment1.getId()).commentGroup(CommentGroup.LAST_WORD).commenterId(pet3.getId()).comment("방울이는 너무 사랑스러워요").depth(2).build();
        Comment comment13 = Comment.builder().memory(memory).pet(null).parentCommentId(comment.getId()).commentGroup(CommentGroup.MEMORY_COMMENT).commenterId(pet2.getId()).comment("방울이는 내 첫사랑~").depth(2).build();
        Comment comment14 = Comment.builder().memory(memory).pet(null).parentCommentId(comment.getId()).commentGroup(CommentGroup.MEMORY_COMMENT).commenterId(pet3.getId()).comment("최고의 방울이").depth(2).build();
        commentRepository.saveAll(List.of(comment10,comment11,comment12,comment13,comment14));


        Follow follow = Follow.builder().petId(pet2.getId()).followingPet(pet1).build();
        Follow follow1 = Follow.builder().petId(pet3.getId()).followingPet(pet1).build();
        Follow follow2 = Follow.builder().petId(pet4.getId()).followingPet(pet1).build();
        Follow follow3 = Follow.builder().petId(pet2.getId()).followingPet(pet3).build();
        Follow follow4 = Follow.builder().petId(pet2.getId()).followingPet(pet4).build();
        Follow follow5 = Follow.builder().petId(pet3.getId()).followingPet(pet2).build();
        Follow follow6 = Follow.builder().petId(pet4.getId()).followingPet(pet2).build();
        Follow follow7 = Follow.builder().petId(pet4.getId()).followingPet(pet3).build();
        followRepository.saveAll(List.of(follow,follow1,follow2,follow3,follow4,follow5,follow6,follow7));

        List<String> searchTexts = new ArrayList<>();
        searchTexts.add("코코");
        searchTexts.add("피기");

        RecentSearch recentSearch = RecentSearch.builder().searchTexts(searchTexts).createdDate(LocalDateTime.now()).pet(pet1).build();

        recentSearchRepository.save(recentSearch);
    }
}
