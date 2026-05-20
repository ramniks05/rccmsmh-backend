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

import java.time.Instant;

@Entity
@Table(name = "case_notice")
public class CaseNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseRegistry caseRegistry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hearing_id")
    private CaseHearing hearing;

    @Column(name = "notice_type", nullable = false, length = 64)
    private String noticeType;

    @Column(name = "selected_parties_json", columnDefinition = "TEXT")
    private String selectedPartiesJson;

    @Column(name = "draft_content", nullable = false, columnDefinition = "TEXT")
    private String draftContent;

    @Column(name = "final_content", columnDefinition = "TEXT")
    private String finalContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private CaseNoticeStatus status = CaseNoticeStatus.CLERK_DRAFT;

    @Column(name = "clerk_drafted_by_login_id", nullable = false, length = 150)
    private String clerkDraftedByLoginId;

    @Column(name = "po_finalized_by_login_id", length = 150)
    private String poFinalizedByLoginId;

    @Column(name = "po_signed_by_login_id", length = 150)
    private String poSignedByLoginId;

    @Column(name = "digital_signature_ref", length = 512)
    private String digitalSignatureRef;

    @Column(name = "served_at")
    private Instant servedAt;

    @Column(name = "served_by_login_id", length = 150)
    private String servedByLoginId;

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

    public CaseHearing getHearing() {
        return hearing;
    }

    public void setHearing(CaseHearing hearing) {
        this.hearing = hearing;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getSelectedPartiesJson() {
        return selectedPartiesJson;
    }

    public void setSelectedPartiesJson(String selectedPartiesJson) {
        this.selectedPartiesJson = selectedPartiesJson;
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

    public CaseNoticeStatus getStatus() {
        return status;
    }

    public void setStatus(CaseNoticeStatus status) {
        this.status = status;
    }

    public String getClerkDraftedByLoginId() {
        return clerkDraftedByLoginId;
    }

    public void setClerkDraftedByLoginId(String clerkDraftedByLoginId) {
        this.clerkDraftedByLoginId = clerkDraftedByLoginId;
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

    public Instant getServedAt() {
        return servedAt;
    }

    public void setServedAt(Instant servedAt) {
        this.servedAt = servedAt;
    }

    public String getServedByLoginId() {
        return servedByLoginId;
    }

    public void setServedByLoginId(String servedByLoginId) {
        this.servedByLoginId = servedByLoginId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
