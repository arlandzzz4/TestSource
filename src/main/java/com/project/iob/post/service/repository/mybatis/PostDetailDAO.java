package com.project.iob.post.service.repository.mybatis;

import com.project.iob.post.dto.PostDetailDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostDetailDAO {
    // 게시글 상세 조회
    PostDetailDto getPostDetail(Long postId);

    // 게시글 수정
    void updatePost(@Param("postId") Long postId,
                    @Param("title") String title,
                    @Param("content") String content,
                    @Param("categoryCode") String categoryCode);

    // 게시글 삭제
    void deletePost(@Param("postId") Long postId,
                    @Param("userEmail") String userEmail);

    // 게시글 좋아요 추가
    void insertPostLike(@Param("postId") Long postId, @Param("userEmail") String userEmail);

    // 게시글 좋아요 취소
    void deletePostLike(@Param("postId") Long postId, @Param("userEmail") String userEmail);

    // 게시글 좋아요 여부 확인
    int checkPostLike(@Param("postId") Long postId, @Param("userEmail") String userEmail);

    // 신고 등록
    void insertReport(@Param("targetCode") String targetCode,
                      @Param("targetId") Long targetId,
                      @Param("reporterEmail") String reporterEmail,
                      @Param("reasonCode") String reasonCode);
    //좋아요 알림
    String findAuthorEmailByPostId(@Param("postId") Long postId);
    
    // 게시글 제목 조회
    String getTitleByPostId(@Param("postId") Long postId);
}
