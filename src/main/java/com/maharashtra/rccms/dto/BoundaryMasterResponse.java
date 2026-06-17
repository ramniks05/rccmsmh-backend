package com.maharashtra.rccms.dto;

public class BoundaryMasterResponse {
    private final Long id;
    private final String name;
    private final String localName;
    private final String lgdCode;
    /** Present only for state master rows; null for other boundary levels. */
    private final String stateOrUT;
    /** Present only for division master rows; null for other boundary levels. */
    private final String divisionCode;

    private final Long stateId;
    private final Long divisionId;
    private final Long districtId;
    /** Present for subdistrict master rows; null for other boundary levels. */
    private final String districtLgdCode;
    private final Long subdistrictId;
    /** Present for taluka master rows; null for other boundary levels. */
    private final String subdistrictLgdCode;
    private final Long talukaId;
    /** Present for village master rows; null for other boundary levels. */
    private final String talukaLgdCode;

    public BoundaryMasterResponse(
            Long id,
            String name,
            String localName,
            String lgdCode,
            String stateOrUT,
            String divisionCode,
            Long stateId,
            Long divisionId,
            Long districtId,
            String districtLgdCode,
            Long subdistrictId,
            String subdistrictLgdCode,
            Long talukaId,
            String talukaLgdCode
    ) {
        this.id = id;
        this.name = name;
        this.localName = localName;
        this.lgdCode = lgdCode;
        this.stateOrUT = stateOrUT;
        this.divisionCode = divisionCode;
        this.stateId = stateId;
        this.divisionId = divisionId;
        this.districtId = districtId;
        this.districtLgdCode = districtLgdCode;
        this.subdistrictId = subdistrictId;
        this.subdistrictLgdCode = subdistrictLgdCode;
        this.talukaId = talukaId;
        this.talukaLgdCode = talukaLgdCode;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocalName() {
        return localName;
    }

    public String getLgdCode() {
        return lgdCode;
    }

    public String getStateOrUT() {
        return stateOrUT;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public Long getStateId() {
        return stateId;
    }

    public Long getDivisionId() {
        return divisionId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public String getDistrictLgdCode() {
        return districtLgdCode;
    }

    public Long getSubdistrictId() {
        return subdistrictId;
    }

    public String getSubdistrictLgdCode() {
        return subdistrictLgdCode;
    }

    public Long getTalukaId() {
        return talukaId;
    }

    public String getTalukaLgdCode() {
        return talukaLgdCode;
    }
}

