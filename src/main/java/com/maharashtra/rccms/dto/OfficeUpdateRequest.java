package com.maharashtra.rccms.dto;

public class OfficeUpdateRequest {
    private Long departmentId;
    private Long officeTypeId;
    /** Optional; depends on office type boundary level. */
    private Long stateId;
    private Long divisionId;
    private Long districtId;
    private Long talukaId;
    private String name;
    private String officeCode;
    private String localName;
    private String shortName;
    private String shortNameLocal;
    private String officeAddress;
    private String officeAddressLocal;
    private String email;
    private String officeContactNo;
    private String stateLgdCode;
    private String districtLgdCode;
    private String talukaLgdCode;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getOfficeTypeId() {
        return officeTypeId;
    }

    public void setOfficeTypeId(Long officeTypeId) {
        this.officeTypeId = officeTypeId;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public Long getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Long divisionId) {
        this.divisionId = divisionId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getTalukaId() {
        return talukaId;
    }

    public void setTalukaId(Long talukaId) {
        this.talukaId = talukaId;
    }

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

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortNameLocal() {
        return shortNameLocal;
    }

    public void setShortNameLocal(String shortNameLocal) {
        this.shortNameLocal = shortNameLocal;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getOfficeAddressLocal() {
        return officeAddressLocal;
    }

    public void setOfficeAddressLocal(String officeAddressLocal) {
        this.officeAddressLocal = officeAddressLocal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOfficeContactNo() {
        return officeContactNo;
    }

    public void setOfficeContactNo(String officeContactNo) {
        this.officeContactNo = officeContactNo;
    }

    public String getStateLgdCode() {
        return stateLgdCode;
    }

    public void setStateLgdCode(String stateLgdCode) {
        this.stateLgdCode = stateLgdCode;
    }

    public String getDistrictLgdCode() {
        return districtLgdCode;
    }

    public void setDistrictLgdCode(String districtLgdCode) {
        this.districtLgdCode = districtLgdCode;
    }

    public String getTalukaLgdCode() {
        return talukaLgdCode;
    }

    public void setTalukaLgdCode(String talukaLgdCode) {
        this.talukaLgdCode = talukaLgdCode;
    }
}
