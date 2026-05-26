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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "case_order_sheet",
        uniqueConstraints = @UniqueConstraint(name = "uk_case_order_sheet_case_id", columnNames = "case_id")
)
public class CaseOrderSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseRegistry caseRegistry;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "draft_content", columnDefinition = "TEXT")
    private String draftContent;

    @Column(name = "final_content", columnDefinition = "TEXT")
    private String finalContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private CaseOrderSheetStatus status = CaseOrderSheetStatus.CLERK_DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_hearing_id")
    private CaseHearing currentHearing;

    @Enumerated(EnumType.STRING)
    @Column(name = "hearing_outcome", length = 16)
    private HearingOutcome hearingOutcome;

    @Column(name = "drafted_by_login_id", length = 150)
    private String draftedByLoginId;

    @Column(name = "po_finalized_by_login_id", length = 150)
    private String poFinalizedByLoginId;

    @Column(name = "po_signed_by_login_id", length = 150)
    private String poSignedByLoginId;

    @Column(name = "digital_signature_ref", length = 512)
    private String digitalSignatureRef;

    @Column(name = "updated_by_login_id", nullable = false, length = 150)
    private String updatedByLoginId;

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

    public CaseRegistry getCaseRegistry() {
        return caseRegistry;
    }

    public void setCaseRegistry(CaseRegistry caseRegistry) {
        this.caseRegistry = caseRegistry;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDraftContent() {
        return draftContent;
    }

    public void setDraftContent(String draftContent) {
        this.draftContent = draftContent;
    }

    public String getFinalContent() {
        return finalContent;
    }

    public void setFinalContent(String finalContent) {
        this.finalContent = finalContent;
    }

    public CaseOrderSheetStatus getStatus() {
        return status;
    }

    public void setStatus(CaseOrderSheetStatus status) {
        this.status = status;
    }

    public CaseHearing getCurrentHearing() {
        return currentHearing;
    }

    public void setCurrentHearing(CaseHearing currentHearing) {
        this.currentHearing = currentHearing;
    }

    public HearingOutcome getHearingOutcome() {
        return hearingOutcome;
    }

    public void setHearingOutcome(HearingOutcome hearingOutcome) {
        this.hearingOutcome = hearingOutcome;
    }

    public String getDraftedByLoginId() {
        return draftedByLoginId;
    }

    public void setDraftedByLoginId(String draftedByLoginId) {
        this.draftedByLoginId = draftedByLoginId;
    }

    public String getPoFinalizedByLoginId() {
        return poFinalizedByLoginId;
    }

    public void setPoFinalizedByLoginId(String poFinalizedByLoginId) {
        this.poFinalizedByLoginId = poFinalizedByLoginId;
    }

    public String getPoSignedByLoginId() {
        return poSignedByLoginId;
    }

    public void setPoSignedByLoginId(String poSignedByLoginId) {
        this.poSignedByLoginId = poSignedByLoginId;
    }

    public String getDigitalSignatureRef() {
        return digitalSignatureRef;
    }

    public void setDigitalSignatureRef(String digitalSignatureRef) {
        this.digitalSignatureRef = digitalSignatureRef;
    }

    public String getUpdatedByLoginId() {
        return updatedByLoginId;
    }

    public void setUpdatedByLoginId(String updatedByLoginId) {
        this.updatedByLoginId = updatedByLoginId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
