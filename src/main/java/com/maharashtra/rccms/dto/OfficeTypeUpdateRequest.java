package com.maharashtra.rccms.dto;

public class OfficeTypeUpdateRequest {
    private Long departmentId;
    /** STATE | DIVISION | DISTRICT | TALUKA | VILLAGE */
    private String boundaryLevel;
    private String name;
    private String localName;
    private String shortName;
    private String shortNameLocal;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getBoundaryLevel() {
        return boundaryLevel;
    }

    public void setBoundaryLevel(String boundaryLevel) {
        this.boundaryLevel = boundaryLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortNameLocal() {
        return shortNameLocal;
    }

    public void setShortNameLocal(String shortNameLocal) {
        this.shortNameLocal = shortNameLocal;
    }
}

