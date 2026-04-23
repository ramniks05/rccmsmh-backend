package com.maharashtra.rccms.dto;

/**
 * Common create payload for boundary masters (state → village).
 * Parent IDs are provided on the specific endpoints where applicable.
 */
public class BoundaryMasterCreateRequest {
    private String name;
    private String localName;
    private String lgdCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLgdCode() {
        return lgdCode;
    }

    public void setLgdCode(String lgdCode) {
        this.lgdCode = lgdCode;
    }
}

