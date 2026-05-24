package com.maharashtra.rccms.dto;

import java.util.ArrayList;
import java.util.List;

public class DocumentTypeMappingListResponse {
    private Long caseCategoryId;
    private String caseCategoryCode;
    private String caseCategoryName;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private List<DocumentTypeMappingItemResponse> items = new ArrayList<>();

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

    public String getCaseCategoryName() {
        return caseCategoryName;
    }

    public void setCaseCategoryName(String caseCategoryName) {
        this.caseCategoryName = caseCategoryName;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<DocumentTypeMappingItemResponse> getItems() {
        return items;
    }

    public void setItems(List<DocumentTypeMappingItemResponse> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}
