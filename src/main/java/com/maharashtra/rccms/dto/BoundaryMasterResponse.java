package com.maharashtra.rccms.dto;

public class BoundaryMasterResponse {
    private final Long id;
    private final String name;
    private final String localName;
    private final String lgdCode;

    private final Long stateId;
    private final Long divisionId;
    private final Long districtId;
    private final Long subdistrictId;
    private final Long talukaId;

    public BoundaryMasterResponse(
            Long id,
            String name,
            String localName,
            String lgdCode,
            Long stateId,
            Long divisionId,
            Long districtId,
            Long subdistrictId,
            Long talukaId
    ) {
        this.id = id;
        this.name = name;
        this.localName = localName;
        this.lgdCode = lgdCode;
        this.stateId = stateId;
        this.divisionId = divisionId;
        this.districtId = districtId;
        this.subdistrictId = subdistrictId;
        this.talukaId = talukaId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocalName() {
        return localName;
    }

    public String getLgdCode() {
        return lgdCode;
    }

    public Long getStateId() {
        return stateId;
    }

    public Long getDivisionId() {
        return divisionId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public Long getSubdistrictId() {
        return subdistrictId;
    }

    public Long getTalukaId() {
        return talukaId;
    }
}

