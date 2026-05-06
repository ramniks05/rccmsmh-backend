package com.maharashtra.rccms.dto.filing;

public class ApplicationActionResponse {
    private Long applicationId;
    private String processingStage;
    private String currentAssigneeRole;
    private String message;

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getProcessingStage() {
        return processingStage;
    }

    public void setProcessingStage(String processingStage) {
        this.processingStage = processingStage;
    }

    public String getCurrentAssigneeRole() {
        return currentAssigneeRole;
    }

    public void setCurrentAssigneeRole(String currentAssigneeRole) {
        this.currentAssigneeRole = currentAssigneeRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
