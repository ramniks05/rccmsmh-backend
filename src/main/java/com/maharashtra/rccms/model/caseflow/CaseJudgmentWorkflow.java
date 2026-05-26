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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "case_judgment_workflow",
        uniqueConstraints = @UniqueConstraint(name = "uk_case_judgment_workflow_case_id", columnNames = "case_id")
)
public class CaseJudgmentWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseRegistry caseRegistry;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private CaseJudgmentWorkflowStatus status = CaseJudgmentWorkflowStatus.CLERK_DRAFT;

    @Column(name = "draft_summary", columnDefinition = "TEXT")
    private String draftSummary;

    @Column(name = "final_summary", columnDefinition = "TEXT")
    private String finalSummary;

    @Column(name = "published_summary", columnDefinition = "TEXT")
    private String publishedSummary;

    @Column(name = "drafted_by_login_id", length = 150)
    private String draftedByLoginId;

    @Column(name = "finalized_by_login_id", length = 150)
    private String finalizedByLoginId;

    @Column(name = "published_by_login_id", length = 150)
    private String publishedByLoginId;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "digital_signature_ref", length = 255)
    private String digitalSignatureRef;

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

    public CaseJudgmentWorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(CaseJudgmentWorkflowStatus status) {
        this.status = status;
    }

    public String getDraftSummary() {
        return draftSummary;
    }

    public void setDraftSummary(String draftSummary) {
        this.draftSummary = draftSummary;
    }

    public String getFinalSummary() {
        return finalSummary;
    }

    public void setFinalSummary(String finalSummary) {
        this.finalSummary = finalSummary;
    }

    public String getPublishedSummary() {
        return publishedSummary;
    }

    public void setPublishedSummary(String publishedSummary) {
        this.publishedSummary = publishedSummary;
    }

    public String getDraftedByLoginId() {
        return draftedByLoginId;
    }

    public void setDraftedByLoginId(String draftedByLoginId) {
        this.draftedByLoginId = draftedByLoginId;
    }

    public String getFinalizedByLoginId() {
        return finalizedByLoginId;
    }

    public void setFinalizedByLoginId(String finalizedByLoginId) {
        this.finalizedByLoginId = finalizedByLoginId;
    }

    public String getPublishedByLoginId() {
        return publishedByLoginId;
    }

    public void setPublishedByLoginId(String publishedByLoginId) {
        this.publishedByLoginId = publishedByLoginId;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getDigitalSignatureRef() {
        return digitalSignatureRef;
    }

    public void setDigitalSignatureRef(String digitalSignatureRef) {
        this.digitalSignatureRef = digitalSignatureRef;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
