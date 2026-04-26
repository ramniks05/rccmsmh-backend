package com.maharashtra.rccms.dto;

import java.util.List;

/**
 * Replace (overwrite) mapping set for a given CaseCategory + Subject.
 */
public class DocumentTypeMappingReplaceRequest {
    private Long caseCategoryId;
    private Long subjectId;
    private List<DocumentTypeMappingItemRequest> items;

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

    public List<DocumentTypeMappingItemRequest> getItems() {
        return items;
    }

    public void setItems(List<DocumentTypeMappingItemRequest> items) {
        this.items = items;
    }
}

