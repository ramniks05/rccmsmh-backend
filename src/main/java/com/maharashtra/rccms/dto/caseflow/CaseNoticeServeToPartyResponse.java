package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CaseNoticeServeToPartyResponse {
    private Long caseId;
    private Long hearingId;
    private Long noticeId;
    private String status;
    private String message;
    private List<String> selectedParties = new ArrayList<>();
    private String digitalSignatureRef;
    private Instant servedAt;
    private boolean noticeFinalized;
    private boolean noticeSigned;
    private boolean noticeServed;

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getHearingId() {
        return hearingId;
    }

    public void setHearingId(Long hearingId) {
        this.hearingId = hearingId;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Instant getServedAt() {
        return servedAt;
    }

    public void setServedAt(Instant servedAt) {
        this.servedAt = servedAt;
    }

    public boolean isNoticeFinalized() {
        return noticeFinalized;
    }

    public void setNoticeFinalized(boolean noticeFinalized) {
        this.noticeFinalized = noticeFinalized;
    }

    public boolean isNoticeSigned() {
        return noticeSigned;
    }

    public void setNoticeSigned(boolean noticeSigned) {
        this.noticeSigned = noticeSigned;
    }

    public boolean isNoticeServed() {
        return noticeServed;
    }

    public void setNoticeServed(boolean noticeServed) {
        this.noticeServed = noticeServed;
    }
}
