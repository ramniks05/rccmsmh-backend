package com.maharashtra.rccms.dto;

public class VillageCreateRequest extends BoundaryMasterCreateRequest {
    private Long talukaId;
    /** Parent taluka LGD code; if omitted, copied from the taluka master row. */
    private String talukaLgdCode;

    public Long getTalukaId() {
        return talukaId;
    }

    public void setTalukaId(Long talukaId) {
        this.talukaId = talukaId;
    }

    public String getTalukaLgdCode() {
        return talukaLgdCode;
    }

    public void setTalukaLgdCode(String talukaLgdCode) {
        this.talukaLgdCode = talukaLgdCode;
    }
}

