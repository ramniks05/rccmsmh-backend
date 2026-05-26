package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseNoticeServeToPartyRequest {
    private Long hearingId;
    private String noticeType;
    private String draftContent;
    private String finalContent;
    private List<String> selectedParties = new ArrayList<>();
    /** Optional; backend generates one when omitted. */
    private String digitalSignatureRef;

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

    public String getFinalContent() {
        return finalContent;
    }

    public void setFinalContent(String finalContent) {
        this.finalContent = finalContent;
    }

    public List<String> getSelectedParties() {
        return selectedParties;
    }

    public void setSelectedParties(List<String> selectedParties) {
        this.selectedParties = selectedParties;
    }

    public String getDigitalSignatureRef() {
        return digitalSignatureRef;
    }

    public void setDigitalSignatureRef(String digitalSignatureRef) {
        this.digitalSignatureRef = digitalSignatureRef;
    }
}
