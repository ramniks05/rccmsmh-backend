package com.maharashtra.rccms.dto;

public class StateCreateRequest extends BoundaryMasterCreateRequest {
    /** Whether this row is a State or Union Territory (e.g. "State", "UT"). */
    private String stateOrUT;

    public String getStateOrUT() {
        return stateOrUT;
    }

    public void setStateOrUT(String stateOrUT) {
        this.stateOrUT = stateOrUT;
    }
}
