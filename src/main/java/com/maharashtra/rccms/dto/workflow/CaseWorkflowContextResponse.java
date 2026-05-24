package com.maharashtra.rccms.dto.workflow;

import java.util.ArrayList;
import java.util.List;

public class CaseWorkflowContextResponse {

    private Long caseId;
    private String caseNo;
    private String caseStatus;
    private Long caseCategoryId;
    private String caseCategoryCode;
    private String caseCategoryName;
    private String blueprintCode;
    private Long filingApplicationId;
    private WorkflowPhaseContextResponse filing;
    private WorkflowPhaseContextResponse scrutiny;
    private WorkflowArtifactContextResponse notice;
    private WorkflowArtifactContextResponse roznama;
    private WorkflowArtifactContextResponse judgment;
    private List<WorkflowHearingContextResponse> hearings = new ArrayList<>();
    private List<String> allowedActions = new ArrayList<>();

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

    public Long getCaseCategoryId() {
        return caseCategoryId;
    }

    public void setCaseCategoryId(Long caseCategoryId) {
        this.caseCategoryId = caseCategoryId;
    }

    public String getCaseCategoryCode() {
        return caseCategoryCode;
    }

    public void setCaseCategoryCode(String caseCategoryCode) {
        this.caseCategoryCode = caseCategoryCode;
    }

    public String getCaseCategoryName() {
        return caseCategoryName;
    }

    public void setCaseCategoryName(String caseCategoryName) {
        this.caseCategoryName = caseCategoryName;
    }

    public String getBlueprintCode() {
        return blueprintCode;
    }

    public void setBlueprintCode(String blueprintCode) {
        this.blueprintCode = blueprintCode;
    }

    public Long getFilingApplicationId() {
        return filingApplicationId;
    }

    public void setFilingApplicationId(Long filingApplicationId) {
        this.filingApplicationId = filingApplicationId;
    }

    public WorkflowPhaseContextResponse getFiling() {
        return filing;
    }

    public void setFiling(WorkflowPhaseContextResponse filing) {
        this.filing = filing;
    }

    public WorkflowPhaseContextResponse getScrutiny() {
        return scrutiny;
    }

    public void setScrutiny(WorkflowPhaseContextResponse scrutiny) {
        this.scrutiny = scrutiny;
    }

    public WorkflowArtifactContextResponse getNotice() {
        return notice;
    }

    public void setNotice(WorkflowArtifactContextResponse notice) {
        this.notice = notice;
    }

    public WorkflowArtifactContextResponse getRoznama() {
        return roznama;
    }

    public void setRoznama(WorkflowArtifactContextResponse roznama) {
        this.roznama = roznama;
    }

    public WorkflowArtifactContextResponse getJudgment() {
        return judgment;
    }

    public void setJudgment(WorkflowArtifactContextResponse judgment) {
        this.judgment = judgment;
    }

    public List<WorkflowHearingContextResponse> getHearings() {
        return hearings;
    }

    public void setHearings(List<WorkflowHearingContextResponse> hearings) {
        this.hearings = hearings;
    }

    public List<String> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<String> allowedActions) {
        this.allowedActions = allowedActions;
    }
}
