package com.memopet.memopet.domain.pet.dto;

import com.memopet.memopet.domain.pet.entity.Audience;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoryPostRequestDto {
    private Long petId;
    @Size(max = 60, message = "Memory title must be at most 60 characters long")
    private String memoryTitle;
    private String memoryDate;
    @Size(max = 2000, message = "Memory description must be at most 2000 characters long")
    private String memoryDesc;
    private Audience audience;

}
