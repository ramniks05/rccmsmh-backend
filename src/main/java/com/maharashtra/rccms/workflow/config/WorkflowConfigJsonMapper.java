package com.maharashtra.rccms.workflow.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class WorkflowConfigJsonMapper {

    private final ObjectMapper objectMapper;

    public WorkflowConfigJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(CaseWorkflowDefinition definition) {
        try {
            return objectMapper.writeValueAsString(definition);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid workflow configuration.", ex);
        }
    }

    public CaseWorkflowDefinition fromJson(String json) {
        if (json == null || json.isBlank()) {
            return CaseWorkflowDefinition.suitDefault();
        }
        try {
            return objectMapper.readValue(json, CaseWorkflowDefinition.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Workflow configuration JSON is invalid.", ex);
        }
    }
}
