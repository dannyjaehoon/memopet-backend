package com.memopet.memopet.global.common.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.Md5Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.global.common.dto.AccessLogDto;
import com.memopet.memopet.global.common.dto.ImageUploadDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.memopet.memopet.global.common.utils.Utils.toJson;
import static com.memopet.memopet.global.config.RabbitMQImageUploadDirectConfig.IMAGE_UPLOAD_DIRECT_QUEUE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadRabbitConsumer {
    private final ObjectMapper objectMapper;


    @RabbitListener(queues = IMAGE_UPLOAD_DIRECT_QUEUE_NAME)
    @Transactional(readOnly = false)
    public void consumeSub(String jsonMessage) {

        try {
            //imageUploadDto = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(imageUploadDtoStr, ImageUploadDto.class);
            ImageUploadDto imageUploadDto = objectMapper.readValue(jsonMessage, ImageUploadDto.class);


            MockMultipartFile multipartFile = new MockMultipartFile(
                    imageUploadDto.getFileName(),           // 파일 이름
                    imageUploadDto.getFileName(),           // 원본 파일 이름
                    imageUploadDto.getContentType(),        // 파일 컨텐츠 타입 (예: "image/jpeg")
                    imageUploadDto.getFileBytes()           // 파일 데이터
            );

            File file = convertToFile(multipartFile);

            // MD5 해시를 설정하고 S3에 업로드
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                String md5 = calculateMD5(file);
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentMD5(md5);
                // PutObjectRequest 생성
                PutObjectRequest putObjectRequest = new PutObjectRequest(imageUploadDto.getBucketName(), imageUploadDto.getFilePath(), fileInputStream, metadata);

                // S3 클라이언트를 사용하여 파일 업로드
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2).build();
                s3Client.putObject(putObjectRequest);

                System.out.println("File uploaded successfully with MD5 hash.");
            } catch (Exception e) {
                e.printStackTrace();
            }

//            try {
//                amazonS3Client.putObject(new PutObjectRequest(imageUploadDto.getBucketName(), imageUploadDto.getFilePath(),uploadFile).withCannedAcl(
//                CannedAccessControlList.PublicRead));
//            } catch (Exception e) {
//                log.error(e.getMessage());
//            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public File convertToFile(MockMultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private String calculateMD5(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        }

        byte[] md5Bytes = md.digest();
        return Base64.encodeBase64String(md5Bytes);
    }
}
