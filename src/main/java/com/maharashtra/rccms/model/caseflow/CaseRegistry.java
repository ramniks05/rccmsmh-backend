package com.maharashtra.rccms.model.caseflow;

import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.model.master.Office;
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
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "case_registry",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_case_registry_case_no", columnNames = "case_no"),
                @UniqueConstraint(name = "uk_case_registry_filing_application_id", columnNames = "filing_application_id")
        }
)
public class CaseRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_no", nullable = false, length = 64)
    private String caseNo;

    @Column(name = "filing_application_id", nullable = false)
    private Long filingApplicationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_category_id", nullable = false)
    private CaseCategory caseCategory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @Column(name = "status", nullable = false, length = 32)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "approved_at", nullable = false)
    private Instant approvedAt;

    @Column(name = "approved_by_officer_login_id", nullable = false, length = 150)
    private String approvedByOfficerLoginId;

    @Column(name = "disposed_at")
    private Instant disposedAt;

    @Column(name = "disposed_by_officer_login_id", length = 150)
    private String disposedByOfficerLoginId;

    @Column(name = "judgment_summary", columnDefinition = "TEXT")
    private String judgmentSummary;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.approvedAt == null) {
            this.approvedAt = now;
        }
    }

    public Long getId() {
        return id;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public Long getFilingApplicationId() {
        return filingApplicationId;
    }

    public void setFilingApplicationId(Long filingApplicationId) {
        this.filingApplicationId = filingApplicationId;
    }

    public CaseCategory getCaseCategory() {
        return caseCategory;
    }

    public void setCaseCategory(CaseCategory caseCategory) {
        this.caseCategory = caseCategory;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getApprovedByOfficerLoginId() {
        return approvedByOfficerLoginId;
    }

    public void setApprovedByOfficerLoginId(String approvedByOfficerLoginId) {
        this.approvedByOfficerLoginId = approvedByOfficerLoginId;
    }

    public Instant getDisposedAt() {
        return disposedAt;
    }

    public void setDisposedAt(Instant disposedAt) {
        this.disposedAt = disposedAt;
    }

    public String getDisposedByOfficerLoginId() {
        return disposedByOfficerLoginId;
    }

    public void setDisposedByOfficerLoginId(String disposedByOfficerLoginId) {
        this.disposedByOfficerLoginId = disposedByOfficerLoginId;
    }

    public String getJudgmentSummary() {
        return judgmentSummary;
    }

    public void setJudgmentSummary(String judgmentSummary) {
        this.judgmentSummary = judgmentSummary;
    }
}
