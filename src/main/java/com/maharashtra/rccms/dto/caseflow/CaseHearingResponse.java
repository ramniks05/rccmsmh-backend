package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;
import java.time.LocalDate;

public class CaseHearingResponse {
    private Long hearingId;
    private Long caseId;
    private String caseNo;
    private Integer hearingNo;
    private LocalDate hearingDate;
    private String status;
    private Boolean noticeGenerated;
    private String remarks;
    private Instant createdAt;
    private Instant updatedAt;

    public Long getHearingId() {
        return hearingId;
    }

    public void setHearingId(Long hearingId) {
        this.hearingId = hearingId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
