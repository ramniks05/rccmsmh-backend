package com.maharashtra.rccms.dto;

public class CaseCategoryResponse {
    private final Long id;
    private final String code;
    private final String name;
    private final String localName;
    private final Integer sequenceNo;

    private final Long hearingOfficeTypeId;
    private final String hearingOfficeTypeName;
    private final String hearingOfficeTypeLocalName;

    private final Long nextCaseCategoryId;
    private final String nextCaseCategoryCode;
    private final String nextCaseCategoryName;

    public CaseCategoryResponse(
            Long id,
            String code,
            String name,
            String localName,
            Integer sequenceNo,
            Long hearingOfficeTypeId,
            String hearingOfficeTypeName,
            String hearingOfficeTypeLocalName,
            Long nextCaseCategoryId,
            String nextCaseCategoryCode,
            String nextCaseCategoryName
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.localName = localName;
        this.sequenceNo = sequenceNo;
        this.hearingOfficeTypeId = hearingOfficeTypeId;
        this.hearingOfficeTypeName = hearingOfficeTypeName;
        this.hearingOfficeTypeLocalName = hearingOfficeTypeLocalName;
        this.nextCaseCategoryId = nextCaseCategoryId;
        this.nextCaseCategoryCode = nextCaseCategoryCode;
        this.nextCaseCategoryName = nextCaseCategoryName;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getLocalName() {
        return localName;
    }

    public Integer getSequenceNo() {
        return sequenceNo;
    }

    public Long getHearingOfficeTypeId() {
        return hearingOfficeTypeId;
    }

    public String getHearingOfficeTypeName() {
        return hearingOfficeTypeName;
    }

    public String getHearingOfficeTypeLocalName() {
        return hearingOfficeTypeLocalName;
    }

    public Long getNextCaseCategoryId() {
        return nextCaseCategoryId;
    }

    public String getNextCaseCategoryCode() {
        return nextCaseCategoryCode;
    }

    public String getNextCaseCategoryName() {
        return nextCaseCategoryName;
    }
}
