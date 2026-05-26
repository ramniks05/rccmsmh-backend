package com.maharashtra.rccms.dto.caseflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseOrderSheetSignRequest {
    private String digitalSignatureRef;
    /** Required on roznamma sign: ADJOURN or FINAL (mutually exclusive). */
    private String hearingOutcome;
    /** Required when hearingOutcome is ADJOURN (may be set here or via reschedule API after sign). */
    private java.time.LocalDate nextHearingDate;
    private String remarks;

    public String getDigitalSignatureRef() {
        return digitalSignatureRef;
    }

    public void setDigitalSignatureRef(String digitalSignatureRef) {
        this.digitalSignatureRef = digitalSignatureRef;
    }

    public String getHearingOutcome() {
        return hearingOutcome;
    }

    public void setHearingOutcome(String hearingOutcome) {
        this.hearingOutcome = hearingOutcome;
    }

    public java.time.LocalDate getNextHearingDate() {
        return nextHearingDate;
    }

    public void setNextHearingDate(java.time.LocalDate nextHearingDate) {
        this.nextHearingDate = nextHearingDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
