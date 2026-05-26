package com.maharashtra.rccms.model.caseflow;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "case_hearing_attendance",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_case_hearing_attendance_slot",
                columnNames = {"hearing_id", "party_slot_key"}
        )
)
public class CaseHearingAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hearing_id", nullable = false)
    private CaseHearing caseHearing;

  /** Stable key: APPLICANT_{id}, RESPONDENT_{id}, OTHER_{uuid} */
    @Column(name = "party_slot_key", nullable = false, length = 128)
    private String partySlotKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "party_type", nullable = false, length = 16)
    private HearingPartyType partyType;

    /** application_applicant.id or application_respondent.id; null for OTHER. */
    @Column(name = "party_ref_id")
    private Long partyRefId;

    @Column(name = "party_name", nullable = false, length = 512)
    private String partyName;

    @Column(name = "line_no")
    private Integer lineNo;

    @Column(name = "present", nullable = false)
    private Boolean present = false;

    @Column(name = "marked_by_login_id", nullable = false, length = 150)
    private String markedByLoginId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public CaseHearing getCaseHearing() {
        return caseHearing;
    }

    public void setCaseHearing(CaseHearing caseHearing) {
        this.caseHearing = caseHearing;
    }

    public String getPartySlotKey() {
        return partySlotKey;
    }

    public void setPartySlotKey(String partySlotKey) {
        this.partySlotKey = partySlotKey;
    }

    public HearingPartyType getPartyType() {
        return partyType;
    }

    public void setPartyType(HearingPartyType partyType) {
        this.partyType = partyType;
    }

    public Long getPartyRefId() {
        return partyRefId;
    }

    public void setPartyRefId(Long partyRefId) {
        this.partyRefId = partyRefId;
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

    public String getMarkedByLoginId() {
        return markedByLoginId;
    }

    public void setMarkedByLoginId(String markedByLoginId) {
        this.markedByLoginId = markedByLoginId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
