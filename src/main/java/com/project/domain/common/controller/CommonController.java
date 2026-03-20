package com.project.domain.common.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.domain.auth.dto.LoginResponseDto;
import com.project.domain.common.service.FileService;
import com.project.domain.user.dto.UserResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Auth API", description = "사용자 등록, 로그인, 로그아웃, 리프레시 API")
@Slf4j
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonController {

    private final FileService fileService;
    
    @Operation(summary = "파일업로드", description = "하나의 파일을 업로드합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestPart MultipartFile file) throws IOException {
        
    	String filename = fileService.upload(file); // ★ 실제 파일 업로드 로직은 MultipartFile을 받아서 처리해야 함 (예시에서는 생략)
        // 3. 쿠키를 헤더에 추가하고, 바디에는 Access Token이 포함된 DTO를 담아 반환
        return ResponseEntity.ok()
                .body(filename);
    }
    
    @Operation(summary = "파일업로드", description = "다수의 파일을 업로드합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/uploadList")
    public ResponseEntity<List<String>> uploadList(@RequestPart List<MultipartFile> files) throws IOException{
        
    	fileService.uploadList(null); // ★ 실제 파일 업로드 로직은 MultipartFile을 받아서 처리해야 함 (예시에서는 생략)
    	List<String> filenames = fileService.uploadList(files);
        // 3. 쿠키를 헤더에 추가하고, 바디에는 Access Token이 포함된 DTO를 담아 반환
        return ResponseEntity.ok()
                .body(filenames);
    }
    
    @Operation(summary = "파일 삭제", description = "파일을 삭제합니다. 파일명이 존재하면 정상.")
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Void> delete(@PathVariable(value = "fileName") String fileName) {
    	fileService.delete(fileName);
        
    	// 2. 204 No Content 반환 (성공했지만 줄 데이터는 없음)
        return ResponseEntity.noContent().build(); 
	}
    
}