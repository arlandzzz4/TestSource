package com.project.iob.post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.iob.post.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Post API", description = "게시글 API")
@Slf4j
@RestController
@RequestMapping("/api/post/comment")
@RequiredArgsConstructor
public class CommentController {
	
	private final CommentService commentService;
	
	
	@Operation(summary = "총 댓글 검색", description = "전체 댓글 수를 조회합니다. 성공 시 총 댓글 수를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (총 게시글 수 반환)"),
        @ApiResponse(responseCode = "404", description = "존재하는 유저가 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/totalcnt")
    public ResponseEntity<Integer> searchCommentTotalCount() {
    	int cnt = commentService.searcCommentCount(null);
    	
        return ResponseEntity.ok(cnt); 
	}
    
    @Operation(summary = "오늘 작성된 댓글 수 검색", description = "오늘 작성된 댓글 수를 조회합니다. 성공 시 오늘 작성된 댓글 수를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (오늘 작성된 댓글 수 반환)"),
        @ApiResponse(responseCode = "404", description = ""),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/todaycnt")
    public ResponseEntity<Integer> searchPostTodayCount(){
    	//오늘
    	String today = java.time.LocalDate.now().toString();
    	int cnt = commentService.searcCommentCount(today);
    	
        return ResponseEntity.ok(cnt); 
	}
}
