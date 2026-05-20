package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseOrderSheetUpsertRequest {
    private Long hearingId;
    private java.time.LocalDate hearingDate;
    private String content;
    private String remarks;

    public Long getHearingId() {
        return hearingId;
    }

    public void setHearingId(Long hearingId) {
        this.hearingId = hearingId;
    }

    public java.time.LocalDate getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(java.time.LocalDate hearingDate) {
        this.hearingDate = hearingDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
