package com.maharashtra.rccms.dto;

public class CaseCategoryCreateRequest {
    private String code;
    private String name;
    private String localName;
    private Integer sequenceNo;
    private Long hearingOfficeTypeId;
    private Long nextCaseCategoryId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Integer getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public Long getHearingOfficeTypeId() {
        return hearingOfficeTypeId;
    }

    public void setHearingOfficeTypeId(Long hearingOfficeTypeId) {
        this.hearingOfficeTypeId = hearingOfficeTypeId;
    }

    public Long getNextCaseCategoryId() {
        return nextCaseCategoryId;
    }

    public void setNextCaseCategoryId(Long nextCaseCategoryId) {
        this.nextCaseCategoryId = nextCaseCategoryId;
    }
}
