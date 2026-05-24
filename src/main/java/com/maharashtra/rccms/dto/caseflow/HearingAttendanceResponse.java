package com.maharashtra.rccms.dto.caseflow;

import java.util.ArrayList;
import java.util.List;

public class HearingAttendanceResponse {
    private Long caseId;
    private Long hearingId;
    private boolean attendanceRequired;
    private boolean attendanceComplete;
    private List<HearingAttendanceItemResponse> entries = new ArrayList<>();

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

    public boolean isAttendanceRequired() {
        return attendanceRequired;
    }

    public void setAttendanceRequired(boolean attendanceRequired) {
        this.attendanceRequired = attendanceRequired;
    }

    public boolean isAttendanceComplete() {
        return attendanceComplete;
    }

    public void setAttendanceComplete(boolean attendanceComplete) {
        this.attendanceComplete = attendanceComplete;
    }

    public List<HearingAttendanceItemResponse> getEntries() {
        return entries;
    }

    public void setEntries(List<HearingAttendanceItemResponse> entries) {
        this.entries = entries != null ? entries : new ArrayList<>();
    }
}
