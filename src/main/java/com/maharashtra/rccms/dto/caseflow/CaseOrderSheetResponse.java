package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;

public class CaseOrderSheetResponse {
    private Long id;
    private Long caseId;
    private String caseNo;
    private String content;
    private String draftContent;
    private String finalContent;
    private String status;
    private String hearingOutcome;
    private Long currentHearingId;
    private String caseStatus;
    private String message;
    private String digitalSignatureRef;
    private Instant updatedAt;
    private String updatedByLoginId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDraftContent() {
        return draftContent;
    }

    public void setDraftContent(String draftContent) {
        this.draftContent = draftContent;
    }

    public String getFinalContent() {
        return finalContent;
    }

    public void setFinalContent(String finalContent) {
        this.finalContent = finalContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHearingOutcome() {
        return hearingOutcome;
    }

    public void setHearingOutcome(String hearingOutcome) {
        this.hearingOutcome = hearingOutcome;
    }

    public Long getCurrentHearingId() {
        return currentHearingId;
    }

    public void setCurrentHearingId(Long currentHearingId) {
        this.currentHearingId = currentHearingId;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDigitalSignatureRef() {
        return digitalSignatureRef;
    }

    public void setDigitalSignatureRef(String digitalSignatureRef) {
        this.digitalSignatureRef = digitalSignatureRef;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedByLoginId() {
        return updatedByLoginId;
    }

    public void setUpdatedByLoginId(String updatedByLoginId) {
        this.updatedByLoginId = updatedByLoginId;
    }
}
