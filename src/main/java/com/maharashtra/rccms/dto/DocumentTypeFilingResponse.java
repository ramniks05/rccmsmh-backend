package com.maharashtra.rccms.dto;

public class DocumentTypeFilingResponse {
    private final Long id;
    private final String code;
    private final String name;
    private final String localName;
    private final boolean validForPhotoId;
    private final boolean validForAddress;
    private final String sourceUrl;
    private final boolean required;
    private final int displayOrder;

    public DocumentTypeFilingResponse(
            Long id,
            String code,
            String name,
            String localName,
            boolean validForPhotoId,
            boolean validForAddress,
            String sourceUrl,
            boolean required,
            int displayOrder
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.localName = localName;
        this.validForPhotoId = validForPhotoId;
        this.validForAddress = validForAddress;
        this.sourceUrl = sourceUrl;
        this.required = required;
        this.displayOrder = displayOrder;
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

    public boolean isRequired() {
        return required;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
