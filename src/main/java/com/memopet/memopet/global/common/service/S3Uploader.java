package com.memopet.memopet.global.common.service;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * todo : 로컬에 File 로 저장하지 않고 오브젝트 스트림을 그대로 s3 로 업로드하는게 좋습니다.
     * todo : 안그러면 로컬에 있는 파일이 계속 쌓이지 않도록 지속적으로 지워줘야 하고 서버가 여러대일때도 문제가 될수있어서 복잡해지고 특히나 느려집니다.
     * todo : 유저가 업로드가 빨리 된다고 느끼게 하려면 어떤 방법이 있는지 고민할 필요가 있어보입니다.
     * todo : 예를들어 유저가 업로드할 파일을 선택할 때 즉시 비동기로 서버로 업로드를 하고 link 를 리턴받으면 link의 이미지를 보여주다가 다른 정보와 함께 "저장"을 하면 해당 link 정보도 같이 저장하는게 좋습니다.
     *    -> 이렇게 되면 "저장" 할때 상당히 가벼워 집니다. 특히나 이미지가 여러장 있는 경우 더 크게 느껴질 수 있습니다.
     *    -> 이때 이미지 상태관리를 해야할 수 있는데 이건 여러가지 방법으로 관리가 가능합니다.
     * 로컬 경로에 저장
     */
    public String uploadFileToS3(MultipartFile multipartFile, String filePath) {
        // MultipartFile -> File 로 변환
        File uploadFile = null;
        try {
            uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("[error]: MultipartFile -> 파일 변환 실패"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // S3에 저장된 파일 이름
        String fileName = filePath + "/" + UUID.randomUUID();

        // s3로 업로드 후 로컬 파일 삭제
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }


    /**
     * S3로 업로드
     * @param uploadFile : 업로드할 파일
     * @param fileName : 업로드할 파일 이름
     * @return 업로드 경로
     */
    public String putS3(File uploadFile, String fileName) {

        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
                CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * S3에 있는 파일 삭제
     * 영어 파일만 삭제 가능 -> 한글 이름 파일은 안됨
     */
    public void deleteS3(String filePath) {
        try{
            String key = filePath.substring(55); // 폴더/파일.확장자
            log.info("key : " +  key);
            try {
                amazonS3Client.deleteObject(bucket, key);
            } catch (AmazonServiceException e) {
                log.info(e.getErrorMessage());
            }

        } catch (Exception exception) {
            log.info(exception.getMessage());
        }
        log.info("[S3Uploader] : S3에 있는 파일 삭제");
    }

    /**
     * 로컬에 저장된 파일 지우기
     * @param targetFile : 저장된 파일
     */
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("[파일 업로드] : 파일 삭제 성공");
            return;
        }
        log.info("[파일 업로드] : 파일 삭제 실패");
    }

    /**
     * 로컬에 파일 업로드 및 변환
     * @param file : 업로드할 파일
     */
    private Optional<File> convert(MultipartFile file) throws IOException {
        // 로컬에서 저장할 파일 경로 : user.dir => 현재 디렉토리 기준
        String dirPath = System.getProperty("user.dir") + "/" + file.getOriginalFilename();
        File convertFile = new File(dirPath);
        if (convertFile.createNewFile()) {
            // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }
}