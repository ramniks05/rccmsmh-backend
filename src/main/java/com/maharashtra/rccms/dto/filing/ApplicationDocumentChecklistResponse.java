package com.maharashtra.rccms.dto.filing;

import java.util.ArrayList;
import java.util.List;

public class ApplicationDocumentChecklistResponse {
    private Long applicationId;
    private Long caseCategoryId;
    private Long subjectId;
    private boolean documentsConfigured;
    private boolean allRequiredUploaded;
    private boolean allRequiredClerkVerified;
    private List<ApplicationDocumentChecklistItemResponse> items = new ArrayList<>();

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getCaseCategoryId() {
        return caseCategoryId;
    }

    public void setCaseCategoryId(Long caseCategoryId) {
        this.caseCategoryId = caseCategoryId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public boolean isDocumentsConfigured() {
        return documentsConfigured;
    }

    public void setDocumentsConfigured(boolean documentsConfigured) {
        this.documentsConfigured = documentsConfigured;
    }

    public boolean isAllRequiredUploaded() {
        return allRequiredUploaded;
    }

    public void setAllRequiredUploaded(boolean allRequiredUploaded) {
        this.allRequiredUploaded = allRequiredUploaded;
    }

    public boolean isAllRequiredClerkVerified() {
        return allRequiredClerkVerified;
    }

    public void setAllRequiredClerkVerified(boolean allRequiredClerkVerified) {
        this.allRequiredClerkVerified = allRequiredClerkVerified;
    }

    public List<ApplicationDocumentChecklistItemResponse> getItems() {
        return items;
    }

    public void setItems(List<ApplicationDocumentChecklistItemResponse> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}
