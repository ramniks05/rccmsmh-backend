package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RoznamaResponse {
    private Long id;
    private Long caseId;
    private String caseNo;
    private Long hearingId;
    private String content;
    private String draftContent;
    private String finalContent;
    private String status;
    private String hearingOutcome;
    private Boolean finalHearing;
    private Long nextHearingId;
    private java.time.LocalDate nextHearingDate;
    private String caseStatus;
    private String message;
    private String digitalSignatureRef;
    private Instant updatedAt;
    private String updatedByLoginId;
    private boolean attendanceRequired;
    private boolean attendanceComplete;
    private List<HearingAttendanceItemResponse> attendance = new ArrayList<>();
    /** Per-hearing roznamma rows: prior hearings read-only, current hearing editable. */
    private List<RoznamaTableEntryResponse> tableRows = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getHearingId() {
        return hearingId;
    }

    public void setHearingId(Long hearingId) {
        this.hearingId = hearingId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHearingOutcome() {
        return hearingOutcome;
    }

    public void setHearingOutcome(String hearingOutcome) {
        this.hearingOutcome = hearingOutcome;
    }

    public Boolean getFinalHearing() {
        return finalHearing;
    }

    public void setFinalHearing(Boolean finalHearing) {
        this.finalHearing = finalHearing;
    }

    public Long getNextHearingId() {
        return nextHearingId;
    }

    public void setNextHearingId(Long nextHearingId) {
        this.nextHearingId = nextHearingId;
    }

    public java.time.LocalDate getNextHearingDate() {
        return nextHearingDate;
    }

    public void setNextHearingDate(java.time.LocalDate nextHearingDate) {
        this.nextHearingDate = nextHearingDate;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDigitalSignatureRef() {
        return digitalSignatureRef;
    }

    public void setDigitalSignatureRef(String digitalSignatureRef) {
        this.digitalSignatureRef = digitalSignatureRef;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedByLoginId() {
        return updatedByLoginId;
    }

    public void setUpdatedByLoginId(String updatedByLoginId) {
        this.updatedByLoginId = updatedByLoginId;
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

    public List<HearingAttendanceItemResponse> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<HearingAttendanceItemResponse> attendance) {
        this.attendance = attendance != null ? attendance : new ArrayList<>();
    }

    public List<RoznamaTableEntryResponse> getTableRows() {
        return tableRows;
    }

    public void setTableRows(List<RoznamaTableEntryResponse> tableRows) {
        this.tableRows = tableRows != null ? tableRows : new ArrayList<>();
    }
}
