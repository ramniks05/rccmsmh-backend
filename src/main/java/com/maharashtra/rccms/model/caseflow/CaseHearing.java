package com.maharashtra.rccms.model.caseflow;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "case_hearing")
public class CaseHearing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseRegistry caseRegistry;

    @Column(name = "hearing_no", nullable = false)
    private Integer hearingNo;

    @Column(name = "hearing_date", nullable = false)
    private LocalDate hearingDate;

    @Column(name = "status", nullable = false, length = 32)
    private String status = "SCHEDULED";

    @Column(name = "notice_generated", nullable = false)
    private Boolean noticeGenerated = false;

    @Column(name = "remarks", length = 2000)
    private String remarks;

    @Column(name = "created_by_login_id", nullable = false, length = 150)
    private String createdByLoginId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public CaseRegistry getCaseRegistry() {
        return caseRegistry;
    }

    public void setCaseRegistry(CaseRegistry caseRegistry) {
        this.caseRegistry = caseRegistry;
    }

    public Integer getHearingNo() {
        return hearingNo;
    }

    public void setHearingNo(Integer hearingNo) {
        this.hearingNo = hearingNo;
    }

    public LocalDate getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(LocalDate hearingDate) {
        this.hearingDate = hearingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getNoticeGenerated() {
        return noticeGenerated;
    }

    public void setNoticeGenerated(Boolean noticeGenerated) {
        this.noticeGenerated = noticeGenerated;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreatedByLoginId() {
        return createdByLoginId;
    }

    public void setCreatedByLoginId(String createdByLoginId) {
        this.createdByLoginId = createdByLoginId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
