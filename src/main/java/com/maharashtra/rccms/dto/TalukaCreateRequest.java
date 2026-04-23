package com.maharashtra.rccms.dto;

public class TalukaCreateRequest extends BoundaryMasterCreateRequest {
    private Long districtId;
    private Long subdistrictId;

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getSubdistrictId() {
        return subdistrictId;
    }

    public void setSubdistrictId(Long subdistrictId) {
        this.subdistrictId = subdistrictId;
    }
}

