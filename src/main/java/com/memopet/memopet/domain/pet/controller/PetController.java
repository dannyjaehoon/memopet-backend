package com.memopet.memopet.domain.pet.controller;


import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.service.PetService;
import com.memopet.memopet.global.common.dto.RestResult;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.config.annotation.Authed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.memopet.memopet.global.common.utils.Utils.toJson;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PetController {

    private final PetService petService;
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PostMapping(value="/pet/new",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestResult savePet(@RequestPart(value="back_img_url") MultipartFile backImgUrl,
                              @RequestPart(value="pet_profile_url") MultipartFile petProfileUrl,
                              @RequestPart(value = "petRequestDto") @Valid SavedPetRequestDto petRequestDto,
                              @Authed Member member) throws IOException {

        log.info("/pet/new member: {}, dto: {}", toJson(member), toJson(petRequestDto));
        log.info("save pet start");
        SavedPetResponseDto savedPetResponseDto = petService.savePet(backImgUrl, petProfileUrl, petRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("savePetResponse", savedPetResponseDto);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/pets")
    public RestResult findPets(PetsRequestDto petsRequestDto) {
        PetsResponseDto petResponseDto = petService.findPetsByPetId(petsRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("findPetsResponse", petResponseDto);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/profile-detail")
    public RestResult findPetDetailInfo(PetDetailInfoRequestDto petDetailInfoRequestDto) {
        PetDetailInfoResponseDto petDetailInfoResponseDto  = petService.findPetDetailInfo(petDetailInfoRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("findPetDetailedInfoResponse", petDetailInfoResponseDto);

        return new RestResult(dataMap);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PatchMapping("/profile")
    public RestResult findPets(@RequestPart(value="backImgUrl") MultipartFile backImgUrl, @RequestPart(value="petProfileUrl") MultipartFile petProfileUrl, @RequestPart(value = "petUpdateInfoRequestDto") PetUpdateInfoRequestDto petUpdateInfoRequestDto) throws Exception {
        PetUpdateInfoResponseDto petUpdateInfoResponseDto  = petService.updatePetInfo(backImgUrl, petProfileUrl, petUpdateInfoRequestDto);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("updatePetInfoResponse", petUpdateInfoResponseDto);

        return new RestResult(dataMap);
    }

    /**
     * 내 프로필 리스트
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @GetMapping("/pet/profiles/{petId}")
    public RestResult petsList(@PathVariable Long petId, Authentication authentication) {
        boolean validatePetResult = petService.validatePetRequest(authentication.getName(), petId);
        if (!validatePetResult) {
            throw new BadRequestRuntimeException("Pet not available or not active");
        }

        PetProfileResponseDto petProfileResponseDto = petService.profileList(petId);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("findPetListResponse", petProfileResponseDto);

        return new RestResult(dataMap);
    }

    /**
     * 펫 프로필 전환
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @PatchMapping("/pet")
    public RestResult switchProfile(@RequestBody PetSwitchRequestDto petSwitchResponseDTO) {
        PetProfileResponseDto petProfileResponseDto = petService.switchProfile(petSwitchResponseDTO);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("switchPetProfileResponse", petProfileResponseDto);

        return new RestResult(dataMap);
    }

    /**
     * 펫 프로필 삭제
     */
    @PreAuthorize("hasAuthority('SCOPE_USER_AUTHORITY')")
    @DeleteMapping("/pet")
    public RestResult deletePetProfile(@RequestBody PetDeleteRequestDto petDeleteRequestDTO) {
        PetProfileResponseDto petProfileResponseDto = petService.deletePetProfile(petDeleteRequestDTO);

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("deletePetProfileResponse", petProfileResponseDto);

        return new RestResult(dataMap);
    }


}
