package com.maharashtra.rccms.dto;

public class DocumentTypeMappingConfiguredSubjectResponse {
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private int mappedDocumentCount;
    private int requiredDocumentCount;

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

    public int getMappedDocumentCount() {
        return mappedDocumentCount;
    }

    public void setMappedDocumentCount(int mappedDocumentCount) {
        this.mappedDocumentCount = mappedDocumentCount;
    }

    public int getRequiredDocumentCount() {
        return requiredDocumentCount;
    }

    public void setRequiredDocumentCount(int requiredDocumentCount) {
        this.requiredDocumentCount = requiredDocumentCount;
    }
}
