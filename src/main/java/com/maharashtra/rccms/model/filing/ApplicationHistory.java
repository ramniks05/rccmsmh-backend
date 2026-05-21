package com.maharashtra.rccms.model.filing;

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
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "application_history")
@SuppressWarnings("null")
public class ApplicationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private FilingApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 48)
    private ApplicationHistoryAction action;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "actor_role", length = 48)
    private String actorRole;

    @Column(name = "actor_login_id", nullable = false, length = 150)
    private String actorLoginId;

    @Column(name = "application_no", length = 64)
    private String applicationNo;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_no", length = 64)
    private String caseNo;

    @Column(name = "processing_stage", length = 64)
    private String processingStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", length = 32)
    private ApplicationStatus applicationStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public FilingApplication getApplication() {
        return application;
    }

    public void setApplication(FilingApplication application) {
        this.application = application;
    }

    public ApplicationHistoryAction getAction() {
        return action;
    }

    public void setAction(ApplicationHistoryAction action) {
        this.action = action;
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

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public String getProcessingStage() {
        return processingStage;
    }

    public void setProcessingStage(String processingStage) {
        this.processingStage = processingStage;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
