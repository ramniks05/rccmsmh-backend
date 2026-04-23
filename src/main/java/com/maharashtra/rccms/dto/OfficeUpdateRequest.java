package com.maharashtra.rccms.dto;

public class OfficeUpdateRequest {
    private Long departmentId;
    private Long officeTypeId;
    private String level;
    private Long locationId;
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

    public Long getOfficeTypeId() {
        return officeTypeId;
    }

    public void setOfficeTypeId(Long officeTypeId) {
        this.officeTypeId = officeTypeId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
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

