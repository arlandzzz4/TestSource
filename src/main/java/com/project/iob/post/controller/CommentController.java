package com.project.iob.post.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.iob.post.dto.CommentRequestDto;
import com.project.iob.post.dto.CommentResponseDto;
import com.project.iob.post.service.CommentService;
import com.project.iob.report.dto.ReportRequestDto;
import com.project.iob.report.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Comment API", description = "댓글 API")
@Slf4j
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ReportService reportService;

    // 총 댓글 수 조회
    @Operation(summary = "총 댓글 검색")
    @GetMapping("/search/totalcnt")
    public ResponseEntity<Integer> searchCommentTotalCount(
            @ParameterObject CommentRequestDto commentRequestDto) {
        int cnt = commentService.searchCommentCount(commentRequestDto);
        return ResponseEntity.ok(cnt);
    }

    //오늘 댓글 수 조회
    @Operation(summary = "오늘 작성된 댓글 수 검색")
    @GetMapping("/search/todaycnt")
    public ResponseEntity<Integer> searchPostTodayCount() {
        String today = java.time.LocalDate.now().toString();
        CommentRequestDto commentRequestDto = new CommentRequestDto(today);
        int cnt = commentService.searchCommentCount(commentRequestDto);
        return ResponseEntity.ok(cnt);
    }

    //댓글 삭제
    @Operation(summary = "댓글 삭제")
    @PatchMapping("/delete")
    public ResponseEntity<Void> deleteComment(
            @RequestBody CommentRequestDto commentRequestDto) {
        commentService.updateCommentDelYn(commentRequestDto);
        ReportRequestDto reportRequestDto = new ReportRequestDto(commentRequestDto.reportId(), "02");
        reportService.updateReportStatusCode(reportRequestDto);
        return ResponseEntity.noContent().build();
    }

    //댓글 조회(페이징)
    @Operation(summary = "댓글 조회(페이징)")
    @GetMapping("/search/comment")
    public ResponseEntity<List<CommentResponseDto>> searchComments(
            @ParameterObject CommentRequestDto commentRequestDto) {
        List<CommentResponseDto> comments = commentService.searchComments(commentRequestDto);
        return ResponseEntity.ok(comments);
    }

    //댓글 목록 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentList(
            @PathVariable("postId") Long postId) {
        return ResponseEntity.ok(commentService.getCommentList(postId));
    }

    //댓글 등록
    @PostMapping
    public ResponseEntity<Void> insertComment(@RequestBody CommentRequestDto commentRequestDto) {
        commentService.insertComment(commentRequestDto);
        return ResponseEntity.ok().build();
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteCommentById(
            @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    //댓글 좋아요 토글
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Boolean> toggleCommentLike(
            @PathVariable("commentId") Long commentId,
            @RequestParam("userEmail") String userEmail) {
        boolean liked = commentService.toggleCommentLike(commentId, userEmail);
        return ResponseEntity.ok(liked);
    }
}