package com.project.iob.photo.service.impl;

import java.io.IOException;
import java.util.List;

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
@Profile("prod")
@RequiredArgsConstructor
public class S3PhotoServiceImpl implements PhotoService {

    private final FileService fileService;  // 파일 저장/삭제는 FileService에 위임
    private final PhotoDAO photoDAO;        // DB 처리만 담당

    @Override
    public String upload(MultipartFile file, Long postId) throws IOException {
        return fileService.upload(file);
    }

    @Override
    public List<String> uploadList(List<MultipartFile> files, Long postId) throws IOException {
        List<String> urls = fileService.uploadList(files);
        if (!urls.isEmpty()) {
            photoDAO.insertImages(postId, urls);
        }
        return urls;
    }

    @Override
    public void delete(String fileName) {
        fileService.delete(fileName);
    }
}