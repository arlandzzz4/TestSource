package com.project.iob.photo.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.iob.photo.service.PhotoService;
import com.project.iob.photo.service.repository.mybatis.PhotoDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("prod") // 프로덕션 환경에서만 활성화
@RequiredArgsConstructor
public class S3PhotoServiceImpl implements PhotoService {

    private final AmazonS3 amazonS3;
    private final PhotoDAO photoDAO;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file, Long postId) throws IOException {
        // 1. 파일명 중복 방지
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        // 2. 메타데이터 설정 (파일 타입 및 크기)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        // 3. S3 업로드 실행
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)); // 외부에서 읽기 가능하도록 설정
        // 4. 저장된 파일의 URL 반환
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    @Override
    public List<String> uploadList(List<MultipartFile> files, Long postId) throws IOException {
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String fileName = upload(multipartFile, postId); // 개별 파일 업로드
            fileNames.add(fileName); // 업로드된 파일의 URL을 리스트에 추가
        }
        // post_images 테이블에 INSERT
        if (!fileNames.isEmpty()) {
            photoDAO.insertImages(postId, fileNames);
        }
        return fileNames;
    }

    @Override
    public void delete(String fileName) {
        // 파일 삭제 (파일명/Key 기준)
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}
