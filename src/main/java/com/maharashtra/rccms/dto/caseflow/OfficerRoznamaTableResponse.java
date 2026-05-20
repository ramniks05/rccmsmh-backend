package com.maharashtra.rccms.dto.caseflow;

import java.time.LocalDate;
import java.util.List;

public class OfficerRoznamaTableResponse {
    private LocalDate hearingDate;
    private int totalRows;
    private List<OfficerCauseListItemResponse> rows;

    public LocalDate getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(LocalDate hearingDate) {
        this.hearingDate = hearingDate;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public List<OfficerCauseListItemResponse> getRows() {
        return rows;
    }

    public void setRows(List<OfficerCauseListItemResponse> rows) {
        this.rows = rows;
    }
}
