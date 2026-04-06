package com.project.iob.post.service.impl;
 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.iob.post.dto.PostWriteDto;
import com.project.iob.post.service.PostWriteService;
import com.project.iob.post.service.repository.mybatis.PostWriteDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostWriteServiceImpl implements PostWriteService {
    private final PostWriteDAO postWriteDAO;
 
    /**
     * [게시글 작성]
     */
    @Override
    public void createPost(PostWriteDto postWriteDto) {
        postWriteDAO.insert(postWriteDto);
    }
}