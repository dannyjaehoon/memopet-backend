package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.*;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.common.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final BlockedRepository blockedRepository;
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
     * @param memoryRequestDto
     * @return
     */
    public MemoryResponseDto findMemoryByMemoryId(MemoryRequestDto memoryRequestDto) {

        // 사용자의 차단한 펫 id 가져오기
        BlockedAndBlockerListResponseDto blockedAndBlockerListResponseDto = blockedService.blockedPetList(memoryRequestDto.getPetId());
        List<Blocked> petList = blockedAndBlockerListResponseDto.getPetList();

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
                .memoryImageUrl1(!q.isEmpty() ? q.poll().getImageUrl() : null)
                .memoryImageUrlId2(!q.isEmpty() ? q.peek().getId() : null)
                .memoryImageUrl2(!q.isEmpty() ? q.poll().getImageUrl() : null)
                .memoryImageUrlId3(!q.isEmpty() ? q.peek().getId() : null)
                .memoryImageUrl3(!q.isEmpty() ? q.poll().getImageUrl() : null)
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

        if(!pet.isPresent()) return LikedMemoryResponseDto.builder().hasNext(false).currentPage(0).dataCounts(0).memoryResponseDto(memoriesContent).build();

        // 내가 좋아요한 정보를 가져온다.
        List<Likes> likesList = likesRepository.findLikesByPetId(pet.get().getId());

        if(likesList == null) return LikedMemoryResponseDto.builder().hasNext(false).currentPage(0).dataCounts(0).memoryResponseDto(memoriesContent).build();

        Queue<MemoryImage> q = new LinkedList<>();

        // 사용자가 차단하거나 사용자를 차단한 펫 id 가져오기
        HashMap<Long, Integer> blockList = blockedService.findBlockList(likedMemoryRequestDto.getPetId());

        for(Likes like : likesList) {
            if(blockList.getOrDefault(like.getLikedOwnPetId(),0) == 0) memoryIds.add(like.getMemory().getId());
        }

        PageRequest pageRequest = PageRequest.of(likedMemoryRequestDto.getCurrentPage()-1, likedMemoryRequestDto.getDataCounts());
        Slice<Memory> memorySlice = memoryRepository.findByMemoryIdsWithSlice(memoryIds, pageRequest);

        List<Memory> filteredMemories = memorySlice.getContent();
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
                    .memoryImageUrl1(!q.isEmpty() ? q.poll().getImageUrl() : null)
                    .memoryImageUrlId2(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl2(!q.isEmpty() ? q.poll().getImageUrl() : null)
                    .memoryImageUrlId3(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl3(!q.isEmpty() ? q.poll().getImageUrl() : null)
                    .build());
        }

        LikedMemoryResponseDto likedMemoryResponseDto = LikedMemoryResponseDto.builder().hasNext(memorySlice.hasNext()).currentPage(memorySlice.getNumber()+1).dataCounts(memorySlice.getContent().size()).memoryResponseDto(memoriesContent).build();
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
     * @param recentMainMemoriesRequestDto
     * @return
     */
    public RecentMainMemoriesResponseDto findMainMemoriesByPetId(RecentMainMemoriesRequestDto recentMainMemoriesRequestDto) {
        Optional<Pet> pet = petRepository.findById(Long.valueOf(recentMainMemoriesRequestDto.getPetId()));

        List<MemoryResponseDto> memoriesContent = new ArrayList<>();

        if(!pet.isPresent()) {
            return RecentMainMemoriesResponseDto.builder().totalPage(0).currentPage(0).dataCounts(0).memoryResponseDto(memoriesContent).build();
        }

        // 사용자가 차단하거나 사용자를 차단한 펫 id 가져오기
        HashMap<Long, Integer> blockMap = blockedService.findBlockList(recentMainMemoriesRequestDto.getPetId());
        List<Long> blockPetList = new ArrayList<>(blockMap.keySet());

        PageRequest pageRequest = PageRequest.of(recentMainMemoriesRequestDto.getCurrentPage()-1, recentMainMemoriesRequestDto.getDataCounts());
        Page<Memory> memoryPage = null;

        if(blockPetList.size() == 0) {
            memoryPage = memoryRepository.findByRecentMemoryIdsWithPaginationWithoutBlockedPetList(LocalDateTime.now().minusDays(7), pet.get().getId(), pageRequest);
        } else {
            memoryPage = memoryRepository.findByRecentMemoryIdsWithPagination(blockPetList,LocalDateTime.now().minusDays(7), pet.get().getId(), pageRequest);
        }

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
                    .memoryImageUrl1(!q.isEmpty() ? q.poll().getImageUrl() : null)
                    .memoryImageUrlId2(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl2(!q.isEmpty() ? q.poll().getImageUrl() : null)
                    .memoryImageUrlId3(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl3(!q.isEmpty() ? q.poll().getImageUrl() : null)
                    .build());
        }

        return RecentMainMemoriesResponseDto.builder().totalPage(memoryPage.getTotalPages()).currentPage(memoryPage.getNumber()+1).dataCounts(memoryPage.getContent().size()).memoryResponseDto(memoriesContent).build();
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
        if(!pet.isPresent()) return MonthMemoriesResponseDto.builder().build();

        Pet petInfo = pet.get();

        // 프로필 소유자가 사용자를 차단한 경우 노출 x
        if(blockedRepository.existsByPetIds(monthMemoriesRequestDto.getPetId(), monthMemoriesRequestDto.getMyPetId())) {
            return MonthMemoriesResponseDto.builder().build();
        }

        // yearMonth로 null 이면 최신 추억을 찾아 해당 달의 정보를 가져옴
        String yearMonth = monthMemoriesRequestDto.getYearMonth();
        Page<Memory> page;
        LocalDateTime firstDayOfMonth;
        LocalDateTime lastDayOfMonth;
        if(yearMonth == null) {
            Optional<Memory> theRecentMomory = memoryRepository.findTheRecentMomoryByPetId(petInfo.getId());
            if(theRecentMomory.get() == null) return MonthMemoriesResponseDto.builder().build();

            Memory memory = theRecentMomory.get();
            LocalDateTime recentPostedDate = memory.getCreatedDate();

            firstDayOfMonth = beginningOfMonth(recentPostedDate);
            lastDayOfMonth = endOfMonth(recentPostedDate);

        } else {
            // yearmonth로 받으면 해당 달 20개 를 가져옴
            int year = Integer.parseInt(yearMonth.substring(0,4));
            int month = Integer.parseInt(yearMonth.substring(4,6));
            LocalDateTime localDateTime = LocalDateTime.of(year, month, 01, 00, 00, 00);

            firstDayOfMonth = beginningOfMonth(localDateTime);
            lastDayOfMonth = endOfMonth(localDateTime);
        }

        PageRequest pageRequest = PageRequest.of(monthMemoriesRequestDto.getCurrentPage()-1, monthMemoriesRequestDto.getDataCounts());

        // 프로필 소유주의 친구일경우 보여주기위해서 pet 프로필 소유주 id로 조회
        page = memoryRepository.findMonthMomoriesByPetId(petInfo.getId(),firstDayOfMonth,lastDayOfMonth, pageRequest);

        List<MemoryResponseDto> memoryResponseDtos = new ArrayList<>();
        List<Memory> filteredMemories = page.getContent();
        List<MemoryImage> memoryImages = null;
        Queue<MemoryImage> q = new LinkedList<>();

        for (Memory memory : filteredMemories) {
            memoryImages = memoryImageRepository.findByMemoryId(memory.getId());
            memoryImages.forEach(memoryImage -> {
                q.offer(memoryImage);
            });

            memoryResponseDtos.add(MemoryResponseDto.builder()
                    .memoryImageUrlId1(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl1(!q.isEmpty() ? q.poll().getImageUrl() : null)
                    .memoryImageUrlId2(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl2(!q.isEmpty() ? q.poll().getImageUrl() : null)
                    .memoryImageUrlId3(!q.isEmpty() ? q.peek().getId() : null)
                    .memoryImageUrl3(!q.isEmpty() ? q.poll().getImageUrl() : null)
                    .memoryDate(memory.getMemoryDate())
                    .memoryTitle(memory.getTitle())
                    .memoryId(memory.getId())
                    .memoryDescription(memory.getMemoryDescription())
                    .build());
        }

        monthMemoriesResponseDto = MonthMemoriesResponseDto.builder().currentPage(page.getNumber()+1).totalPages(page.getTotalPages()).dataCounts(page.getContent().size()).memoryResponseDto(memoryResponseDtos).build();
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

            s3Uploader.deleteS3(memoryImage.getImageUrl());
        }

        memoryDeleteResponseDto = MemoryDeleteResponseDto.builder().decCode('1').build();
        return memoryDeleteResponseDto;
    }

    @Transactional(readOnly = false)
    public MemoryUpdateResponseDto updateMemoryInfo(MemoryUpdateRequestDto memoryUpdateRequestDto, List<MultipartFile> files) {

        Optional<Memory> memoryOptional = memoryRepository.findById(memoryUpdateRequestDto.getMemoryId());
        if(!memoryOptional.isPresent()) return MemoryUpdateResponseDto.builder().decCode('0').errorMsg("해당 추억 ID로 조회된 데이터가 없습니다.").build();
        Memory memory = memoryOptional.get();

        memoryRepository.updateMemoryInfo(memoryUpdateRequestDto);

        if(Objects.nonNull(memoryUpdateRequestDto.getMemoryImageUrlId1())) {
            Optional<MemoryImage> memoryImage1 = memoryImageRepository.findById(memoryUpdateRequestDto.getMemoryImageUrlId1());
            if(memoryImage1.isPresent()) memoryImageRepository.delete(memoryImage1.get());
        }

        if(Objects.nonNull(memoryUpdateRequestDto.getMemoryImageUrlId2())) {
            Optional<MemoryImage> memoryImage2 = memoryImageRepository.findById(memoryUpdateRequestDto.getMemoryImageUrlId2());
            if(memoryImage2.isPresent())  memoryImageRepository.delete(memoryImage2.get());
        }

        if(Objects.nonNull(memoryUpdateRequestDto.getMemoryImageUrlId3())) {
            Optional<MemoryImage> memoryImage3 = memoryImageRepository.findById(memoryUpdateRequestDto.getMemoryImageUrlId3());
            if(memoryImage3.isPresent()) memoryImageRepository.delete(memoryImage3.get());
        }


        List<MemoryImage> memoryImages = new ArrayList<>();

        for (MultipartFile file : files) {
            String storedMemoryImgUrl = s3Uploader.uploadFileToS3(file, "static/memory-image");
            MemoryImage memoryImage = getMemoryImage(memory, file, storedMemoryImgUrl);

            memoryImages.add(memoryImage);
        }
        if(memoryImages.size() > 0) {
            memoryImageRepository.saveAll(memoryImages);
        }
        return MemoryUpdateResponseDto.builder().decCode('1').errorMsg("수정 완료됬습니다.").build();
    }

    /**
     * 추억 생성
     */
    public MemoryPostResponseDto postMemoryAndMemoryImages(List<MultipartFile> files, MemoryPostRequestDto memoryPostRequestDTO) {
        Memory memory = createAMemory(memoryPostRequestDTO);
        if (memory == null) {
            throw new BadRequestRuntimeException("No Memory Info");
        }

        if (!createMemoryImages(memory, files)) {
            memoryRepository.deleteById(memory.getId());
        }
        return MemoryPostResponseDto.builder().decCode('1').build();
    }


    /**
     * (추억 생성)-추억 글
     */
    public Memory createAMemory(MemoryPostRequestDto memoryPostRequestDTO) {
        Pet pet = petRepository.findById(memoryPostRequestDTO.getPetId())
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id: " + memoryPostRequestDTO.getPetId()));

        Audience audience = memoryPostRequestDTO.getAudience().equals("1") ? Audience.ALL : memoryPostRequestDTO.getAudience().equals("2") ? Audience.FRIEND : Audience.ME;
        Memory memory = Memory.builder()
                .pet(pet)
                .title(memoryPostRequestDTO.getMemoryTitle())
                .memoryDate(LocalDate.parse(memoryPostRequestDTO.getMemoryDate(), DateTimeFormatter.ISO_DATE))
                .memoryDescription(memoryPostRequestDTO.getMemoryDesc())
                .audience(audience)
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
                String storedMemoryImgUrl = s3Uploader.uploadFileToS3(file, "static/memory-image");
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
                s3Uploader.deleteS3(image.getImageUrl());
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
                    .imageUrl(storedMemoryImgUrl)
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