package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HearingAttendanceSaveRequest {
    private List<HearingAttendanceEntryRequest> entries = new ArrayList<>();

    public List<HearingAttendanceEntryRequest> getEntries() {
        return entries;
    }

    public void setEntries(List<HearingAttendanceEntryRequest> entries) {
        this.entries = entries != null ? entries : new ArrayList<>();
    }
}
