package com.memopet.memopet.global.common.service;

import com.memopet.memopet.domain.pet.dto.SearchMemoryCommentResponseDto;
import com.memopet.memopet.domain.pet.dto.SearchPetCommentResponseDto;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.*;
import com.memopet.memopet.domain.pet.service.BlockedService;
import com.memopet.memopet.global.common.dto.*;
import com.memopet.memopet.global.common.entity.RecentSearch;
import com.memopet.memopet.global.common.repository.RecentSearchRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecentSearchService {
    private final RecentSearchRepository recentSearchRepository;
    private final FollowRepository followRepository;
    private final MemoryImageRepository memoryImageRepository;
    private final BlockedService blockedService;
    private final MemoryRepository memoryRepository;
    private final PetRepository petRepository;
    private static final int MAX_RECENT_SEARCHES = 5;

    public SearchResponseDTO search(SearchRequestDTO searchRequestDTO) {
        int desCode = searchRequestDTO.getDesCode();

        if (desCode == 0) {
            // 연관추억 조회
            SearchResponseDTO searchMemoryResponseDTO= searchMemory(searchRequestDTO);
            // 연관 프로필 조회
            SearchResponseDTO searchProfileResponseDTO= searchProfile(searchRequestDTO);

            return SearchResponseDTO.builder()
                    .searchMemoryCommentResponseDtos(searchMemoryResponseDTO.getSearchMemoryCommentResponseDtos())
                    .hasNext(searchMemoryResponseDTO.isHasNext())
                    .currentPage(searchMemoryResponseDTO.getCurrentPage())
                    .dataCounts(searchMemoryResponseDTO.getDataCounts())
                    .searchPetCommentResponseDtos(searchProfileResponseDTO.getSearchPetCommentResponseDtos())
                    .hasNext2(searchProfileResponseDTO.isHasNext2())
                    .currentPage2(searchProfileResponseDTO.getCurrentPage2())
                    .dataCounts2(searchProfileResponseDTO.getDataCounts2())
                    .build();

        } else if(desCode == 1) {
            // 연관추억 조회
            SearchResponseDTO searchMemoryResponseDTO= searchMemory(searchRequestDTO);
            return searchMemoryResponseDTO;
        } else if(desCode == 2) {
            // 연관 프로필 조회
            SearchResponseDTO searchProfileResponseDTO= searchProfile(searchRequestDTO);
            return searchProfileResponseDTO;
        }

        return SearchResponseDTO.builder().build();
    }

    private SearchResponseDTO searchProfile(SearchRequestDTO searchRequestDTO) {
        String searchText = searchRequestDTO.getSearchText();
        long petId = searchRequestDTO.getPetId();

        log.info("getSearchText : " + searchRequestDTO.getSearchText());
        log.info("getDataCounts2 : " + searchRequestDTO.getDataCounts2());
        log.info("getCurrentPage2 : " + searchRequestDTO.getCurrentPage2());
        log.info("getPetId : " + searchRequestDTO.getPetId());


        List<SearchPetCommentResponseDto> searchPetCommentResponseDtos = new ArrayList<>();
        Optional<Pet> pet = petRepository.findById(petId);

        // 팔로우 여부를 위해서 검색자의 팔로우 리스트 조회
        List<Follow> followList = followRepository.findByPetId(pet.get());
        HashMap<Long,Integer> map = new HashMap<>();
        for(Follow f : followList) {
           map.put(f.getPetId(), 1);
        }

        // 사용자가 차단하거나 사용자를 차단한 펫 id 가져오기
        HashMap<Long, Integer> blockMap = blockedService.findBlockList(petId);
        List<Long> blockedList = new ArrayList<>(blockMap.keySet());

        PageRequest pageRequest = PageRequest.of(searchRequestDTO.getCurrentPage2()-1, searchRequestDTO.getDataCounts2());
        blockedList.add(petId);

        Slice<Pet> slice = petRepository.findPetBySearchText(blockedList, searchText, pageRequest);

         //fixme 이런식으로 변경할 필요있습니다. if(slice.getContent().isEmpty())
        if(slice.getContent().size() == 0) return SearchResponseDTO.builder().build();
        List<Pet> pets = slice.getContent();

        for(Pet p : pets) {
            searchPetCommentResponseDtos.add(SearchPetCommentResponseDto.builder()
                    .petId(p.getId())
                    .petName(p.getPetName())
                    .petDesc(p.getPetDesc())
                    .petProfileUrl(p.getPetProfileUrl())
                    .petDeathDate(p.getPetDeathDate())
                    .followYn(map.getOrDefault(p.getId(),0) != 0 ? 1 : 0)
                    .build());
        }

        return SearchResponseDTO.builder().searchPetCommentResponseDtos(searchPetCommentResponseDtos).currentPage2(slice.getNumber()+1).dataCounts2(slice.getContent().size()).hasNext2(slice.hasNext()).build();
    }



    private SearchResponseDTO searchMemory(SearchRequestDTO searchRequestDTO) {
        String searchText = searchRequestDTO.getSearchText();
        long petId = searchRequestDTO.getPetId();

        // 프로필 차단 로직 + 메모리 자체 접근 로직 필요(모두, 친구, 비공개)
        // 사용자가 차단하거나 사용자를 차단한 펫 id 가져오기
        HashMap<Long, Integer> blockMap = blockedService.findBlockList(petId);
        List<Long> blockedList = new ArrayList<>(blockMap.keySet());

        PageRequest pageRequest = PageRequest.of(searchRequestDTO.getCurrentPage()-1, searchRequestDTO.getDataCounts());
        blockedList.add(petId);

        // 모두, 친구, 비공개
        // 검색에서는 비공개는 안나오는게 맞고
        // 친구일경우에 보여주고
        // 모두인것들은 전부가져오는게 필요
        Slice<Memory> slice = memoryRepository.findMemoryBySearchText(blockedList, searchText, petId,pageRequest);

        List<Memory> memories = slice.getContent();
        log.info("memories size : " + memories.size());
        List<SearchMemoryCommentResponseDto> searchMemoryCommentResponseDtos = new ArrayList<>();

        for(Memory m : memories) {
            Optional<MemoryImage> memoryImage = memoryImageRepository.findOneById(m.getId());
            searchMemoryCommentResponseDtos.add(SearchMemoryCommentResponseDto.builder()
                            .memoryId(m.getId())
                            .memoryImageUrl(memoryImage.isPresent() ? memoryImage.get().getUrl():null)
                            .memoryImageUrlId(memoryImage.isPresent() ? memoryImage.get().getId():null)
                            .memoryDescription(m.getMemoryDescription())
                            .memoryTitle(m.getTitle())
                            .build());
        }

        return SearchResponseDTO.builder().searchMemoryCommentResponseDtos(searchMemoryCommentResponseDtos).currentPage(slice.getNumber()+1).dataCounts(slice.getContent().size()).hasNext(slice.hasNext()).build();
    }


    public RecentSearchResponseDto recentSearch(RecentSearchRequestDto recentSearchRequestDto) {
        Optional<Pet> petOptional = petRepository.findById(recentSearchRequestDto.getPetId());

        if(!petOptional.isPresent()) return RecentSearchResponseDto.builder().build();
        RecentSearch recentSearch = recentSearchRepository.findByPet(petOptional.get());

        return RecentSearchResponseDto.builder().searchTexts(recentSearch.getSearchTexts()).dataCounts(recentSearch.getSearchTexts().size()).build();
    }

    public RecentSearchDeleteResponseDto deleteRecentSearchText(RecentSearchDeleteRequestDto recentSearchDeleteRequestDto) {
        Optional<Pet> petOptional = petRepository.findById(recentSearchDeleteRequestDto.getPetId());

        if(!petOptional.isPresent()) return RecentSearchDeleteResponseDto.builder().dscCode("0").build();
        RecentSearch petSearched = recentSearchRepository.findByPet(petOptional.get());

        List<String> searchTexts = petSearched.getSearchTexts();
        for(String searchText : searchTexts) {
            if(searchText.equals(recentSearchDeleteRequestDto.getSearchText())) {
                searchTexts.remove(recentSearchDeleteRequestDto.getSearchText());
                petSearched.updateSearchText(searchTexts);
                return RecentSearchDeleteResponseDto.builder().dscCode("1").searchTexts(searchTexts).dataCounts(searchTexts.size()).build();
            }
        }
        return RecentSearchDeleteResponseDto.builder().dscCode("0").searchTexts(searchTexts).dataCounts(searchTexts.size()).build();
    }

    public void addRecentSearch(String searchText, Long petId) {
        Pet pet1 = petRepository.getReferenceById(petId);
        if (!recentSearchRepository.existsByPetId(pet1)) {
            RecentSearch recentSearch = RecentSearch.builder()
                    .pet(pet1).build();
        }
        RecentSearch results = recentSearchRepository.findByPet(pet1);
        if (results.getSearchTexts().size() < MAX_RECENT_SEARCHES) {
            results.getSearchTexts().add(searchText);
        }
    }

    public RecentSearchDeleteAllResponseDto deleteAllRecentSearchText(RecentSearchDeleteAllRequestDto recentSearchDeleteAllRequestDto) {
        Optional<Pet> petOptional = petRepository.findById(recentSearchDeleteAllRequestDto.getPetId());

        if(!petOptional.isPresent()) return RecentSearchDeleteAllResponseDto.builder().code("0").build();
        RecentSearch petSearched = recentSearchRepository.findByPet(petOptional.get());

        List<String> searchTexts = new ArrayList<>();
        petSearched.updateSearchText(searchTexts);
        return RecentSearchDeleteAllResponseDto.builder().code("1").build();
    }
}
