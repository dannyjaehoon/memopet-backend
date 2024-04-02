package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.*;
import com.memopet.memopet.global.common.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemoryService {
    private final MemoryRepository memoryRepository;
    private final MemoryImageRepository memoryImageRepository;
    private final BlockedService blockedService;
    private final LikesRepository likesRepository;
    private final FollowRepository followRepository;
    private final PetRepository petRepository;
    private final S3Uploader s3Uploader;

    /**
     *
     * memory_id = 3 가 조회할 추억 id
     * pet_id = 2 가 사용자 pet_id
     * 로직 :
     * 1. memory_id = 3로 추억 조회 후 추억의 접근권한(모두, 친구, 비공개) 확인
     * 친구 : pet_id = 2가 memory_id=3의 친구인지 확인
     * 비공개 : 자기자신 외에는 전부 비공개 (추억의 소유주에게만 노출)
     *
     * 2. 추억의 주인(memory_id = 3)이 조회자(pet_id = 2)를 차단된 상태라면 비노출
     *
     * @param likedMemoryRequestDto
     * @return
     */
    public MemoryResponseDto findMemoryByMemoryId(MemoryRequestDto memoryRequestDto) {

        // 사용자의 차단한 펫 id 가져오기
        BlockListResponseDto blockListResponseDto = blockedService.blockedPetList(memoryRequestDto.getPetId());
        List<Blocked> petList = blockListResponseDto.getPetList();

        Optional<Memory> memory1 = memoryRepository.findById(memoryRequestDto.getMemoryId());

        if(!memory1.isPresent()) return MemoryResponseDto.builder().build();
        Memory memory = memory1.get();

        // 차단된 프로필 목록이 있을때
        if(petList != null) {
            //차단된 계정중에서 해당 추억을 소유를 했다면 노출하면안됨
            for(Blocked blocked : petList) {
                if(blocked.getBlockedPet().getId() == memory.getPet().getId()) return MemoryResponseDto.builder().build();
            }
        }

        // 추억 공개 제한이 친구일때
        if(memory.getAudience().equals(Audience.FRIEND)) {
            System.out.println("친구");
            List<Follow> followList = followRepository.findByPetId(memory.getPet());

            if(memory.getPet().getId() == memoryRequestDto.getPetId()) {
                return createMemory(memory);
            } else if(followList != null){
                for(Follow follow : followList) {
                    // 등록된 추억의 pet_id 의 친구랑 사용자 프로필 계정이랑 친구인지 확인
                    // 등록된 추억의 pet_id는 1, 1이 친구 추가를 한 1,2,3
                    // 사용자는 pet_id가 2임. 따라서 조회가능
                    if(follow.getPetId() == memoryRequestDto.getPetId()) {
                        return createMemory(memory);
                    }
                }
            }

            // 자기자신 또는 친구가 없다면 빈값으로 보내줌
            return MemoryResponseDto.builder().build();
        }

        // 추억 공개 제한이 비공개
        if(memory.getAudience().equals(Audience.ME) && memory.getPet().getId() != memoryRequestDto.getPetId()) {
            System.out.println("비공개");
            return MemoryResponseDto.builder().build();
        }

        return createMemory(memory);
    }

    public MemoryResponseDto createMemory(Memory memory) {
        // 추억 이미지 가져오기
        List<MemoryImage> memoryImages = memoryImageRepository.findByMemoryId(memory.getId());
        Queue<MemoryImage> q = new LinkedList<>();

        memoryImages.forEach(memoryImage -> {
            q.offer(memoryImage);
        });

        MemoryResponseDto memoryResponseDto = MemoryResponseDto.builder()
                .memoryImageUrlId1(!q.isEmpty() ? q.peek().getId() : null)
                .memoryImageUrl1(!q.isEmpty() ? q.poll().getUrl() : null)
                .memoryImageUrlId2(!q.isEmpty() ? q.peek().getId() : null)
                .memoryImageUrl2(!q.isEmpty() ? q.poll().getUrl() : null)
                .memoryImageUrlId3(!q.isEmpty() ? q.peek().getId() : null)
                .memoryImageUrl3(!q.isEmpty() ? q.poll().getUrl() : null)
                .memoryId(memory.getId())
                .memoryTitle(memory.getTitle())
                .memoryDescription(memory.getMemoryDescription())
                .memoryDate(memory.getMemoryDate())
                .build();

        return memoryResponseDto;
    }

    /**
     *
     * pet_id = 2 가 조회자
     * 로직 :
     * 1. pet_id = 2가 좋아요를 누른 추억 조회 후 각 추억의 접근권한(모두, 친구, 비공개) 로 확인
     * 친구 : pet_id = 2가 1의 친구인지 확인
     * 비공개 : 자기자신 외에는 전부 비공개 (pet_id = 1 인 경우외만 노출)
     *
     * 2. 각 추억의 주인(pet_id)가 조회자(pet_id = 2)에게 차단된 상태라면 비노출
     *
     * @param likedMemoryRequestDto
     * @return
     */
    public LikedMemoryResponseDto findLikedMemoriesByPetId(LikedMemoryRequestDto likedMemoryRequestDto) {

        List<Long> memoryIds = new ArrayList<>();
        List<MemoryResponseDto> memoriesContent = new ArrayList<>();

        Optional<Pet> pet = petRepository.findById(likedMemoryRequestDto.getPetId());

        if(!pet.isPresent()) return LikedMemoryResponseDto.builder().totalPages(0).currentPage(0).dataCounts(0).memoryResponseDto(memoriesContent).build();

        List<Likes> likesLs = likesRepository.findLikesByPetId(pet.get().getId());

        if(likesLs == null) return LikedMemoryResponseDto.builder().totalPages(0).currentPage(0).dataCounts(0).memoryResponseDto(memoriesContent).build();

        Queue<MemoryImage> q = new LinkedList<>();
        List<Long> filteredMemoryIds = new ArrayList<>();



        if(likesLs != null) {
            for (Likes like : likesLs) {
                memoryIds.add(like.getMemoryId().getId());
            }

            List<Memory> memories = memoryRepository.findByMemoryIds(memoryIds);

            // 사용자의 차단한 펫 id 가져오기
            BlockListResponseDto blockListResponseDto = blockedService.blockedPetList(likedMemoryRequestDto.getPetId());
            List<Blocked> petList = blockListResponseDto.getPetList();

            loop :
            for(int i = 0; i<memories.size(); i++) {
                // 차단된 프로필 목록이 있을때
                if(petList != null) {
                    //차단된 계정중에서 해당 추억을 소유를 했다면 노출하면안됨
                    for(Blocked blocked : petList) {
                        if(blocked.getBlockedPet().getId() == memories.get(i).getPet().getId()) continue loop;
                    }
                }
                // 추억 공개 제한이 친구일때 - 여기에서는 친구의 좋아요만 나오므로 체크할 필요가 없음

                // 추억 공개 제한이 비공개
                if(memories.get(i).getAudience().equals(Audience.ME) && memories.get(i).getPet().getId() != likedMemoryRequestDto.getPetId()) {
                    // 자기소유가 아니면 다음으로 넘기기.
                    continue;
                }
                filteredMemoryIds.add(memories.get(i).getId());
            }
        }
        PageRequest pageRequest = PageRequest.of(likedMemoryRequestDto.getCurrentPage()-1, likedMemoryRequestDto.getDataCounts());
        // 필터된 데이터로 다시 메모리 조회
        Page<Memory> memoryPage = memoryRepository.findByMemoryIdsWithPagination(filteredMemoryIds, pageRequest);

        List<Memory> filteredMemories = memoryPage.getContent();
        List<MemoryImage> memoryImages = null;
        for(Memory memory : filteredMemories) {
            memoryImages = memoryImageRepository.findByMemoryId(memory.getId());
            memoryImages.forEach(memoryImage -> {
                q.offer(memoryImage);
            });

            memoriesContent.add(MemoryResponseDto.builder()
                    .memoryId(memory.getId())
                    .memoryTitle(memory.getTitle())
                    .memoryDescription(memory.getMemoryDescription())
                    .memoryDate(memory.getMemoryDate())
                    .memoryImageUrlId1(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl1(!q.isEmpty() ? q.poll().getUrl() : null)
                    .memoryImageUrlId2(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl2(!q.isEmpty() ? q.poll().getUrl() : null)
                    .memoryImageUrlId3(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl3(!q.isEmpty() ? q.poll().getUrl() : null)
                    .build());
        }

        LikedMemoryResponseDto likedMemoryResponseDto = LikedMemoryResponseDto.builder().totalPages(memoryPage.getTotalPages()).currentPage(memoryPage.getNumber()+1).dataCounts(memoryPage.getContent().size()).memoryResponseDto(memoriesContent).build();
        return likedMemoryResponseDto;
    }

    /**
     *
     * pet_id = 2 가 조회자
     * 로직 :
     * 1. pet_id = 2가 좋아요를 누른 최신 추억 조회(일주인 전꺼만) 후 각 추억의 접근권한(모두, 친구, 비공개) 로 확인
     * 친구 : pet_id = 2가 1의 친구인지 확인
     * 비공개 : 자기자신 외에는 전부 비공개 (pet_id = 1 인 경우외만 노출)
     *
     * 2. 각 추억의 주인(pet_id)이 조회자(pet_id = 2)를 차단된 상태라면 비노출
     *
     * @param likedMemoryRequestDto
     * @return
     */
    public LikedMemoryResponseDto findMainMemoriesByPetId(LikedMemoryRequestDto likedMemoryRequestDto) {
        Optional<Pet> pet = petRepository.findById(Long.valueOf(likedMemoryRequestDto.getPetId()));

        List<MemoryResponseDto> memoriesContent = new ArrayList<>();
        List<Long> memoryIds = new ArrayList<>();

        if(!pet.isPresent()) {
            return LikedMemoryResponseDto.builder().totalPages(0).currentPage(0).dataCounts(0).memoryResponseDto(memoriesContent).build();
        }

        List<Likes> likesLs = likesRepository.findLikesByPetId(pet.get().getId());
        List<Long> filteredMemoryIds = new ArrayList<>();

        if(likesLs == null) return LikedMemoryResponseDto.builder().totalPages(0).currentPage(0).dataCounts(0).memoryResponseDto(memoriesContent).build();

        if(likesLs != null) {
            for (Likes like : likesLs) {
                memoryIds.add(like.getMemoryId().getId());
            }

            List<Memory> memories = memoryRepository.findByRecentMemoryIds(memoryIds, LocalDateTime.now().minusDays(7));

            // 사용자의 차단한 펫 id 가져오기
            BlockListResponseDto blockListResponseDto = blockedService.blockedPetList(likedMemoryRequestDto.getPetId());
            List<Blocked> petList = blockListResponseDto.getPetList();

            loop :
            for(int i = 0; i<memories.size(); i++) {
                // 차단된 프로필 목록이 있을때
                if(petList != null) {
                    //차단된 계정중에서 해당 추억을 소유를 했다면 노출하면안됨
                    for(Blocked blocked : petList) {
                        if(blocked.getBlockedPet().getId() == memories.get(i).getPet().getId()) continue loop;
                    }
                }
                // 추억 공개 제한이 친구일때 - 여기에서는 친구의 좋아요만 나오므로 체크할 필요가 없음

                // 추억 공개 제한이 비공개
                if(memories.get(i).getAudience().equals(Audience.ME) && memories.get(i).getPet().getId() != likedMemoryRequestDto.getPetId()) {
                    // 자기소유가 아니면 다음으로 넘기기.
                    continue;
                }
                filteredMemoryIds.add(memories.get(i).getId());
            }
        }
        PageRequest pageRequest = PageRequest.of(likedMemoryRequestDto.getCurrentPage()-1, likedMemoryRequestDto.getDataCounts());
        // 필터된 데이터로 다시 메모리 조회
        Page<Memory> memoryPage = memoryRepository.findByRecentMemoryIdsWithPagination(filteredMemoryIds,LocalDateTime.now().minusDays(7), pageRequest);

        List<Memory> filteredMemories = memoryPage.getContent();
        List<MemoryImage> memoryImages = null;
        Queue<MemoryImage> q = new LinkedList<>();

        for (Memory m : filteredMemories) {
            memoryImages = memoryImageRepository.findByMemoryId(m.getId());
            memoryImages.forEach(memoryImage -> {
                q.offer(memoryImage);
            });

            memoriesContent.add(MemoryResponseDto.builder()
                    .memoryId(m.getId())
                    .memoryTitle(m.getTitle())
                    .memoryDescription(m.getMemoryDescription())
                    .memoryDate(m.getMemoryDate())
                    .memoryImageUrlId1(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl1(!q.isEmpty() ? q.poll().getUrl() : null)
                    .memoryImageUrlId2(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl2(!q.isEmpty() ? q.poll().getUrl() : null)
                    .memoryImageUrlId3(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl3(!q.isEmpty() ? q.poll().getUrl() : null)
                    .build());
        }

        LikedMemoryResponseDto likedMemoryResponseDto = LikedMemoryResponseDto.builder().totalPages(memoryPage.getTotalPages()).currentPage(memoryPage.getNumber()+1).dataCounts(memoryPage.getContent().size()).memoryResponseDto(memoriesContent).build();
        return likedMemoryResponseDto;

    }

    /**
     *
     * pet_id = 1 이 추억들 주인이고 my_pet_id = 2 가 조회자
     * 로직 : pet_id = 1 로 추억 조회 후 각 추억의 접근권한(모두, 친구, 비공개) 로 확인
     * 친구 : my_pet_id = 2가 1의 친구인지 확인
     * 비공개 : 자기자신 외에는 전부 비공개 (pet_id = 1 인 경우외만 노출)
     * @param monthMemoriesRequestDto
     * @return
     */
    public MonthMemoriesResponseDto findMonthMemoriesByPetId(MonthMemoriesRequestDto monthMemoriesRequestDto) {
        MonthMemoriesResponseDto monthMemoriesResponseDto;
        // pet id로 펫 정보를 가져옴
        Optional<Pet> pet = petRepository.findById(monthMemoriesRequestDto.getPetId());
        if(!pet.isPresent()) return monthMemoriesResponseDto = MonthMemoriesResponseDto.builder().build();

        Pet petInfo = pet.get();

        // yearMonth로 null 이면 최신 추억을 찾아 해당 달의 정보를 가져옴
        String yearMonth = monthMemoriesRequestDto.getYearMonth();
        List<Memory> memories;
        LocalDateTime firstDayOfMonth;
        LocalDateTime lastDayOfMonth;
        if(yearMonth == null) {
            Optional<Memory> theRecentMomory = memoryRepository.findTheRecentMomoryByPetId(petInfo.getId());

            if(theRecentMomory.get() == null) return monthMemoriesResponseDto = MonthMemoriesResponseDto.builder().build();

            Memory memory = theRecentMomory.get();
            LocalDateTime recentPostedDate = memory.getCreatedDate();

            firstDayOfMonth = beginningOfMonth(recentPostedDate);
            lastDayOfMonth = endOfMonth(recentPostedDate);
            memories = memoryRepository.findMonthMomoriesByPetId(petInfo.getId(), firstDayOfMonth, lastDayOfMonth);

        } else {
            // yearmonth로 받으면 해당 달 20개 를 가져옴
            int year = Integer.parseInt(yearMonth.substring(0,4));
            int month = Integer.parseInt(yearMonth.substring(4,6));
            LocalDateTime localDateTime = LocalDateTime.of(year, month, 01, 00, 00, 00);

            firstDayOfMonth = beginningOfMonth(localDateTime);
            lastDayOfMonth = endOfMonth(localDateTime);
            memories = memoryRepository.findMonthMomoriesByPetId(petInfo.getId(),firstDayOfMonth,lastDayOfMonth);
        }




        if(memories == null) return monthMemoriesResponseDto = MonthMemoriesResponseDto.builder().build();

        List<MemoryResponseDto> memoryResponseDtos = new ArrayList<>();
        List<Long> filteredMemoryIds = new ArrayList<>();
        boolean isFlag = false;

        loop :
        for(int i = 0; i<memories.size(); i++) {
            isFlag = false;

            // 추억 공개 제한이 친구일때 - 여기에서는 친구의 좋아요만 나오므로 체크할 필요가 없음
            if(memories.get(i).getAudience().equals(Audience.FRIEND)) {
                System.out.println("친구");
                List<Follow> followList = followRepository.findByPetId(memories.get(i).getPet());

                if(memories.get(i).getPet().getId() == monthMemoriesRequestDto.getMyPetId()) {
                    isFlag = true;
                } else if(followList != null){
                    for(Follow follow : followList) {
                        if(follow.getPetId() == monthMemoriesRequestDto.getMyPetId()) {
                            isFlag = true;
                            break;
                        }
                    }
                }
                // 자기자신 또는 친구가 없다면 다음으로 넘기기
                if(!isFlag) continue;
            }

            // 추억 공개 제한이 비공개
            if(memories.get(i).getAudience().equals(Audience.ME) && memories.get(i).getPet().getId() != monthMemoriesRequestDto.getMyPetId()) {
                // 자기소유가 아니면 다음으로 넘기기.
                continue;
            }
            filteredMemoryIds.add(memories.get(i).getId());
        }

        PageRequest pageRequest = PageRequest.of(monthMemoriesRequestDto.getCurrentPage()-1, monthMemoriesRequestDto.getDataCounts());
        // 필터된 데이터로 다시 메모리 조회
        Page<Memory> memoryPage = memoryRepository.findByMemoryIdsWithPagination(filteredMemoryIds, pageRequest);

        List<Memory> filteredMemories = memoryPage.getContent();
        List<MemoryImage> memoryImages = null;
        Queue<MemoryImage> q = new LinkedList<>();


        for (Memory memory : filteredMemories) {
            memoryImages = memoryImageRepository.findByMemoryId(memory.getId());
            memoryImages.forEach(memoryImage -> {
                q.offer(memoryImage);
            });

            memoryResponseDtos.add(MemoryResponseDto.builder()
                    .memoryImageUrlId1(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl1(!q.isEmpty() ? q.poll().getUrl() : null)
                    .memoryImageUrlId2(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl2(!q.isEmpty() ? q.poll().getUrl() : null)
                    .memoryImageUrlId3(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl3(!q.isEmpty() ? q.poll().getUrl() : null)
                    .memoryDate(memory.getMemoryDate())
                    .memoryTitle(memory.getTitle())
                    .memoryId(memory.getId())
                    .memoryDescription(memory.getMemoryDescription())
                    .build());
        }


        monthMemoriesResponseDto = MonthMemoriesResponseDto.builder().currentPage(memoryPage.getNumber()+1).totalPages(memoryPage.getTotalPages()).dataCounts(memoryPage.getContent().size()).memoryResponseDto(memoryResponseDtos).build();
        return monthMemoriesResponseDto;
    }

    @Transactional(readOnly = false)
    public MemoryDeleteResponseDto deleteMemory(MemoryDeleteRequestDto memoryDeleteRequestDto) throws Exception {
        Long memoryId = memoryDeleteRequestDto.getMemoryId();
        Optional<Memory> memoryOptional = memoryRepository.findById(memoryId);
        MemoryDeleteResponseDto memoryDeleteResponseDto;
        if(!memoryOptional.isPresent()) return memoryDeleteResponseDto = MemoryDeleteResponseDto.builder().decCode('0').errorMsg("해당 memory id로 조회되는 데이터가 없습니다.").build();
        Memory memory = memoryOptional.get();
        memory.updateDeleteDate(LocalDateTime.now());

        List<MemoryImage> memoryImages = memoryImageRepository.findByMemoryId(memory.getId());

        for(MemoryImage memoryImage : memoryImages) {
            memoryImage.updateDeletedDate(LocalDateTime.now());

            //s3Uploader.deleteS3(memoryImage.getUrl());
        }

        memoryDeleteResponseDto = MemoryDeleteResponseDto.builder().decCode('1').build();
        return memoryDeleteResponseDto;
    }

//    @Transactional(readOnly = false)
//    public MemoryUpdateResponseDto updateMemoryInfo(MemoryUpdateRequestDto memoryUpdateRequestDto, List<MultipartFile> files) {
//
//        Optional<Memory> memoryOptional = memoryRepository.findById(memoryUpdateRequestDto.getMemoryId());
//        if(!memoryOptional.isPresent()) return MemoryUpdateResponseDto.builder().decCode('0').errorMsg("해당 추억 ID로 조회된 데이터가 없습니다.").build();
//        Memory memory = memoryOptional.get();
//
//        //추억제목
//        if(memoryUpdateRequestDto.getMemoryTitle() != null || memoryUpdateRequestDto.getMemoryTitle().equals("")) {
//            memory.updateTitle(memoryUpdateRequestDto.getMemoryTitle());
//        }
//
//        //추억날짜
//        if(memoryUpdateRequestDto.getMemoryDate() != null || memoryUpdateRequestDto.getMemoryDate().equals("")) {
//            memory.updateMemoryDate(memoryUpdateRequestDto.getMemoryDate());
//        }
//
//        //추억 설명
//        if(memoryUpdateRequestDto.getMemoryDescription() != null || memoryUpdateRequestDto.getMemoryDescription().equals("")) {
//            memory.updateDesc(memoryUpdateRequestDto.getMemoryDescription());
//        }
//
//        //공개범위
//        if(memoryUpdateRequestDto.getOpenRestrictionLevel() != null || memoryUpdateRequestDto.getOpenRestrictionLevel().equals("")) {
//            Audience audience = null;
//            if(memoryUpdateRequestDto.getOpenRestrictionLevel() == 1)  audience = Audience.ALL;
//            if(memoryUpdateRequestDto.getOpenRestrictionLevel() == 2)  audience = Audience.FRIEND;
//            if(memoryUpdateRequestDto.getOpenRestrictionLevel() == 3)  audience = Audience.ME;
//            memory.updateAudience(audience);
//        }
//
//        if(memoryUpdateRequestDto.getMemoryImageUrlId1() != null || memoryUpdateRequestDto.getMemoryImageUrlId1().equals("")) {
//            Optional<MemoryImage> memoryImage1 = memoryImageRepository.findById(memoryUpdateRequestDto.getMemoryImageUrlId1());
//        }
//
//
//        if(memoryUpdateRequestDto.getMemoryImageUrlId2() != null || memoryUpdateRequestDto.getMemoryImageUrlId2().equals("")) {
//            Optional<MemoryImage> memoryImage2 = memoryImageRepository.findById(memoryUpdateRequestDto.getMemoryImageUrlId2());
//        }
//
//        if(memoryUpdateRequestDto.getMemoryImageUrlId3() != null || memoryUpdateRequestDto.getMemoryImageUrlId3().equals("")) {
//            Optional<MemoryImage> memoryImage3 = memoryImageRepository.findById(memoryUpdateRequestDto.getMemoryImageUrlId3());
//        }
//
//
//        for (MultipartFile file : files) {
//            String storedMemoryImgUrl = getS3Url(file);
//            MemoryImage memoryImage = getMemoryImage(memory, file, storedMemoryImgUrl);
//
//            MemoryImage.builder()
//                    .url(storedMemoryImgUrl)
//                    .imageFormat(file.getContentType())
//                    .memory(memory)
//                    .imageSize(String.valueOf(file.getSize()))
//                    .imageLogicalName(UUID.randomUUID().toString())
//                    .imagePhysicalName(file.getOriginalFilename())
//                    .build();
//        }
//
//        return MemoryUpdateResponseDto.builder().decCode('1').errorMsg("수정 완료됬습니다.").build();
//    }

    /**
     * 추억 생성
     */
    public boolean postMemoryAndMemoryImages(List<MultipartFile> files, MemoryPostRequestDto memoryPostRequestDTO) {
        Memory memory = createAMemory(memoryPostRequestDTO);
        if (memory == null) {
            return false;
        }

        if (!createMemoryImages(memory, files)) {
            memoryRepository.deleteById(memory.getId());
            return false;
        }
        return true;
    }


    /**
     * (추억 생성)-추억 글
     */
    public Memory createAMemory(MemoryPostRequestDto memoryPostRequestDTO) {
        Pet pet = petRepository.findById(memoryPostRequestDTO.getPetId())
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id: " + memoryPostRequestDTO.getPetId()));
        Memory memory = Memory.builder()
                .pet(pet)
                .title(memoryPostRequestDTO.getMemoryTitle())
                .memoryDate(memoryPostRequestDTO.getMemoryDate())
                .memoryDescription(memoryPostRequestDTO.getMemoryDesc())
                .audience(memoryPostRequestDTO.getAudience())
                .build();
        return memoryRepository.save(memory);
    }

    /**
     * (추억 생성)-사진
     */
    public boolean createMemoryImages(Memory memory, List<MultipartFile> files) {
        List<MemoryImage> images = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String storedMemoryImgUrl = getS3Url(file);
                MemoryImage memoryImage = getMemoryImage(memory, file, storedMemoryImgUrl);

                images.add(memoryImage);
            }

            if (images.size() == files.size()) {
                memoryImageRepository.saveAll(images);
                return true;
            } else {
                throw new RuntimeException("Not all memory images were created successfully");
            }
        } catch (Exception e) {
            cleanupAndThrowException(images, e);
            return false; // Return false to indicate that the operation failed
        }
    }

    /**
     * (추억 생성)-중간에 오류 나면 생성 중이던 사진을 지운다 (db와 s3)
     */
    private void cleanupAndThrowException(List<MemoryImage> images, Exception originalException) {
        // If there was an issue saving images, delete uploaded files from S3
        for (MemoryImage image : images) {
            try {
                s3Uploader.deleteS3(image.getUrl());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        // Remove any associated records from the database
        memoryImageRepository.deleteAll(images);

        // Throw original exception with additional context
        throw new RuntimeException("Error occurred while creating memory images", originalException);
    }

    /**
     * (추억 생성)-사진을 DB에 저장 한다.
     */
    private static MemoryImage getMemoryImage(Memory memory, MultipartFile file, String storedMemoryImgUrl) {
        try {
            return MemoryImage.builder()
                    .url(storedMemoryImgUrl)
                    .imageFormat(file.getContentType())
                    .memory(memory)
                    .imageSize(String.valueOf(file.getSize()))
                    .imageLogicalName(UUID.randomUUID().toString())
                    .imagePhysicalName(file.getOriginalFilename())
                    .build();
        } catch (Exception exp) {
            throw new RuntimeException("Error creating MemoryImage Builder", exp);
        }
    }

    private String getS3Url(MultipartFile file) {
        try {
            System.out.println("MemoryImage s3Uploader upload start");
            return s3Uploader.uploadFileToS3(file, "static/Memory-image");
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }
    }

    /**
     * beginningOfMonth
     * <pre>
     * 파라미터로 받은 날짜에 해당되는 달의 첫번째 날짜를 리턴한다.
     * </pre>
     * @param dateTime
     * @return
     */
    public static LocalDateTime beginningOfMonth(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), 1, 0, 0, 0);
    }

    /**
     * endOfMonth
     * <pre>
     * 파라미터로 받은 날짜에 해당되는 달의 마지막 날짜(23시 59분 99초)를 리턴한다.
     * </pre>
     * @param dateTime
     * @return
     */
    public static LocalDateTime endOfMonth(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), daysInMonth(dateTime), 23, 59, 59, 999_999_999);
    }
    /**
     * daysInMonth
     * <pre>
     * 파라미터로 받은 날짜에 해당되는 달의 총 일수를 리턴한다.
     * </pre>
     * @param dateTime
     * @return
     */
    public static int daysInMonth(LocalDateTime dateTime) {
        return dateTime.getMonth().length(dateTime.toLocalDate().isLeapYear()); //isLeapYear은 윤년여부
    }

}