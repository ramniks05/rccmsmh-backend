package com.maharashtra.rccms.dto;

public class DistrictCreateRequest extends BoundaryMasterCreateRequest {
    private Long stateId;
    private String divisionCode;

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }
}
