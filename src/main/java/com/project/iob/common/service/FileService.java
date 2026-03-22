package com.project.iob.common.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	List<String> uploadList(List<MultipartFile> file) throws IOException;
    String upload(MultipartFile file) throws IOException;
    void delete(String fileName);
}