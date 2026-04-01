package com.project.iob.common.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    List<String> uploadList(List<MultipartFile> file, Long postId) throws IOException;
    String upload(MultipartFile file, Long postId) throws IOException;
    void delete(String fileName);
}