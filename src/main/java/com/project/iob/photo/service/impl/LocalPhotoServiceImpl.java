package com.project.iob.photo.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.iob.common.service.FileService;
import com.project.iob.photo.service.PhotoService;
import com.project.iob.photo.service.repository.mybatis.PhotoDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile({"local", "dev"}) // 로컬과 dev 프로필에서만 활성화
@RequiredArgsConstructor
public class LocalPhotoServiceImpl implements PhotoService {

    @Value("${file.upload-path}")
    private String uploadPath;

    private final PhotoDAO photoDAO;
    private final FileService fileService;

    @Override
    public String upload(MultipartFile file, Long postId) throws IOException {
    	String s = fileService.upload(null);
        StringBuilder fileName = new StringBuilder();
        fileName.append(UUID.randomUUID()).append("_").append(file.getOriginalFilename());

        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        File target = new File(uploadPath + fileName);
        file.transferTo(target);

        log.info("===== upload 호출됨 - postId: {}, fileName: {}", postId, fileName);

        return "/images/" + fileName; // 로컬 리소스 핸들러 주소 반환
    }

    @Override
    public List<String> uploadList(List<MultipartFile> files, Long postId) throws IOException {
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String fileName = upload(multipartFile, postId); // 개별 파일 업로드
            fileNames.add(fileName); // 업로드된 파일의 URL을 리스트에 추가
        }
        log.info("===== uploadList 호출됨 - postId: {}, fileNames: {}", postId, fileNames);
        // post_images 테이블에 INSERT
        if (!fileNames.isEmpty()) {
            photoDAO.insertImages(postId, fileNames);
        }
        return fileNames;
    }

    @Override
    public void delete(String fileName) {
        new File(uploadPath, fileName).delete();
    }
}
