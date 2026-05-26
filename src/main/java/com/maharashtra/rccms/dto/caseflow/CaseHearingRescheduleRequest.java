package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseHearingRescheduleRequest {
    private LocalDate nextHearingDate;
    private Boolean noticeGenerate;
    private String remarks;

    public LocalDate getNextHearingDate() {
        return nextHearingDate;
    }

    public void setNextHearingDate(LocalDate nextHearingDate) {
        this.nextHearingDate = nextHearingDate;
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
