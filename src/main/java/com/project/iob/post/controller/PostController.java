package com.project.iob.post.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.iob.post.dto.MyPostRequestDto;
import com.project.iob.post.dto.MyPostResponseDto;
import com.project.iob.post.dto.PostRequestDto;
import com.project.iob.post.dto.PostResponseDto;
import com.project.iob.post.service.PostService;
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
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
	
	private final PostService postService;
	private final ReportService reportService;
	
	@Operation(summary = "게시글 조회(페이징)", description = "게시글을 페이징하여 조회합니다. 페이지 번호와 페이지 크기를 쿼리 파라미터로 전달받아 해당 페이지의 게시글 목록을 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (게시글 정보 반환)"),
        @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/post")
    public ResponseEntity<List<PostResponseDto>> searchPosts(
    		@ParameterObject PostRequestDto postRequestDto
    		){
    	List<PostResponseDto> posts = postService.searchPosts(postRequestDto);
    	
        return ResponseEntity.ok(posts); 
	}
	
	@Operation(summary = "총 게시글 검색", description = "전체 게시글 수를 조회합니다. 성공 시 총 게시글 수를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (총 게시글 수 반환)"),
        @ApiResponse(responseCode = "404", description = "존재하는 게시글 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/totalcnt")
    public ResponseEntity<Integer> searchPostTotalCount(
    		@ParameterObject PostRequestDto postRequestDto
    		) {
    	int cnt = postService.searchPostCount(postRequestDto);
    	
        return ResponseEntity.ok(cnt); 
	}
    
    @Operation(summary = "오늘 작성된 게시글 수 검색", description = "오늘 작성된 게시글 수를 조회합니다. 성공 시 오늘 작성된 게시글 수를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (오늘 작성된 게시글 수 반환)"),
        @ApiResponse(responseCode = "404", description = "존재하는 게시글 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/todaycnt")
    public ResponseEntity<Integer> searchPostTodayCount(){
    	//오늘
    	String today = java.time.LocalDate.now().toString();
    	
    	PostRequestDto postRequestDto = new PostRequestDto(today);
    	int cnt = postService.searchPostCount(postRequestDto);
    	
        return ResponseEntity.ok(cnt); 
	}
    
    @Operation(summary = "게시글 삭제", description = "게시글 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "존재하는 게시글 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PatchMapping("/delete")
    public ResponseEntity<Void> deletePost(
    		@RequestBody PostRequestDto postRequestDto
        ){
    	postService.updatePostDelYn(postRequestDto);
    	//처리
    	if(postRequestDto != null && postRequestDto.reportId() != null) {
	    	ReportRequestDto reportRequestDto = new ReportRequestDto(postRequestDto.reportId(),"02");
	    	reportService.updateReportStatusCode(reportRequestDto);
    	}
    	
        return ResponseEntity.noContent().build(); 
	}
    
    
    @Operation(summary = "내 게시글 조회", description = "로그인된 사용자의 삭제되지 않은 게시글 목록을 최신순으로 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/my")
    public ResponseEntity<List<MyPostResponseDto>> searchMyPosts(
            @ParameterObject MyPostRequestDto myPostRequestDto
    ) {
        List<MyPostResponseDto> posts = postService.searchMyPosts(myPostRequestDto);
        return ResponseEntity.ok(posts);
    }
    
    @Operation(summary = "내 게시글 수 조회", description = "로그인된 사용자의 삭제되지 않은 게시글 목록 수를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/my/count")
    public ResponseEntity<Integer> searchMyPostCount(
            @ParameterObject MyPostRequestDto myPostRequestDto
    ) {
        return ResponseEntity.ok(postService.searchMyPostCount(myPostRequestDto));
    }
}
