package com.maharashtra.rccms.dto.workflow;

import java.util.ArrayList;
import java.util.List;

public class WorkflowPhaseContextResponse {

    private String phase;
    private String status;
    private List<String> allowedActions = new ArrayList<>();
    private Object config;

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
