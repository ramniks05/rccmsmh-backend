package com.maharashtra.rccms.dto.workflow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkflowHearingContextResponse {

    private Long hearingId;
    private Integer hearingNo;
    private LocalDate hearingDate;
    private String hearingStatus;
    private boolean noticeServed;
    private Long activeNoticeId;
    private String activeNoticeStatus;
    private List<String> allowedActions = new ArrayList<>();

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

    public boolean isNoticeServed() {
        return noticeServed;
    }

    public void setNoticeServed(boolean noticeServed) {
        this.noticeServed = noticeServed;
    }

    public Long getActiveNoticeId() {
        return activeNoticeId;
    }

    public void setActiveNoticeId(Long activeNoticeId) {
        this.activeNoticeId = activeNoticeId;
    }

    public String getActiveNoticeStatus() {
        return activeNoticeStatus;
    }

    public void setActiveNoticeStatus(String activeNoticeStatus) {
        this.activeNoticeStatus = activeNoticeStatus;
    }

    public List<String> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<String> allowedActions) {
        this.allowedActions = allowedActions;
    }
}
