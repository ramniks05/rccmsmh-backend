package com.maharashtra.rccms.dto.workflow;

import java.util.ArrayList;
import java.util.List;

public class WorkflowArtifactContextResponse {

    private String artifact;
    private Long artifactId;
    private Long hearingId;
    private Integer hearingNo;
    private String status;
    /** ADJOURN or FINAL — set when roznamma is signed. */
    private String hearingOutcome;
    private boolean noticeServed;
    private List<String> allowedActions = new ArrayList<>();
    private Object config;

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public Long getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(Long artifactId) {
        this.artifactId = artifactId;
    }

    public Long getHearingId() {
        return hearingId;
    }

    public void setHearingId(Long hearingId) {
        this.hearingId = hearingId;
    }

    public Integer getHearingNo() {
        return hearingNo;
    }

    public void setHearingNo(Integer hearingNo) {
        this.hearingNo = hearingNo;
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

    public boolean isNoticeServed() {
        return noticeServed;
    }

    public void setNoticeServed(boolean noticeServed) {
        this.noticeServed = noticeServed;
    }

    public List<String> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<String> allowedActions) {
        this.allowedActions = allowedActions;
    }

    public Object getConfig() {
        return config;
    }

    public void setConfig(Object config) {
        this.config = config;
    }
}
