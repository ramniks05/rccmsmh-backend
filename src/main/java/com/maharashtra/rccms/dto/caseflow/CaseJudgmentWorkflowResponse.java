package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;

public class CaseJudgmentWorkflowResponse {
    private Long caseId;
    private String caseNo;
    private String caseStatus;
    private String workflowStatus;
    private String draftSummary;
    private String finalSummary;
    private String publishedSummary;
    private Instant publishedAt;
    private Instant updatedAt;

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

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getDraftSummary() {
        return draftSummary;
    }

    public void setDraftSummary(String draftSummary) {
        this.draftSummary = draftSummary;
    }

    public String getFinalSummary() {
        return finalSummary;
    }

    public void setFinalSummary(String finalSummary) {
        this.finalSummary = finalSummary;
    }

    public String getPublishedSummary() {
        return publishedSummary;
    }

    public void setPublishedSummary(String publishedSummary) {
        this.publishedSummary = publishedSummary;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
