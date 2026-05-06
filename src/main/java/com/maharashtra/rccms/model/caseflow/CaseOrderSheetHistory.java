package com.maharashtra.rccms.model.caseflow;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "case_order_sheet_history")
public class CaseOrderSheetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseRegistry caseRegistry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hearing_id")
    private CaseHearing caseHearing;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "remarks", length = 2000)
    private String remarks;

    @Column(name = "created_by_login_id", nullable = false, length = 150)
    private String createdByLoginId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
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

    public CaseHearing getCaseHearing() {
        return caseHearing;
    }

    public void setCaseHearing(CaseHearing caseHearing) {
        this.caseHearing = caseHearing;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreatedByLoginId() {
        return createdByLoginId;
    }

    public void setCreatedByLoginId(String createdByLoginId) {
        this.createdByLoginId = createdByLoginId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
