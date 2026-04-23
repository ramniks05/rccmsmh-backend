package com.maharashtra.rccms.dto;

public class DivisionCreateRequest extends BoundaryMasterCreateRequest {
    private Long stateId;

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }
}

