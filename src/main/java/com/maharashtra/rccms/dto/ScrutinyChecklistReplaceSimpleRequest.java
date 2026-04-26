package com.maharashtra.rccms.dto;

import java.util.List;

public class ScrutinyChecklistReplaceSimpleRequest {
    private Long caseCategoryId;
    private Long subjectId;
    private List<ScrutinyChecklistItemSimpleRequest> items;

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

    public List<ScrutinyChecklistItemSimpleRequest> getItems() {
        return items;
    }

    public void setItems(List<ScrutinyChecklistItemSimpleRequest> items) {
        this.items = items;
    }
}

