package com.maharashtra.rccms.dto;

public class SectionResponse {
    private final Long id;
    private final Long actId;
    private final String actCode;
    private final String actName;
    private final String actNameLocal;
    private final String sectionCode;
    private final String sectionName;
    private final String sectionNameLocal;

    public SectionResponse(
            Long id,
            Long actId,
            String actCode,
            String actName,
            String actNameLocal,
            String sectionCode,
            String sectionName,
            String sectionNameLocal
    ) {
        this.id = id;
        this.actId = actId;
        this.actCode = actCode;
        this.actName = actName;
        this.actNameLocal = actNameLocal;
        this.sectionCode = sectionCode;
        this.sectionName = sectionName;
        this.sectionNameLocal = sectionNameLocal;
    }

    public Long getId() {
        return id;
    }

    public Long getActId() {
        return actId;
    }

    public String getActCode() {
        return actCode;
    }

    public String getActName() {
        return actName;
    }

    public String getActNameLocal() {
        return actNameLocal;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getSectionNameLocal() {
        return sectionNameLocal;
    }
}

