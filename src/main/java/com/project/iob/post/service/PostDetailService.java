package com.project.iob.post.service;

import com.project.iob.post.dto.PostDetailDto;

public interface PostDetailService {
	// 게시글 상세 조회
	PostDetailDto getPostDetail(Long postId);

	// 게시글 수정
	void updatePost(Long postId, String title, String content, String categoryCode);

	// 게시글 삭제
	void deletePost(Long postId, String userEmail);

	// 게시글 좋아요 토글 (좋아요/취소)
    boolean togglePostLike(Long postId, String userEmail);

    // 신고 등록
    void insertReport(String targetCode, Long targetId, String reporterEmail, String reasonCode);
}