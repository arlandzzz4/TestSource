package com.project.iob.post.entity;

import java.time.LocalDateTime;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "posts")
public class Post {

	@Id
    @Column(length = 100, unique = true)
    private Long postId;
    
    private String userEmail;
    
    @Column(name = "category_code")
    private String categoryCode;
    
    @Column(name = "provider_id")
    private String providerId;
    
    private String title;
    
    private String content;
    
    @Column(name = "del_yn")
    private String delYn;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "delete_id")
    private String deleteId;
    
    public static Post createEmpty() {
        return new Post();
    }
    
    @Builder
    public Post(Long postId, String userEmail, String categoryCode, String providerId, String title
    		, String content, String delYn) {
        this.postId = postId;
        this.userEmail = userEmail;
        this.categoryCode = categoryCode;
        this.providerId = providerId;
        this.title = title;
        this.content = content;
        this.delYn = delYn;
        this.createdAt = LocalDateTime.now(); // 생성 시 시간 자동 입력 예시
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 저장 시 자동으로 현재 시간 주입
        this.updatedAt = LocalDateTime.now(); // 저장 시 자동으로 현재 시간 주입
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreDestroy
    public void preDelete() {
        this.deletedAt = LocalDateTime.now();
    }
    
}