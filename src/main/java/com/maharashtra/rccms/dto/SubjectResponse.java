package com.maharashtra.rccms.dto;

public class SubjectResponse {
    private final Long id;
    private final String subjectCode;
    private final String subjectName;
    private final String subjectNameLocal;

    public SubjectResponse(Long id, String subjectCode, String subjectName, String subjectNameLocal) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.subjectNameLocal = subjectNameLocal;
    }

    public Long getId() {
        return id;
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

