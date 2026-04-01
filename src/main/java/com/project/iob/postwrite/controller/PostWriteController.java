package com.project.iob.postwrite.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.iob.postwrite.dto.PostWriteDto;
import com.project.iob.postwrite.service.PostWriteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "PostWrite API", description = "게시글 작성 API")
@Slf4j
@RestController
@RequestMapping("/api/postwrite")
@RequiredArgsConstructor
public class PostWriteController {

    private final PostWriteService postWriteService;

    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다. 사용자 이메일, 카테고리 코드, 제목, 본문을 전달받아 게시글을 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/create")
    public ResponseEntity<Long> createPost(@RequestBody PostWriteDto postWriteDto) {
        postWriteService.createPost(postWriteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postWriteDto.getPostId());
    }
}