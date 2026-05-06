package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
}
