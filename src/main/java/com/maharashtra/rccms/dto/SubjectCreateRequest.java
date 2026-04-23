package com.maharashtra.rccms.dto;

public class SubjectCreateRequest {
    private Long departmentId;
    private String subjectCode;
    private String subjectName;
    private String subjectNameLocal;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
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

    public String getSubjectNameLocal() {
        return subjectNameLocal;
    }

    public void setSubjectNameLocal(String subjectNameLocal) {
        this.subjectNameLocal = subjectNameLocal;
    }
}

