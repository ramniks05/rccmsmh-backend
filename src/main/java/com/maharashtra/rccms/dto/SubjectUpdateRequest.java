package com.maharashtra.rccms.dto;

public class SubjectUpdateRequest {
    private String subjectCode;
    private String subjectName;
    private String subjectNameLocal;

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

    public String getSubjectNameLocal() {
        return subjectNameLocal;
    }

    public void setSubjectNameLocal(String subjectNameLocal) {
        this.subjectNameLocal = subjectNameLocal;
    }
}

