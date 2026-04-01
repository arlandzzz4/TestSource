package com.project.iob.common.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.iob.common.service.FileService;
import com.project.iob.common.service.repository.mybatis.PostImageDAO;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile({ "local", "dev" }) // 로컬과 dev 프로필에서만 활성화
@RequiredArgsConstructor
public class LocalFileServiceImpl implements FileService {

	@Value("${file.upload-path}")
	private String uploadPath;

	private final PostImageDAO postImageDAO;

	@Override
	public String upload(MultipartFile file, Long postId) throws IOException {
	    StringBuilder fileName = new StringBuilder();
	    fileName.append(UUID.randomUUID()).append("_").append(file.getOriginalFilename());

	    File dir = new File(uploadPath);
	    if (!dir.exists()) dir.mkdirs();

	    File target = new File(uploadPath + fileName);
	    file.transferTo(target);

	    return "/images/" + fileName; // 로컬 리소스 핸들러 주소 반환
	}

	@Override
	public List<String> uploadList(List<MultipartFile> files, Long postId) throws IOException {
		List<String> fileNames = new java.util.ArrayList<>();
		for (MultipartFile multipartFile : files) {
			String fileName = upload(multipartFile, postId);
			fileNames.add(fileName);
		}
		log.info("===== uploadList 호출됨 - postId: {}, fileNames: {}", postId, fileNames);
		if (!fileNames.isEmpty()) {
			log.info("===== insertImages 호출됨");
			postImageDAO.insertImages(postId, fileNames);
		}
		return fileNames;
	}

	@Override
	public void delete(String fileName) {
		new File(uploadPath, fileName).delete();
	}
}