package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;

public class CaseJudgmentResponse {
    private Long caseId;
    private String caseNo;
    private String status;
    private Instant disposedAt;
    private String message;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getDisposedAt() {
        return disposedAt;
    }

    public void setDisposedAt(Instant disposedAt) {
        this.disposedAt = disposedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
