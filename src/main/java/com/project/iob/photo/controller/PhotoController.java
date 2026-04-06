package com.project.iob.photo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.iob.photo.service.PhotoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Photo API", description = "이미지 업로드/삭제 API")
@Slf4j
@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @Operation(
        summary = "단일 이미지 파일 업로드",
        description = "S3 또는 서버에 하나의 이미지 파일을 업로드하고 저장된 URL을 반환합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "업로드 성공 (저장된 URL 반환)"),
        @ApiResponse(responseCode = "400", description = "파일이 없거나 잘못된 요청"),
        @ApiResponse(responseCode = "415", description = "이미지 형식이 아닌 파일 업로드 시도"),
        @ApiResponse(responseCode = "500", description = "S3 서버 전송 중 서버 에러")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(
            @Parameter(description = "게시글 ID", required = true)
            @RequestParam("postId") Long postId,
            @Parameter(description = "업로드할 이미지 파일 (jpg, png 등)", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart("file") MultipartFile file) throws IOException {
        // 1. 파일이 비어있는지 확인
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 없습니다.");
        }

        // 2. Content-Type이 image로 시작하는지 확인
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                 .body("이미지 파일만 허용됩니다.");
        }

        // 3. (심화) 파일 확장자 추가 체크
        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|webp)$")) {
            return ResponseEntity.badRequest().body("지원하지 않는 확장자입니다.");
        }

        String filename = photoService.upload(file, postId);
        return ResponseEntity.ok().body(filename);
    }

    @Operation(summary = "다중 이미지 파일 업로드", description = "다수의 파일을 업로드합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "업로드 성공 (저장된 URL 반환)"),
        @ApiResponse(responseCode = "400", description = "파일이 없거나 잘못된 요청"),
        @ApiResponse(responseCode = "415", description = "이미지 형식이 아닌 파일 업로드 시도"),
        @ApiResponse(responseCode = "500", description = "S3 서버 전송 중 서버 에러")
    })
    @PostMapping(value = "/uploadList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadList(
            @Parameter(description = "게시글 ID", required = true)
            @RequestParam("postId") Long postId,
            @Parameter(description = "업로드할 이미지 파일 목록 (jpg, png 등)", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart("file") List<MultipartFile> files) throws IOException {
        // 1. 파일 리스트가 비어있는지 체크
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("업로드할 파일이 없습니다.");
        }

        // 2. 모든 파일이 이미지인지 전수 조사
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                     .body("이미지 파일만 업로드 가능합니다: " + file.getOriginalFilename());
            }
        }

        List<String> filenames = photoService.uploadList(files, postId);
        // 3. 업로드된 파일 URL 목록 반환
        return ResponseEntity.ok().body(filenames);
    }

    @Operation(
        summary = "이미지 삭제",
        description = "서버 또는 S3에서 특정 파일을 삭제합니다. 파일명이 정확히 일치해야 합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "파일 삭제 성공 (반환 데이터 없음)"),
        @ApiResponse(responseCode = "404", description = "해당 파일명을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "S3 통신 오류 또는 삭제 권한 없음")
    })
    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 파일의 이름 (확장자 포함, 예: image123.jpg)", required = true, example = "test-image.png")
            @PathVariable(value = "fileName") String fileName) {
        photoService.delete(fileName);
        // 204 No Content 반환 (성공했지만 줄 데이터는 없음)
        return ResponseEntity.noContent().build();
    }
}
