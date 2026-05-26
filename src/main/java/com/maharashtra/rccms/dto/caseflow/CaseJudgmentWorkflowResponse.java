package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CaseJudgmentWorkflowResponse {

    private Long caseId;
    private String caseNo;
    private String caseStatus;
    private String workflowStatus;
    private String draftSummary;
    private String finalSummary;
    private String publishedSummary;
    private String digitalSignatureRef;
    private Instant publishedAt;
    private Instant updatedAt;
    private List<String> allowedActions = new ArrayList<>();
    /** True when current login may edit judgment text (clerk: CLERK_DRAFT; PO: PO_DRAFT / PO_SCRUTINY). */
    private boolean editable;
    /** True when clerk may submit to PO (CLERK_DRAFT only). */
    private boolean submittable;
    /** PRESIDING_OFFICER or CLERK for current login. */
    private String actorRole;

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

    public String getDigitalSignatureRef() {
        return digitalSignatureRef;
    }

    public void setDigitalSignatureRef(String digitalSignatureRef) {
        this.digitalSignatureRef = digitalSignatureRef;
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

    public List<String> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<String> allowedActions) {
        this.allowedActions = allowedActions != null ? allowedActions : new ArrayList<>();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isSubmittable() {
        return submittable;
    }

    public void setSubmittable(boolean submittable) {
        this.submittable = submittable;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }
}
