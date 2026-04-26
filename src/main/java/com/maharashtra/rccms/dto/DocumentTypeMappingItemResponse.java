package com.maharashtra.rccms.dto;

public class DocumentTypeMappingItemResponse {
    private final Long documentTypeId;
    private final boolean required;
    private final int displayOrder;
    private final DocumentTypeResponse documentType;

    public DocumentTypeMappingItemResponse(
            Long documentTypeId,
            boolean required,
            int displayOrder,
            DocumentTypeResponse documentType
    ) {
        this.documentTypeId = documentTypeId;
        this.required = required;
        this.displayOrder = displayOrder;
        this.documentType = documentType;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public boolean isRequired() {
        return required;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public DocumentTypeResponse getDocumentType() {
        return documentType;
    }
}

