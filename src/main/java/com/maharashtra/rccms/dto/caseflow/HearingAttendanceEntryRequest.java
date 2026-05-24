package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HearingAttendanceEntryRequest {
    /** APPLICANT, RESPONDENT, or OTHER */
    private String partyType;
    /** Required for APPLICANT / RESPONDENT */
    private Long partyRefId;
    /** Required when partyType is OTHER and row is new; omit to update by attendanceId */
    private String otherPartyKey;
    /** Required when partyType is OTHER */
    private String partyName;
    /** true = present (checkbox ticked), false = absent */
    private Boolean present;

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

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }
}
