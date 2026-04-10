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
@Profile({ "local", "dev" })
@RequiredArgsConstructor
public class LocalPhotoServiceImpl implements PhotoService {

	private final FileService fileService; // 파일 저장/삭제는 FileService에 위임
	private final PhotoDAO photoDAO; // DB 처리만 담당

	@Override
	public String upload(MultipartFile file, Long postId) throws IOException {
		String url = fileService.upload(file);
		log.info("===== upload 호출됨 - postId: {}, url: {}", postId, url);
		return url;
	}

	@Override
	public List<String> uploadList(List<MultipartFile> files, Long postId) throws IOException {
		List<String> urls = fileService.uploadList(files);
		log.info("===== uploadList 호출됨 - postId: {}, urls: {}", postId, urls);
		if (!urls.isEmpty()) {
			photoDAO.insertImages(postId, urls);
		}
		return urls;
	}

	@Override
	public void delete(String fileName) {
//		fileService.delete(fileName);
		photoDAO.deleteImage(fileName); // DB에서도 삭제
	}
}