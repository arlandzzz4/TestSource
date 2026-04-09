package com.project.iob.common.service.impl;

import java.io.IOException;
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
import com.project.iob.common.service.FileService;

import lombok.RequiredArgsConstructor;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class S3FileServiceImpl implements FileService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}") // 2.x 버전은 보통 cloud.aws 로 시작합니다.
    private String bucket;

    @Override
    public String upload(MultipartFile file) throws IOException {
        // 1. 파일명 중복 방지
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        // 2. 메타데이터 설정 (파일 타입 및 크기)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        // 3. S3 업로드 실행
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
        // 4. 저장된 파일의 URL 반환
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    @Override
    public void delete(String fileName) {
        // 파일 삭제 (파일명/Key 기준)
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    @Override
    public List<String> uploadList(List<MultipartFile> file) throws IOException {
        List<String> fileNames = new java.util.ArrayList<>();
        String fileName = null;
        for (MultipartFile multipartFile : file) {
            fileName = upload(multipartFile); // 개별 파일 업로드
            fileNames.add(fileName); // 업로드된 파일의 URL을 리스트에 추가
        }	
        return fileNames;
    }
}