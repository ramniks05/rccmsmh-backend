package com.maharashtra.rccms.dto;

public class SubjectResponse {
    private final Long id;
    private final Long departmentId;
    private final String departmentName;
    private final String departmentLocalName;
    private final String subjectCode;
    private final String subjectName;
    private final String subjectNameLocal;

    public SubjectResponse(
            Long id,
            Long departmentId,
            String departmentName,
            String departmentLocalName,
            String subjectCode,
            String subjectName,
            String subjectNameLocal
    ) {
        this.id = id;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentLocalName = departmentLocalName;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.subjectNameLocal = subjectNameLocal;
    }

    public Long getId() {
        return id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getDepartmentLocalName() {
        return departmentLocalName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectNameLocal() {
        return subjectNameLocal;
    }
}

