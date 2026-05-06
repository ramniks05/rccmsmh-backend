package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseHearingScheduleRequest {
    private LocalDate hearingDate;
    private Boolean noticeGenerate;
    private String remarks;

    public LocalDate getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(LocalDate hearingDate) {
        this.hearingDate = hearingDate;
    }

    public Boolean getNoticeGenerate() {
        return noticeGenerate;
    }

    public void setNoticeGenerate(Boolean noticeGenerate) {
        this.noticeGenerate = noticeGenerate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
