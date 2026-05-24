package com.maharashtra.rccms.dto.filing;

import com.maharashtra.rccms.dto.caseflow.CaseNoticeResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OfficerApplicationDetailResponse {

    private Long applicationId;
    private String applicationNo;
    private String clientApplicationRef;
    private Long caseId;
    private String caseNo;
    private Long caseCategoryId;
    private String caseCategoryName;
    private String status;
    private String processingStage;
    private String currentAssigneeRole;
    private Long officeId;
    private String officeName;
    private Long subjectId;
    private String subjectName;
    private String applicationDescription;
    private String filedByName;
    private String filedByRole;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant submittedAt;

    private ApplicationFormNestedPayload form;
    private ApplicationDisputedOrderPayload disputedOrder;
    private List<ApplicantRowPayload> applicants = new ArrayList<>();
    private List<RespondentRowPayload> respondents = new ArrayList<>();
    private List<DisputedLandPayload> disputedLands = new ArrayList<>();
    private List<ApplicationAttachmentPayload> attachments = new ArrayList<>();
    private ApplicationDocumentChecklistResponse documentChecklist;
    private ApplicationDescriptionPayload description;
    private List<CaseNoticeResponse> notices = new ArrayList<>();
    private ApplicationHistoryListResponse applicationHistory;
    private String blueprintCode;
    private List<String> allowedActions = new ArrayList<>();

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getClientApplicationRef() {
        return clientApplicationRef;
    }

    public void setClientApplicationRef(String clientApplicationRef) {
        this.clientApplicationRef = clientApplicationRef;
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

    public Long getCaseCategoryId() {
        return caseCategoryId;
    }

    public void setCaseCategoryId(Long caseCategoryId) {
        this.caseCategoryId = caseCategoryId;
    }

    public String getCaseCategoryName() {
        return caseCategoryName;
    }

    public void setCaseCategoryName(String caseCategoryName) {
        this.caseCategoryName = caseCategoryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessingStage() {
        return processingStage;
    }

    public void setProcessingStage(String processingStage) {
        this.processingStage = processingStage;
    }

    public String getCurrentAssigneeRole() {
        return currentAssigneeRole;
    }

    public void setCurrentAssigneeRole(String currentAssigneeRole) {
        this.currentAssigneeRole = currentAssigneeRole;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }

    public String getFiledByName() {
        return filedByName;
    }

    public void setFiledByName(String filedByName) {
        this.filedByName = filedByName;
    }

    public String getFiledByRole() {
        return filedByRole;
    }

    public void setFiledByRole(String filedByRole) {
        this.filedByRole = filedByRole;
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

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public ApplicationFormNestedPayload getForm() {
        return form;
    }

    public void setForm(ApplicationFormNestedPayload form) {
        this.form = form;
    }

    public ApplicationDisputedOrderPayload getDisputedOrder() {
        return disputedOrder;
    }

    public void setDisputedOrder(ApplicationDisputedOrderPayload disputedOrder) {
        this.disputedOrder = disputedOrder;
    }

    public List<ApplicantRowPayload> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<ApplicantRowPayload> applicants) {
        this.applicants = applicants;
    }

    public List<RespondentRowPayload> getRespondents() {
        return respondents;
    }

    public void setRespondents(List<RespondentRowPayload> respondents) {
        this.respondents = respondents;
    }

    public List<DisputedLandPayload> getDisputedLands() {
        return disputedLands;
    }

    public void setDisputedLands(List<DisputedLandPayload> disputedLands) {
        this.disputedLands = disputedLands;
    }

    public List<ApplicationAttachmentPayload> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ApplicationAttachmentPayload> attachments) {
        this.attachments = attachments;
    }

    public ApplicationDocumentChecklistResponse getDocumentChecklist() {
        return documentChecklist;
    }

    public void setDocumentChecklist(ApplicationDocumentChecklistResponse documentChecklist) {
        this.documentChecklist = documentChecklist;
    }

    public ApplicationDescriptionPayload getDescription() {
        return description;
    }

    public void setDescription(ApplicationDescriptionPayload description) {
        this.description = description;
    }

    public List<CaseNoticeResponse> getNotices() {
        return notices;
    }

    public void setNotices(List<CaseNoticeResponse> notices) {
        this.notices = notices;
    }

    public ApplicationHistoryListResponse getApplicationHistory() {
        return applicationHistory;
    }

    public void setApplicationHistory(ApplicationHistoryListResponse applicationHistory) {
        this.applicationHistory = applicationHistory;
    }

    public String getBlueprintCode() {
        return blueprintCode;
    }

    public void setBlueprintCode(String blueprintCode) {
        this.blueprintCode = blueprintCode;
    }

    public List<String> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<String> allowedActions) {
        this.allowedActions = allowedActions;
    }
}
