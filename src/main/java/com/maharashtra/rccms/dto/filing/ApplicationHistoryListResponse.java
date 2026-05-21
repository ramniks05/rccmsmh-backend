package com.maharashtra.rccms.dto.filing;

import java.util.ArrayList;
import java.util.List;

/**
 * Full timeline: filing workflow (draft → case registration) plus case proceeding events
 * (hearings, notices, order sheets, judgment) when a case exists.
 */
public class ApplicationHistoryListResponse {

    private Long applicationId;
    private String applicationNo;
    private Long caseId;
    private String caseNo;
    private int filingCount;
    private int proceedingCount;
    private String status;
    private String processingStage;
    private String processingStageLabel;
    private String currentAssigneeRole;
    private int totalCount;
    /** True when timeline was reconstructed from legacy fields (no persisted history rows). */
    private boolean synthetic;
    private List<ApplicationHistoryResponse> entries = new ArrayList<>();

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

    public int getFilingCount() {
        return filingCount;
    }

    public void setFilingCount(int filingCount) {
        this.filingCount = filingCount;
    }

    public int getProceedingCount() {
        return proceedingCount;
    }

    public void setProceedingCount(int proceedingCount) {
        this.proceedingCount = proceedingCount;
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

    public String getProcessingStageLabel() {
        return processingStageLabel;
    }

    public void setProcessingStageLabel(String processingStageLabel) {
        this.processingStageLabel = processingStageLabel;
    }

    public String getCurrentAssigneeRole() {
        return currentAssigneeRole;
    }

    public void setCurrentAssigneeRole(String currentAssigneeRole) {
        this.currentAssigneeRole = currentAssigneeRole;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isSynthetic() {
        return synthetic;
    }

    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    public List<ApplicationHistoryResponse> getEntries() {
        return entries;
    }

    public void setEntries(List<ApplicationHistoryResponse> entries) {
        this.entries = entries != null ? entries : new ArrayList<>();
    }
}
