package com.maharashtra.rccms.dto;

public class DocumentTypeUpdateRequest {
    private String code;
    private String name;
    private String localName;
    private Boolean validForPhotoId;
    private Boolean validForAddress;
    private String sourceUrl;

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

    public Boolean getValidForPhotoId() {
        return validForPhotoId;
    }

    public void setValidForPhotoId(Boolean validForPhotoId) {
        this.validForPhotoId = validForPhotoId;
    }

    public Boolean getValidForAddress() {
        return validForAddress;
    }

    public void setValidForAddress(Boolean validForAddress) {
        this.validForAddress = validForAddress;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
