package com.maharashtra.rccms.dto.caseflow;

import java.time.Instant;
import java.time.LocalDate;

public class CaseRoznamaCompleteResponse {
    private Long caseId;
    private Long roznamaId;
    private Long hearingId;
    private String status;
    private String hearingOutcome;
    private Boolean finalHearing;
    private Long nextHearingId;
    private LocalDate nextHearingDate;
    private String caseStatus;
    private String message;
    private boolean roznamaFinalized;
    private boolean roznamaSigned;
    private String digitalSignatureRef;
    private Instant updatedAt;

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getRoznamaId() {
        return roznamaId;
    }

    public void setRoznamaId(Long roznamaId) {
        this.roznamaId = roznamaId;
    }

    public Long getHearingId() {
        return hearingId;
    }

    public void setHearingId(Long hearingId) {
        this.hearingId = hearingId;
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

    public Boolean getFinalHearing() {
        return finalHearing;
    }

    public void setFinalHearing(Boolean finalHearing) {
        this.finalHearing = finalHearing;
    }

    public Long getNextHearingId() {
        return nextHearingId;
    }

    public void setNextHearingId(Long nextHearingId) {
        this.nextHearingId = nextHearingId;
    }

    public LocalDate getNextHearingDate() {
        return nextHearingDate;
    }

    public void setNextHearingDate(LocalDate nextHearingDate) {
        this.nextHearingDate = nextHearingDate;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRoznamaFinalized() {
        return roznamaFinalized;
    }

    public void setRoznamaFinalized(boolean roznamaFinalized) {
        this.roznamaFinalized = roznamaFinalized;
    }

    public boolean isRoznamaSigned() {
        return roznamaSigned;
    }

    public void setRoznamaSigned(boolean roznamaSigned) {
        this.roznamaSigned = roznamaSigned;
    }

    public String getDigitalSignatureRef() {
        return digitalSignatureRef;
    }

    public void setDigitalSignatureRef(String digitalSignatureRef) {
        this.digitalSignatureRef = digitalSignatureRef;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
