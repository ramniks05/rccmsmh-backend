package com.maharashtra.rccms.model.filing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "application_disputed_land")
@SuppressWarnings("null")
public class ApplicationDisputedLand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private FilingApplication application;

    @Column(name = "line_no")
    private Integer lineNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "land_type", nullable = false, length = 48)
    private DisputedLandType landType;

    @Enumerated(EnumType.STRING)
    @Column(name = "external_source", length = 48)
    private LandRecordsExternalSource externalSource;

    @Column(name = "district_code", length = 64)
    private String districtCode;

    @Column(name = "district_name", length = 512)
    private String districtName;

    @Column(name = "taluka_code", length = 64)
    private String talukaCode;

    @Column(name = "taluka_name", length = 512)
    private String talukaName;

    @Column(name = "village_lgd_code", length = 64)
    private String villageLgdCode;

    @Column(name = "village_name", length = 512)
    private String villageName;

    @Column(name = "survey_pin", length = 128)
    private String surveyPin;

    @Column(name = "pin1", length = 128)
    private String pin1;

    @Column(name = "pin2", length = 128)
    private String pin2;

    @Column(name = "pin3", length = 128)
    private String pin3;

    @Column(name = "pin4", length = 128)
    private String pin4;

    @Column(name = "pin5", length = 128)
    private String pin5;

    @Column(name = "pin6", length = 128)
    private String pin6;

    @Column(name = "pin7", length = 128)
    private String pin7;

    @Column(name = "pin8", length = 128)
    private String pin8;

    @Column(name = "office_code", length = 64)
    private String officeCode;

    @Column(name = "office_name", length = 512)
    private String officeName;

    @Column(name = "village_code", length = 64)
    private String villageCode;

    @Column(name = "cts_no", length = 255)
    private String ctsNo;

    public Long getId() {
        return id;
    }

    public FilingApplication getApplication() {
        return application;
    }

    public void setApplication(FilingApplication application) {
        this.application = application;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public DisputedLandType getLandType() {
        return landType;
    }

    public void setLandType(DisputedLandType landType) {
        this.landType = landType;
    }

    public LandRecordsExternalSource getExternalSource() {
        return externalSource;
    }

    public void setExternalSource(LandRecordsExternalSource externalSource) {
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
