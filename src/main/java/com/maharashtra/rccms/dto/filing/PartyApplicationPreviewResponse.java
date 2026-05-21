package com.maharashtra.rccms.dto.filing;

import com.maharashtra.rccms.dto.caseflow.CaseHearingResponse;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetHistoryResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Party/advocate view: application details plus case proceedings visible without digital sign.
 */
public class PartyApplicationPreviewResponse {

    private OfficerApplicationDetailResponse application;
    private List<CaseNoticeResponse> notices = new ArrayList<>();
    private List<CaseHearingResponse> hearings = new ArrayList<>();
    private List<CaseOrderSheetHistoryResponse> orderSheetHistory = new ArrayList<>();
    private ApplicationHistoryListResponse applicationHistory;
    private String judgmentWorkflowStatus;
    private String judgmentSummary;

    public OfficerApplicationDetailResponse getApplication() {
        return application;
    }

    public void setApplication(OfficerApplicationDetailResponse application) {
        this.application = application;
    }

    public List<CaseNoticeResponse> getNotices() {
        return notices;
    }

    public void setNotices(List<CaseNoticeResponse> notices) {
        this.notices = notices;
    }

    public List<CaseHearingResponse> getHearings() {
        return hearings;
    }

    public void setHearings(List<CaseHearingResponse> hearings) {
        this.hearings = hearings;
    }

    public List<CaseOrderSheetHistoryResponse> getOrderSheetHistory() {
        return orderSheetHistory;
    }

    public void setOrderSheetHistory(List<CaseOrderSheetHistoryResponse> orderSheetHistory) {
        this.orderSheetHistory = orderSheetHistory;
    }

    public ApplicationHistoryListResponse getApplicationHistory() {
        return applicationHistory;
    }

    public void setApplicationHistory(ApplicationHistoryListResponse applicationHistory) {
        this.applicationHistory = applicationHistory;
    }

    public String getJudgmentWorkflowStatus() {
        return judgmentWorkflowStatus;
    }

    public void setJudgmentWorkflowStatus(String judgmentWorkflowStatus) {
        this.judgmentWorkflowStatus = judgmentWorkflowStatus;
    }

    public String getJudgmentSummary() {
        return judgmentSummary;
    }

    public void setJudgmentSummary(String judgmentSummary) {
        this.judgmentSummary = judgmentSummary;
    }
}
