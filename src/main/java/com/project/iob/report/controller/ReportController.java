package com.project.iob.report.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.iob.post.service.CommentService;
import com.project.iob.post.service.PostService;
import com.project.iob.report.dto.ReportRequestDto;
import com.project.iob.report.dto.ReportResponseDto;
import com.project.iob.report.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Report API", description = "신고 API")
@Slf4j
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final PostService postService;
    private final CommentService commentService;
    
    
    @Operation(summary = "총 신고 수 검색", description = "전체 신고 수를 조회합니다. 성공 시 총 신고 수를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (총 신고 수 반환)"),
        @ApiResponse(responseCode = "404", description = "존재하는 신고가 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/totalcnt")
    public ResponseEntity<Integer> searchReportTotalCount(
    		@RequestParam(name = "targetCode") String targetCode,
    		@RequestParam(name = "reportStatusCode") String reportStatusCode
    	) {
    	int cnt = reportService.searchReportCount(targetCode, null, reportStatusCode);
    	
        return ResponseEntity.ok(cnt); 
	}
    
    @Operation(summary = "오늘 신고된 신고 검색", description = "오늘 신고된 신고 수를 조회합니다. 성공 시 오늘 신고된 신고 수를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (오늘 신고된 신고 수 반환)"),
        @ApiResponse(responseCode = "404", description = "존재하는 신고가 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/todaycnt")
    public ResponseEntity<Integer> searchReportTodayCount(
    		@RequestParam(name = "targetCode") String targetCode,
    		@RequestParam(name = "reportStatusCode") String reportStatusCode
    		){
    	//오늘
    	String today = java.time.LocalDate.now().toString();
    	int cnt = reportService.searchReportCount(targetCode, today, reportStatusCode);
    	
        return ResponseEntity.ok(cnt); 
	}
    
    @Operation(summary = "리포트 조회(페이징)", description = "리포트를 페이징하여 조회합니다. 페이지 번호와 페이지 크기를 쿼리 파라미터로 전달받아 해당 페이지의 리포트 목록을 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (리포트 정보 반환)"),
        @ApiResponse(responseCode = "404", description = "리포트가 존재하지 않음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/report")
    public ResponseEntity<List<ReportResponseDto>> searcRepot(
    		@ParameterObject ReportRequestDto reportRequestDto
    		){
    	List<ReportResponseDto> reports = reportService.searchReports(reportRequestDto);
    	
        return ResponseEntity.ok(reports); 
	}
    
    @Operation(summary = "신고 상태 코드 수정", description = "신고 상태 코드 수정")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "존재하는 신고 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PatchMapping("/updateReportStatusCode")
    public ResponseEntity<Void> updateReportStatusCode(
    		@RequestBody ReportRequestDto reportRequestDto
        ){
    	reportService.updateReportStatusCode(reportRequestDto);
    	
        return ResponseEntity.noContent().build(); 
	}
}