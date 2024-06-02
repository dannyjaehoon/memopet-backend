package com.memopet.memopet.global.common.dto;

import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadDto {

    private String bucketName;
    private byte[] fileBytes;
    private String fileName;
    private String contentType;
    private String filePath;

}
