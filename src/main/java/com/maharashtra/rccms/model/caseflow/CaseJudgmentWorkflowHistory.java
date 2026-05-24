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
@Table(name = "case_judgment_workflow_history")
public class CaseJudgmentWorkflowHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseRegistry caseRegistry;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_id", nullable = false)
    private CaseJudgmentWorkflow workflow;

    @Column(name = "from_status", length = 32)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 32)
    private String toStatus;

    @Column(name = "action_code", nullable = false, length = 64)
    private String actionCode;

    @Column(name = "summary_snapshot", columnDefinition = "TEXT")
    private String summarySnapshot;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "actor_role", length = 32)
    private String actorRole;

    @Column(name = "actor_login_id", nullable = false, length = 150)
    private String actorLoginId;

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

    public CaseJudgmentWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(CaseJudgmentWorkflow workflow) {
        this.workflow = workflow;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getSummarySnapshot() {
        return summarySnapshot;
    }

    public void setSummarySnapshot(String summarySnapshot) {
        this.summarySnapshot = summarySnapshot;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getActorLoginId() {
        return actorLoginId;
    }

    public void setActorLoginId(String actorLoginId) {
        this.actorLoginId = actorLoginId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
