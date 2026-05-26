package com.maharashtra.rccms.dto.workflow;

import com.maharashtra.rccms.workflow.config.CaseWorkflowDefinition;

public class CaseWorkflowConfigSaveRequest {

    private Long caseCategoryId;
    private CaseWorkflowDefinition definition;

    public Long getCaseCategoryId() {
        return caseCategoryId;
    }

    public void setCaseCategoryId(Long caseCategoryId) {
        this.caseCategoryId = caseCategoryId;
    }

    public CaseWorkflowDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(CaseWorkflowDefinition definition) {
        this.definition = definition;
    }
}
