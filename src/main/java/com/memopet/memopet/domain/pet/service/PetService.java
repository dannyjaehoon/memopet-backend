package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.*;
import com.memopet.memopet.global.common.service.S3Uploader;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {
    // 의존성 주입
    private final PetRepository petRepository;
    private final SpeciesRepository speciesRepository;
    private final MemberRepository memberRepository;
    private final LikesRepository likesRepository;
    private final FollowRepository followRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlockedService blockedService;
    private final S3Uploader s3Uploader;
    private final MemoryRepository memoryRepository;
    private final MemoryImageRepository memoryImageRepository;


    @Transactional(readOnly = false)
    public SavedPetResponseDto savePet(MultipartFile petImg, MultipartFile backgroundImg, SavedPetRequestDto petRequestDto) throws IOException {
        String storedPetImgName = null;

        System.out.println("savePet service start");

        if (!petImg.isEmpty()) {
            System.out.println("savePet s3Uploader upload start");
            storedPetImgName = s3Uploader.uploadFileToS3(petImg, "static/pet-image");
        }

        String storedBackgroundImgName = null;
        if (!backgroundImg.isEmpty()) {
            storedBackgroundImgName = s3Uploader.uploadFileToS3(backgroundImg, "static/pet-image");
        }

        System.out.println(storedPetImgName);
        System.out.println(storedBackgroundImgName);
        Species species = Species.builder().largeCategory("포유류").midCategory(petRequestDto.getPetSpecM()).smallCategory(petRequestDto.getPetSpecS()).build();
        Species savedSpecies = speciesRepository.save(species);
        Member member = memberRepository.findByEmail(petRequestDto.getEmail());


        Pet pet = Pet.builder()
                .petName(petRequestDto.getPetName())
                .gender(petRequestDto.getPetGender())
                .petDesc(petRequestDto.getPetDesc())
                .member(member)
                .species(savedSpecies)
                .petBirth(LocalDate.parse(petRequestDto.getBirthDate(), DateTimeFormatter.ISO_DATE))
                .petDeathDate(LocalDate.parse(petRequestDto.getPetDeathDate(), DateTimeFormatter.ISO_DATE))
                .petFavs(petRequestDto.getPetFavs())
                .petFavs2(petRequestDto.getPetFavs2())
                .petFavs3(petRequestDto.getPetFavs3())
                .petProfileUrl(storedPetImgName)
                .backImgUrl(storedBackgroundImgName)
                .petStatus(PetStatus.ACTIVE)
                .build();

        System.out.println("pet build complete");
        petRepository.save(pet);
        System.out.println("pet saved complete");
        SavedPetResponseDto petResponse = SavedPetResponseDto.builder().decCode('1').build();
        return petResponse;
    }

    public PetsResponseDto findPetsByPetId(PetsRequestDto petsRequestDto) {
        // 펫 id로 펫 정보 조회
        Optional<Pet> pet = petRepository.findById(petsRequestDto.getPetId());
        if(!pet.isPresent()) return PetsResponseDto.builder().build();

        // 사용자가 차단한 펫 id 가져오기
        BlockListResponseDto blockListResponseDto = blockedService.blockedPetList(petsRequestDto.getPetId());
        List<Blocked> petList = blockListResponseDto.getPetList();

        // 펫 정보로 자기를 좋아해 주는 프로필 조회
        List<PetResponseDto> petsContent = new ArrayList<>();

        List<Long> petIds = likesRepository.findLikesListByPetId(pet.get().getId());
        HashSet<Long> setPetIds = new HashSet<>();

        // 자기가 좋아요한 petid 추출
        for (long id : petIds) {
            setPetIds.add(id);
        }

        // 자기 자신도 포함
        setPetIds.add(petsRequestDto.getPetId());

        // 사용자가 차단한 사람들도 포함
        for(Blocked blocked : petList ) {
            setPetIds.add(blocked.getBlockedPet().getId());
        }

        // 자기자신 + 자기가 좋아요를 누른 프로필 제외하고 최대 20개 조회
        List<Pet> pets = petRepository.findByIdNotIn(setPetIds);
        HashSet<Long> unLikedPetIds = new HashSet<>();
        pets.forEach(p-> unLikedPetIds.add(p.getId()));

        // 좋아요를 누른 갯수
        List<LikesPerPetDto> likesByPetIds = likesRepository.findLikesByPetIds(unLikedPetIds);
        HashMap<Long, Integer> hashMap = new HashMap<>();

        for(LikesPerPetDto l : likesByPetIds) {
            hashMap.put(l.getPetId(), l.getLikes());
        }

        for (Pet p : pets) {
            petsContent.add(PetResponseDto.builder()
                            .petId(p.getId())
                            .petName(p.getPetName())
                            .petDesc(p.getPetDesc())
                            .petGender(p.getGender())
                            .backImgUrl(p.getBackImgUrl())
                            .petProfileUrl(p.getPetProfileUrl())
                            .likes(hashMap.getOrDefault(p.getId(),0))
                    .build());
        }

        return PetsResponseDto.builder().petResponseDto(petsContent).build();
    }

    public PetDetailInfoResponseDto findPetDetailInfo(PetDetailInfoRequestDto petDetailInfoRequestDto) {
        Long petId = petDetailInfoRequestDto.getPetId(); // 내가 조회한 프로필 pet_id
        Long myPetId = petDetailInfoRequestDto.getMyPetId(); // 내 프로필 pet_id

        Optional<Pet> petInfo = petRepository.findById(petId);
        Pet pet = petInfo.get();

        // follow count
        List<Follow> follows = followRepository.findByPetId(pet);
        // follow check
        Optional<Follow> follow = followRepository.findById(petId);

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Comment> page = commentRepository.findCommentsByPetId(pet, CommentGroup.LAST_WORD, 1,pageRequest);

        List<Comment> comments = page.getContent();
        HashSet<Long> petIdSet = new HashSet<>();

        comments.forEach(c -> petIdSet.add(c.getCommenterId()));

        List<Pet> pets = petRepository.findByIds(petIdSet);
        HashMap<Long, Pet> petIdMap = new HashMap<>();

        pets.forEach(p -> {
            petIdMap.put(p.getId(),p);
        });

        List<PetCommentResponseDto> petComments = new ArrayList<>();
        comments.forEach(comment -> {
                    petComments.add(PetCommentResponseDto.builder()
                                    .commentId(comment.getId())
                                    .commentCreatedDate(comment.getCreatedDate())
                                    .comment(comment.getComment())
                                    .petProfileUrl(petIdMap.get(comment.getCommenterId()).getPetProfileUrl())
                                    .petId(petIdMap.get(comment.getCommenterId()).getId())
                                    .petName(petIdMap.get(comment.getCommenterId()).getPetName())
                            .build());
                });

        PetDetailInfoResponseDto petDetailInfoResponseDto = PetDetailInfoResponseDto.builder()
                .petId(pet.getId())
                .petName(pet.getPetName())
                .petGender(pet.getGender())
                .petBirthDate(pet.getPetBirth())
                .petDeathDate(pet.getPetDeathDate())
                .petDesc(pet.getPetDesc())
                .petFavs(pet.getPetFavs())
                .petFavs2(pet.getPetFavs2())
                .petFavs3(pet.getPetFavs3())
                .follow(follows.size())
                .followYN(follow.isPresent() ? "Y" : "N")
                .petProfileUrl(pet.getPetProfileUrl())
                .backImgUrl(pet.getBackImgUrl())
                .petProfileFrame(pet.getPetProfileFrame())
                .petCommentResponseDto(petComments)
                .build();

        return petDetailInfoResponseDto;
    }

    @Transactional(readOnly = false)
    public PetUpdateInfoResponseDto updatePetInfo(MultipartFile petImg, MultipartFile backgroundImg, PetUpdateInfoRequestDto petUpdateInfoRequestDto) throws Exception {
        Optional<Pet> petOptional = petRepository.findById(petUpdateInfoRequestDto.getPetId());
        if(!petOptional.isPresent()) return PetUpdateInfoResponseDto.builder().decCode('0').errMsg("해당 펫 ID로 조회된 데이터가 없습니다.").build();
        Pet pet = petOptional.get();

        // 펫 정보 업데이트
        if(petUpdateInfoRequestDto.getPetDesc() != null) {
            log.info("펫 desc 수정");
            pet.updateDesc(petUpdateInfoRequestDto.getPetDesc());
        }
        if(petUpdateInfoRequestDto.getPetName() != null) {
            log.info("펫 이름 수정");
            pet.updateName(petUpdateInfoRequestDto.getPetName());
        }
        if(petUpdateInfoRequestDto.getPetBirthDate() != null) {
            log.info("펫 생일 수정");
            pet.updateBirthDate(petUpdateInfoRequestDto.getPetBirthDate());
        }
        if(petUpdateInfoRequestDto.getPetDeathDate() != null) {
            log.info("펫 사망일 수정");
            pet.updateDeathDate(petUpdateInfoRequestDto.getPetDeathDate());
        }
        if(petUpdateInfoRequestDto.getPetProfileFrame() != null) {
            log.info("펫 프레임 수정");
            pet.updatePetProfileFrame(petUpdateInfoRequestDto.getPetProfileFrame());
        }
        if(petUpdateInfoRequestDto.getPetFavs() != null) {
            log.info("펫 f1 수정");
            pet.updateFav(petUpdateInfoRequestDto.getPetFavs(),1);
        }
        if(petUpdateInfoRequestDto.getPetFavs2() != null) {
            log.info("펫 f2 수정");
            pet.updateFav(petUpdateInfoRequestDto.getPetFavs2(),2);
        }
        if(petUpdateInfoRequestDto.getPetFavs3() != null) {
            log.info("펫 f3 수정");
            pet.updateFav(petUpdateInfoRequestDto.getPetFavs3(),3);
        }
        if(!petImg.isEmpty()) {
            log.info("펫 Profile 수정");
            s3Uploader.deleteS3(pet.getPetProfileUrl());
            String storedPetImgName = s3Uploader.uploadFileToS3(petImg, "static/pet-image");

            pet.updateProfileUrl(storedPetImgName);
        }

        if(!backgroundImg.isEmpty()) {
            log.info("펫 background 수정");
            s3Uploader.deleteS3(pet.getBackImgUrl());
            String storedBackgroundImgName = s3Uploader.uploadFileToS3(backgroundImg, "static/pet-image");
            pet.updateBackgroundUrl(storedBackgroundImgName);
        }

        return PetUpdateInfoResponseDto.builder().decCode('1').errMsg("수정 완료됬습니다.").build();
    }

    /**
     * 내 프로필 리스트
     */
    @Transactional(readOnly = true)
    public PetListWrapper profileList(Pageable pageable, Long petId) {

        if (petId == null) {
            // Set error code and description for missing or invalid email
           return PetListWrapper.builder().decCode('0').build();

        } else {
            return PetListWrapper.builder().petList(petRepository.findPetsById(pageable,petId)).build();}

    }

    /**
     * 펫 프로필 전환
     */
    @Transactional(readOnly = false)
    public boolean switchProfile(PetSwitchRequestDto petSwitchRequestDTO) {

        return petRepository.switchPetProfile(petSwitchRequestDTO.getPetId());
    }
    /**
     * 펫 프로필 삭제 -Pet(deletedDate)
     */

    @Transactional(readOnly = false)
    public PetProfileResponseDto deletePetProfile(PetDeleteRequestDto petDeleteRequestDTO) {
        try {
            Optional<Member> member = memberRepository.findOptionalMemberByEmail(petDeleteRequestDTO.getEmail());
            if (member.isEmpty()) {
                return new PetProfileResponseDto('0',"존재하지 않거나 삭제된 이메일입니다"); // Member not found
            }

            if (!passwordEncoder.matches(petDeleteRequestDTO.getPassword(), member.get().getPassword())) {
                return new PetProfileResponseDto('0',"비밀번호를 다시 입력하세요.");
            }
            Pet deletePet = petRepository.getReferenceById(petDeleteRequestDTO.getPetId());
            if (!member.get().getPets().contains(deletePet)) {
                return new PetProfileResponseDto('0',"존재하지않거나 나의 프로필이 아닙니다.");
            }
            // Attempt to delete the pet profile
            boolean deletionSuccessful = petRepository.deleteAPet(member.get().getId(), petDeleteRequestDTO.getPetId());

            if (!deletionSuccessful) {
                return new PetProfileResponseDto('0',"프로필 삭제를 실패했습니다.");
            }
            deletePetsAssociate(deletePet);
            return new PetProfileResponseDto('1',"프로필이 삭제되었습니다.");
        } catch (Exception e) {
            return new PetProfileResponseDto('0',"프로필 삭제를 실패하였습니다.");
        }

    }


    private void deletePetsAssociate(Pet pet) {
        try {
            s3Uploader.deleteS3(pet.getPetProfileUrl());
            s3Uploader.deleteS3(pet.getBackImgUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //memory,comment,images-deleted date
        //images in s3 - delete
        List<Memory> deletePetMemory = memoryRepository.findByPetIds(Collections.singletonList(pet.getId()));
        for (Memory memory:deletePetMemory){
            deleteMemoryAndAssociatedEntities(memory);

        }



    }

    private void deleteMemoryAndAssociatedEntities(Memory memory) {
        memory.updateDeleteDate(LocalDateTime.now());
        List<MemoryImage> images = memoryImageRepository.findByMemoryId(memory.getId());
        for (MemoryImage img : images) {
            img.updateDeletedDate(LocalDateTime.now());
            try {
                s3Uploader.deleteS3(img.getUrl());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        List<Comment> comments = commentRepository.findByMemory(memory);
        for (Comment comment : comments) {
            comment.updateDeleteDate(LocalDateTime.now());
        }
    }
    public boolean validatePetRequest(String email, Long petId) {
        Member member= memberRepository.findByEmail(email);
        if (member == null) {
            return false;
        }
        List<Pet> pets = member.getPets();
        for (Pet pet : pets) {
            if (pet.getId().equals(petId)&& pet.getPetStatus().equals(PetStatus.ACTIVE)) {
                return true;
            }
        }
        return false;

    }
}
