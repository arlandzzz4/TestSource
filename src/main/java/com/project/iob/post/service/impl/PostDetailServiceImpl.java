package com.project.iob.post.service.impl;

import com.project.iob.post.dto.PostDetailDto;
import com.project.iob.post.service.PostDetailService;
import com.project.iob.post.service.repository.mybatis.PostDetailDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostDetailServiceImpl implements PostDetailService {

    private final PostDetailDAO postDetailDAO;

    @Override
    public PostDetailDto getPostDetail(Long postId, String userEmail) {
        PostDetailDto post = postDetailDAO.getPostDetail(postId);
        int liked = postDetailDAO.checkPostLike(postId, userEmail);
        post.setLiked(liked > 0);
        return post;
    }

    @Override
    public void updatePost(Long postId, String title, String content, String categoryCode) {
        postDetailDAO.updatePost(postId, title, content, categoryCode);
    }

    @Override
    public void deletePost(Long postId, String userEmail) {
        postDetailDAO.deletePost(postId, userEmail);
    }

    @Override
    public boolean togglePostLike(Long postId, String userEmail) {
        int count = postDetailDAO.checkPostLike(postId, userEmail);
        if (count > 0) {
            postDetailDAO.deletePostLike(postId, userEmail);
            return false; // 좋아요 취소
        } else {
            postDetailDAO.insertPostLike(postId, userEmail);
            return true; // 좋아요 추가
        }
    }

    @Override
    public void insertReport(String targetCode, Long targetId, String reporterEmail, String reasonCode) {
        postDetailDAO.insertReport(targetCode, targetId, reporterEmail, reasonCode);
    }
}