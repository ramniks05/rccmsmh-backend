package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseRoznamaCompleteRequest {
    private Long hearingId;
    private LocalDate hearingDate;
    private String content;
    /** Required: ADJOURN or FINAL */
    private String hearingOutcome;
    /** Required when hearingOutcome is ADJOURN (may be omitted and set later via reschedule API). */
    private LocalDate nextHearingDate;
    /** Optional; backend generates one when omitted. */
    private String digitalSignatureRef;
    private String remarks;
    /** Optional: save attendance in same request before signing roznamma. */
    private List<HearingAttendanceEntryRequest> attendance = new ArrayList<>();

    public Long getHearingId() {
        return hearingId;
    }

    public void setHearingId(Long hearingId) {
        this.hearingId = hearingId;
    }

    public LocalDate getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(LocalDate hearingDate) {
        this.hearingDate = hearingDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHearingOutcome() {
        return hearingOutcome;
    }

    public void setHearingOutcome(String hearingOutcome) {
        this.hearingOutcome = hearingOutcome;
    }

    public LocalDate getNextHearingDate() {
        return nextHearingDate;
    }

    public void setNextHearingDate(LocalDate nextHearingDate) {
        this.nextHearingDate = nextHearingDate;
    }

    public String getDigitalSignatureRef() {
        return digitalSignatureRef;
    }

    public void setDigitalSignatureRef(String digitalSignatureRef) {
        this.digitalSignatureRef = digitalSignatureRef;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<HearingAttendanceEntryRequest> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<HearingAttendanceEntryRequest> attendance) {
        this.attendance = attendance != null ? attendance : new ArrayList<>();
    }
}
