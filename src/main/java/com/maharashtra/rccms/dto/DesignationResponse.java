package com.maharashtra.rccms.dto;

public class DesignationResponse {
    private final Long id;
    private final Long departmentId;
    private final String departmentName;
    private final String departmentLocalName;
    private final String name;
    private final String localName;
    private final String shortName;
    private final String shortNameLocal;

    public DesignationResponse(
            Long id,
            Long departmentId,
            String departmentName,
            String departmentLocalName,
            String name,
            String localName,
            String shortName,
            String shortNameLocal
    ) {
        this.id = id;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentLocalName = departmentLocalName;
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

