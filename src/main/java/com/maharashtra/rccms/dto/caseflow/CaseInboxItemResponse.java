package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;

public class CaseInboxItemResponse {
    private Long caseId;
    private String caseNo;
    private String status;
    private Long filingApplicationId;
    private Long caseCategoryId;
    private String caseCategoryName;
    private Long officeId;
    private String officeName;
    private Instant approvedAt;
    private Instant disposedAt;
    private Long roznamaId;
    private String proceedingStage;

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

    public Long getFilingApplicationId() {
        return filingApplicationId;
    }

    public void setFilingApplicationId(Long filingApplicationId) {
        this.filingApplicationId = filingApplicationId;
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

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Instant getDisposedAt() {
        return disposedAt;
    }

    public void setDisposedAt(Instant disposedAt) {
        this.disposedAt = disposedAt;
    }

    public Long getRoznamaId() {
        return roznamaId;
    }

    public void setRoznamaId(Long roznamaId) {
        this.roznamaId = roznamaId;
    }

    public String getProceedingStage() {
        return proceedingStage;
    }

    public void setProceedingStage(String proceedingStage) {
        this.proceedingStage = proceedingStage;
    }
}
