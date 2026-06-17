package com.maharashtra.rccms.dto;

/**
 * Common create payload for boundary masters with LGD code (state, district → village).
 * Parent IDs are provided on the specific endpoints where applicable.
 */
public class BoundaryMasterCreateRequest extends BoundaryNamedCreateRequest {
    private String lgdCode;

    public String getLgdCode() {
        return lgdCode;
    }

    public void setLgdCode(String lgdCode) {
        this.lgdCode = lgdCode;
    }
}

