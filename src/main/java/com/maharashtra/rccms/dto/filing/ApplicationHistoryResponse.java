package com.maharashtra.rccms.dto.filing;

import java.time.Instant;
import java.time.LocalDate;

public class ApplicationHistoryResponse {

    /** FILING = pre-case workflow; PROCEEDING = post-registration case events. */
    private String phase;
    private Long historyId;
    private Long applicationId;
    /** 1-based position in the timeline (oldest = 1). */
    private int sequence;
    private String action;
    private String actionLabel;
    private String remarks;
    private String actorRole;
    private String actorRoleLabel;
    private String actorLoginId;
    private String applicationNo;
    private String status;
    private Long caseId;
    private String caseNo;
    private String processingStage;
    private String processingStageLabel;
    private Instant createdAt;
    /** True for legacy rows reconstructed when no DB history exists. */
    private boolean synthetic;
    /** HEARING, NOTICE, ORDER_SHEET, JUDGMENT — links to case proceeding detail. */
    private String referenceType;
    private Long referenceId;
    private Integer hearingNo;
    private LocalDate hearingDate;
    private String noticeType;

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getActorRoleLabel() {
        return actorRoleLabel;
    }

    public void setActorRoleLabel(String actorRoleLabel) {
        this.actorRoleLabel = actorRoleLabel;
    }

    public String getActorLoginId() {
        return actorLoginId;
    }

    public void setActorLoginId(String actorLoginId) {
        this.actorLoginId = actorLoginId;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getProcessingStage() {
        return processingStage;
    }

    public void setProcessingStage(String processingStage) {
        this.processingStage = processingStage;
    }

    public String getProcessingStageLabel() {
        return processingStageLabel;
    }

    public void setProcessingStageLabel(String processingStageLabel) {
        this.processingStageLabel = processingStageLabel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSynthetic() {
        return synthetic;
    }

    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
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

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }
}
