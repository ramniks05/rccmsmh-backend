package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisputedLandPayload {

    private Integer lineNo;
    private String landType;
    private String externalSource;
    /** Rural + shared */
    private String districtCode;
    private String districtName;
    private String talukaCode;
    private String talukaName;
    private String villageLgdCode;
    private String villageName;
    private String surveyPin;
    private String pin1;
    private String pin2;
    private String pin3;
    private String pin4;
    private String pin5;
    private String pin6;
    private String pin7;
    private String pin8;
    /** Urban */
    private String officeCode;
    private String officeName;
    private String villageCode;
    private String ctsNo;
    private String parentCtsNo;
    private String subCtsNo;
    @JsonAlias({"total_area", "Total_Area", "total_area_ha"})
    private String totalArea;
    @JsonAlias({"disputed_area", "Disputed_Area", "disputed_area_ha"})
    private String disputedArea;
    @JsonAlias({"area_unit", "Area_Unit"})
    private String areaUnit;
    @JsonAlias({"land_holders_text", "land_holders", "landHolders", "holderNames"})
    private String landHoldersText;
    @JsonAlias({"landDetail", "land_detail", "landRecord", "land_record"})
    private Map<String, Object> landDetail;

    @JsonIgnore
    private final Map<String, Object> extraFields = new LinkedHashMap<>();

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public String getLandType() {
        return landType;
    }

    public void setLandType(String landType) {
        this.landType = landType;
    }

    public String getExternalSource() {
        return externalSource;
    }

    public void setExternalSource(String externalSource) {
        this.externalSource = externalSource;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getTalukaCode() {
        return talukaCode;
    }

    public void setTalukaCode(String talukaCode) {
        this.talukaCode = talukaCode;
    }

    public String getTalukaName() {
        return talukaName;
    }

    public void setTalukaName(String talukaName) {
        this.talukaName = talukaName;
    }

    public String getVillageLgdCode() {
        return villageLgdCode;
    }

    public void setVillageLgdCode(String villageLgdCode) {
        this.villageLgdCode = villageLgdCode;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getSurveyPin() {
        return surveyPin;
    }

    public void setSurveyPin(String surveyPin) {
        this.surveyPin = surveyPin;
    }

    public String getPin1() {
        return pin1;
    }

    public void setPin1(String pin1) {
        this.pin1 = pin1;
    }

    public String getPin2() {
        return pin2;
    }

    public void setPin2(String pin2) {
        this.pin2 = pin2;
    }

    public String getPin3() {
        return pin3;
    }

    public void setPin3(String pin3) {
        this.pin3 = pin3;
    }

    public String getPin4() {
        return pin4;
    }

    public void setPin4(String pin4) {
        this.pin4 = pin4;
    }

    public String getPin5() {
        return pin5;
    }

    public void setPin5(String pin5) {
        this.pin5 = pin5;
    }

    public String getPin6() {
        return pin6;
    }

    public void setPin6(String pin6) {
        this.pin6 = pin6;
    }

    public String getPin7() {
        return pin7;
    }

    public void setPin7(String pin7) {
        this.pin7 = pin7;
    }

    public String getPin8() {
        return pin8;
    }

    public void setPin8(String pin8) {
        this.pin8 = pin8;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }

    public String getCtsNo() {
        return ctsNo;
    }

    public void setCtsNo(String ctsNo) {
        this.ctsNo = ctsNo;
    }

    public String getParentCtsNo() {
        return parentCtsNo;
    }

    public void setParentCtsNo(String parentCtsNo) {
        this.parentCtsNo = parentCtsNo;
    }

    public String getSubCtsNo() {
        return subCtsNo;
    }

    public void setSubCtsNo(String subCtsNo) {
        this.subCtsNo = subCtsNo;
    }

    public String getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(String totalArea) {
        this.totalArea = totalArea;
    }

    public String getDisputedArea() {
        return disputedArea;
    }

    public void setDisputedArea(String disputedArea) {
        this.disputedArea = disputedArea;
    }

    public String getAreaUnit() {
        return areaUnit;
    }

    public void setAreaUnit(String areaUnit) {
        this.areaUnit = areaUnit;
    }

    public String getLandHoldersText() {
        return landHoldersText;
    }

    public void setLandHoldersText(String landHoldersText) {
        this.landHoldersText = landHoldersText;
    }

    public Map<String, Object> getLandDetail() {
        return landDetail;
    }

    public void setLandDetail(Map<String, Object> landDetail) {
        this.landDetail = landDetail;
    }

    @JsonIgnore
    public Map<String, Object> getExtraFields() {
        return extraFields;
    }

    @JsonAnySetter
    public void putExtraField(String name, Object value) {
        if (value == null || isDeclaredProperty(name)) {
            return;
        }
        extraFields.put(name, value);
    }

    private static boolean isDeclaredProperty(String name) {
        return switch (name) {
            case "lineNo", "landType", "externalSource", "districtCode", "districtName",
                    "talukaCode", "talukaName", "villageLgdCode", "villageName", "surveyPin",
                    "pin1", "pin2", "pin3", "pin4", "pin5", "pin6", "pin7", "pin8",
                    "officeCode", "officeName", "villageCode", "ctsNo", "parentCtsNo", "subCtsNo",
                    "totalArea", "total_area", "disputedArea", "disputed_area", "areaUnit", "area_unit",
                    "landHoldersText", "land_detail", "landDetail" -> true;
            default -> false;
        };
    }
}
