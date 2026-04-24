package com.maharashtra.rccms.dto;

public class DocumentTypeResponse {
    private final Long id;
    private final String code;
    private final String name;
    private final String localName;
    private final boolean validForPhotoId;
    private final boolean validForAddress;
    private final String sourceUrl;

    public DocumentTypeResponse(
            Long id,
            String code,
            String name,
            String localName,
            boolean validForPhotoId,
            boolean validForAddress,
            String sourceUrl
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.localName = localName;
        this.validForPhotoId = validForPhotoId;
        this.validForAddress = validForAddress;
        this.sourceUrl = sourceUrl;
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

    public boolean isValidForPhotoId() {
        return validForPhotoId;
    }

    public boolean isValidForAddress() {
        return validForAddress;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }
}
