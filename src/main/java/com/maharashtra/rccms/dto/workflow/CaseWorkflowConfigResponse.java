package com.maharashtra.rccms.dto.workflow;

import com.maharashtra.rccms.workflow.config.CaseWorkflowDefinition;

public class CaseWorkflowConfigResponse {

    private Long id;
    private Long caseCategoryId;
    private String caseCategoryCode;
    private String blueprintCode;
    private boolean active;
    private CaseWorkflowDefinition definition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCaseCategoryId() {
        return caseCategoryId;
    }

    public void setCaseCategoryId(Long caseCategoryId) {
        this.caseCategoryId = caseCategoryId;
    }

    public String getCaseCategoryCode() {
        return caseCategoryCode;
    }

    public void setCaseCategoryCode(String caseCategoryCode) {
        this.caseCategoryCode = caseCategoryCode;
    }

    public String getBlueprintCode() {
        return blueprintCode;
    }

    public void setBlueprintCode(String blueprintCode) {
        this.blueprintCode = blueprintCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CaseWorkflowDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(CaseWorkflowDefinition definition) {
        this.definition = definition;
    }
}
