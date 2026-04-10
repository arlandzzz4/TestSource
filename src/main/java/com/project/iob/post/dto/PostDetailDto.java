package com.project.iob.post.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDetailDto {
    private Long postId;
    private String userEmail;
    private String nickname;        // users 테이블에서 가져옴
    private String categoryCode;
    private String title;
    private String content;
    private String delYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;          // likes 테이블에서 count
    private int commentCount;       // comments 테이블에서 count
    private boolean isLiked;
    private List<String> imageUrls; // // 게시글 이미지 URL목록 (post_images테이블에서 가져옴)
}