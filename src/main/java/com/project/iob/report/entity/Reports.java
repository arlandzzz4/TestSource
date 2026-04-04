package com.project.iob.report.entity;

import java.time.LocalDateTime;

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
@Table(name = "reports")
public class Reports {

	@Id
    @Column(unique = true)
    private Long reportId;
    
	@Column(name = "target_code")
    private String targetCode;
    
	@Column(name = "target_id")
    private Long targetId;
    
    @Column(name = "reporter_email")
    private String reporterEmail;
    
    @Column(name = "reason_code")
    private String reasonCode;
    
    private String detail;
    
    @Column(name = "report_status_code")
    private String reportStatusCode;
    
    @Column(name = "processed_by")
    private String processedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    
    public static Reports createEmpty() {
        return new Reports();
    }
    
    @Builder
    public Reports(Long reportId, String targetCode, Long targetId, String reporterEmail, String reasonCode, String detail, 
    		String reportStatusCode, String processedBy) {
        this.reportId = reportId;
        this.targetCode = targetCode;
        this.targetId = targetId;
        this.reporterEmail = reporterEmail;
        this.reasonCode = reasonCode;
        this.detail = detail;
        this.reportStatusCode = reportStatusCode;
        this.processedBy = processedBy;
        this.createdAt = LocalDateTime.now(); // 생성 시 시간 자동 입력 예시
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 저장 시 자동으로 현재 시간 주입
    }
    
    @PreUpdate
    public void preUpdate() {
        this.processedAt = LocalDateTime.now();
    }
    
}