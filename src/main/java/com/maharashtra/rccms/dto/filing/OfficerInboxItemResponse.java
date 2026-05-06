package com.maharashtra.rccms.dto.filing;

import java.time.Instant;

public class OfficerInboxItemResponse {

    private Long applicationId;
    private String applicationNo;
    private String clientApplicationRef;
    private Long caseId;
    private Long caseCategoryId;
    private String caseCategoryName;
    private Long subjectId;
    private String subjectName;
    private Long officeId;
    private String officeName;
    private String status;
    private String processingStage;
    private String currentAssigneeRole;
    private String applicationDescription;
    private String filedByName;
    private String filedByRole;
    private Instant submittedAt;
    private Instant createdAt;

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

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
