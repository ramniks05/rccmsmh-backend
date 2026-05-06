package com.maharashtra.rccms.dto.caseflow;

import com.maharashtra.rccms.dto.filing.OfficerInboxItemResponse;

import java.util.ArrayList;
import java.util.List;

public class OfficerDashboardResponse {
    private List<OfficerInboxItemResponse> pendingApplications = new ArrayList<>();
    private List<CaseInboxItemResponse> activeCases = new ArrayList<>();
    private List<CaseHearingResponse> todayHearings = new ArrayList<>();

    public List<OfficerInboxItemResponse> getPendingApplications() {
        return pendingApplications;
    }

    public void setPendingApplications(List<OfficerInboxItemResponse> pendingApplications) {
        this.pendingApplications = pendingApplications;
    }

    public List<CaseInboxItemResponse> getActiveCases() {
        return activeCases;
    }

    public void setActiveCases(List<CaseInboxItemResponse> activeCases) {
        this.activeCases = activeCases;
    }

    public List<CaseHearingResponse> getTodayHearings() {
        return todayHearings;
    }

    public void setTodayHearings(List<CaseHearingResponse> todayHearings) {
        this.todayHearings = todayHearings;
    }
}
