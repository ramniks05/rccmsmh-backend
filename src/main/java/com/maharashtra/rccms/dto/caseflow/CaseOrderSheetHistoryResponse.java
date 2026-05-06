package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;
import java.time.LocalDate;

public class CaseOrderSheetHistoryResponse {
    private Long historyId;
    private Long hearingId;
    private Integer hearingNo;
    private LocalDate hearingDate;
    private String content;
    private String remarks;
    private Instant createdAt;
    private String createdByLoginId;

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByLoginId() {
        return createdByLoginId;
    }

    public void setCreatedByLoginId(String createdByLoginId) {
        this.createdByLoginId = createdByLoginId;
    }
}
