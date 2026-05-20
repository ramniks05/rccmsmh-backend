package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;
import java.time.LocalDate;

public class OfficerCauseListItemResponse {
    private Integer rowNo;
    private LocalDate causeDate;
    private Long caseId;
    private String caseNo;
    private String caseStatus;
    private Long filingApplicationId;
    private Long hearingId;
    private Integer hearingNo;
    private LocalDate hearingDate;
    private String hearingStatus;
    private Long roznamaId;
    private String roznamaStatus;
    private String proceedingStage;
    private String caseCategoryName;
    private String draftContent;
    private String finalContent;
    private Instant roznamaUpdatedAt;
    private Boolean canEdit;
    private Boolean roznamaLinkedToHearing;

    public Integer getRowNo() {
        return rowNo;
    }

    public void setRowNo(Integer rowNo) {
        this.rowNo = rowNo;
    }

    public LocalDate getCauseDate() {
        return causeDate;
    }

    public void setCauseDate(LocalDate causeDate) {
        this.causeDate = causeDate;
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

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public Long getFilingApplicationId() {
        return filingApplicationId;
    }

    public void setFilingApplicationId(Long filingApplicationId) {
        this.filingApplicationId = filingApplicationId;
    }

    public Long getHearingId() {
        return hearingId;
    }

    public void setHearingId(Long hearingId) {
        this.hearingId = hearingId;
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

    public String getHearingStatus() {
        return hearingStatus;
    }

    public void setHearingStatus(String hearingStatus) {
        this.hearingStatus = hearingStatus;
    }

    public Long getRoznamaId() {
        return roznamaId;
    }

    public void setRoznamaId(Long roznamaId) {
        this.roznamaId = roznamaId;
    }

    public String getRoznamaStatus() {
        return roznamaStatus;
    }

    public void setRoznamaStatus(String roznamaStatus) {
        this.roznamaStatus = roznamaStatus;
    }

    public String getProceedingStage() {
        return proceedingStage;
    }

    public void setProceedingStage(String proceedingStage) {
        this.proceedingStage = proceedingStage;
    }

    public String getCaseCategoryName() {
        return caseCategoryName;
    }

    public void setCaseCategoryName(String caseCategoryName) {
        this.caseCategoryName = caseCategoryName;
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

    public Instant getRoznamaUpdatedAt() {
        return roznamaUpdatedAt;
    }

    public void setRoznamaUpdatedAt(Instant roznamaUpdatedAt) {
        this.roznamaUpdatedAt = roznamaUpdatedAt;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Boolean getRoznamaLinkedToHearing() {
        return roznamaLinkedToHearing;
    }

    public void setRoznamaLinkedToHearing(Boolean roznamaLinkedToHearing) {
        this.roznamaLinkedToHearing = roznamaLinkedToHearing;
    }
}
