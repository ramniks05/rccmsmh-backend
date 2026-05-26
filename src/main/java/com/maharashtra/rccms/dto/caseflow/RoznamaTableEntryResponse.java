package com.maharashtra.rccms.dto.caseflow;

import java.time.LocalDate;

/**
 * One row in the per-case roznamma table (one row per hearing date / hearing cycle).
 */
public class RoznamaTableEntryResponse {

    private Integer lineNo;
    private Long hearingId;
    private Integer hearingNo;
    private LocalDate hearingDate;
    /** Same as hearingDate for UI columns labelled "date". */
    private String date;
    private String content;
    private String status;
    private String hearingOutcome;
    private boolean readOnly;

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

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
        this.date = hearingDate != null ? hearingDate.toString() : null;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
