package com.maharashtra.rccms.dto;

public class DistrictCreateRequest extends BoundaryMasterCreateRequest {
    private Long stateId;
    private Long divisionId;

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public Long getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Long divisionId) {
        this.divisionId = divisionId;
    }
}

