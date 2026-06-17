package com.maharashtra.rccms.dto;

public class SubdistrictCreateRequest extends BoundaryMasterCreateRequest {
    private Long districtId;
    /** Parent district LGD code; if omitted, copied from the district master row. */
    private String districtLgdCode;

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public String getDistrictLgdCode() {
        return districtLgdCode;
    }

    public void setDistrictLgdCode(String districtLgdCode) {
        this.districtLgdCode = districtLgdCode;
    }
}

