package com.project.iob.photo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
    /**
     * [단일 이미지 업로드]
     */
    String upload(MultipartFile file, Long postId) throws IOException;

    /**
     * [다중 이미지 업로드]
     */
    List<String> uploadList(List<MultipartFile> files, Long postId) throws IOException;

    /**
     * [이미지 삭제]
     */
    void delete(String fileName);
}
