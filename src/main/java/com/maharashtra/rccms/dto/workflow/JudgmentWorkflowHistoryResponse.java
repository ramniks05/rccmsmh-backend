package com.maharashtra.rccms.dto.workflow;

import java.time.Instant;

public class JudgmentWorkflowHistoryResponse {

    private Long id;
    private String fromStatus;
    private String toStatus;
    private String actionCode;
    private String summarySnapshot;
    private String remarks;
    private String actorRole;
    private String actorLoginId;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
