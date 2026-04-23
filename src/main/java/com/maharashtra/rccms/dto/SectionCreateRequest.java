package com.maharashtra.rccms.dto;

public class SectionCreateRequest {
    private Long actId;
    private String sectionCode;
    private String sectionName;
    private String sectionNameLocal;

    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionNameLocal() {
        return sectionNameLocal;
    }

    public void setSectionNameLocal(String sectionNameLocal) {
        this.sectionNameLocal = sectionNameLocal;
    }
}

