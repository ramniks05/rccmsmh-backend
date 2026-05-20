package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseNoticeDraftRequest {
    private Long hearingId;
    private String noticeType;
    private String draftContent;
    private List<String> selectedParties = new ArrayList<>();

    public Long getHearingId() {
        return hearingId;
    }

    public void setHearingId(Long hearingId) {
        this.hearingId = hearingId;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getDraftContent() {
        return draftContent;
    }

    public void setDraftContent(String draftContent) {
        this.draftContent = draftContent;
    }

    public List<String> getSelectedParties() {
        return selectedParties;
    }

    public void setSelectedParties(List<String> selectedParties) {
        this.selectedParties = selectedParties;
    }
}
