package com.project.iob.postwrite.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostWriteDto {
    @JsonIgnore  // 요청 시 무시, DB에서 자동생성
    private Long postId;
    private String userEmail;
    private String categoryCode;
    private String title;
    private String content;
}