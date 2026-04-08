package com.project.iob.post.controller;

import com.project.iob.post.dto.PostDetailDto;
import com.project.iob.post.service.PostDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostDetailController {

    private final PostDetailService postDetailService;

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailDto> getPostDetail(
            @PathVariable("postId") Long postId,
            @RequestParam(value = "userEmail", required = false, defaultValue = "") String userEmail) {
        PostDetailDto post = postDetailService.getPostDetail(postId, userEmail);
        return ResponseEntity.ok(post);
    }
    
 // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody PostDetailDto postDetailDto) {
        postDetailService.updatePost(
            postId,
            postDetailDto.getTitle(),
            postDetailDto.getContent(),
            postDetailDto.getCategoryCode()
        );
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("postId") Long postId,
            @RequestParam("userEmail") String userEmail) {
        postDetailService.deletePost(postId, userEmail);
        return ResponseEntity.ok().build();
    }
    
    // 게시글 좋아요 토글
    @PostMapping("/{postId}/like")
    public ResponseEntity<Boolean> togglePostLike(
            @PathVariable("postId") Long postId,
            @RequestParam("userEmail") String userEmail) {
        boolean liked = postDetailService.togglePostLike(postId, userEmail);
        return ResponseEntity.ok(liked);
    }
    
    // 신고 등록
    @PostMapping("/report")
    public ResponseEntity<Void> insertReport(
            @RequestParam("targetCode") String targetCode,
            @RequestParam("targetId") Long targetId,
            @RequestParam("reporterEmail") String reporterEmail,
            @RequestParam("reasonCode") String reasonCode) {
        postDetailService.insertReport(targetCode, targetId, reporterEmail, reasonCode);
        return ResponseEntity.ok().build();
    }
}