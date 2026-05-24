package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;

public class HearingAttendanceItemResponse {
    private Long attendanceId;
    private String partyType;
    private Long partyRefId;
    /** For OTHER party rows; null for applicants/respondents. */
    private String otherPartyKey;
    private String partyName;
    private Integer lineNo;
  /** true = present, false = absent, null = not yet marked by PO */
    private Boolean present;
    private boolean mandatory;
    private Instant updatedAt;

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getPartyType() {
        return partyType;
    }

    public void setPartyType(String partyType) {
        this.partyType = partyType;
    }

    public Long getPartyRefId() {
        return partyRefId;
    }

    public void setPartyRefId(Long partyRefId) {
        this.partyRefId = partyRefId;
    }

    public String getOtherPartyKey() {
        return otherPartyKey;
    }

    public void setOtherPartyKey(String otherPartyKey) {
        this.otherPartyKey = otherPartyKey;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
