package com.project.iob.postwrite.service;
 
import com.project.iob.postwrite.dto.PostWriteDto;
 
public interface PostWriteService {
    /**
     * [게시글 작성]
     */
    public void createPost(PostWriteDto postWriteDto);
}
 