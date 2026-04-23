package com.maharashtra.rccms.dto;

public class OfficeResponse {
    private final Long id;

    private final Long departmentId;
    private final String departmentName;
    private final String departmentLocalName;

    private final Long officeTypeId;
    private final String officeTypeName;
    private final String officeTypeLocalName;

    private final String level;
    private final Long locationId;

    private final String name;
    private final String localName;
    private final String shortName;
    private final String shortNameLocal;

    public OfficeResponse(
            Long id,
            Long departmentId,
            String departmentName,
            String departmentLocalName,
            Long officeTypeId,
            String officeTypeName,
            String officeTypeLocalName,
            String level,
            Long locationId,
            String name,
            String localName,
            String shortName,
            String shortNameLocal
    ) {
        this.id = id;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentLocalName = departmentLocalName;
        this.officeTypeId = officeTypeId;
        this.officeTypeName = officeTypeName;
        this.officeTypeLocalName = officeTypeLocalName;
        this.level = level;
        this.locationId = locationId;
        this.name = name;
        this.localName = localName;
        this.shortName = shortName;
        this.shortNameLocal = shortNameLocal;
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

    public Long getOfficeTypeId() {
        return officeTypeId;
    }

    public String getOfficeTypeName() {
        return officeTypeName;
    }

    public String getOfficeTypeLocalName() {
        return officeTypeLocalName;
    }

    public String getLevel() {
        return level;
    }

    public Long getLocationId() {
        return locationId;
    }

    public String getName() {
        return name;
    }

    public String getLocalName() {
        return localName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getShortNameLocal() {
        return shortNameLocal;
    }
}

