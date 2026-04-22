package com.maharashtra.rccms.dto;

public class SubdistrictCreateRequest extends BoundaryMasterCreateRequest {
    private Long districtId;

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }
}

