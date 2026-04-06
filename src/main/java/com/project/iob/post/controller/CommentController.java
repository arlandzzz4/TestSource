package com.project.iob.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.iob.post.dto.CommentRequestDto;
import com.project.iob.post.dto.PostResponseDto;
import com.project.iob.post.service.CommentService;
import com.project.iob.report.dto.ReportRequestDto;
import com.project.iob.report.service.ReportService;

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
	private final ReportService reportService;
	
	
	@Operation(summary = "총 댓글 검색", description = "전체 댓글 수를 조회합니다. 성공 시 총 댓글 수를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (총 게시글 수 반환)"),
        @ApiResponse(responseCode = "404", description = "존재하는 유저가 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/totalcnt")
    public ResponseEntity<Integer> searchCommentTotalCount(
    		@RequestParam("delYn") String delYn
    		) {
    	int cnt = commentService.searchCommentCount(delYn, null);
    	
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
    	int cnt = commentService.searchCommentCount(null, today);
    	
        return ResponseEntity.ok(cnt); 
	}
    
    @Operation(summary = "댓글 삭제", description = "댓글 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "존재하는 댓글 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PatchMapping("/delete")
    public ResponseEntity<Void> deleteComment(
    		@RequestBody CommentRequestDto commentRequestDto
        ){
    	commentService.updateCommentDelYn(commentRequestDto);
    	//처리
    	ReportRequestDto reportRequestDto = new ReportRequestDto(commentRequestDto.reportId(),"02");
    	reportService.updateReportStatusCode(reportRequestDto);
    	
        return ResponseEntity.noContent().build(); 
	}
    
    @Operation(summary = "댓글 조회(페이징)", description = "단순 최신 댓글 조회.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (댓글 정보 반환)"),
        @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/comment")
    public ResponseEntity<List<PostResponseDto>> searchComments(
    		//@ParameterObject PostRequestDto postRequestDto
    		){
    	List<PostResponseDto> posts = null;//postService.searchPosts(postRequestDto);
    	
        return ResponseEntity.ok(posts); 
	}
}
