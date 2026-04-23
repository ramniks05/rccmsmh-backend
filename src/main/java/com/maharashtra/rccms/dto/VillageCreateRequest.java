package com.maharashtra.rccms.dto;

public class VillageCreateRequest extends BoundaryMasterCreateRequest {
    private Long talukaId;

    public Long getTalukaId() {
        return talukaId;
    }

    public void setTalukaId(Long talukaId) {
        this.talukaId = talukaId;
    }
}

