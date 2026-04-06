package com.project.iob.post.service;
 
import com.project.iob.post.dto.PostWriteDto;
 
public interface PostWriteService {
    /**
     * [게시글 작성]
     */
    public void createPost(PostWriteDto postWriteDto);
}
 