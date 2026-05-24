package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDocumentChecklistEntryRequest {
    private Long documentTypeId;
    /** Clerk confirms uploaded document is acceptable. */
    private Boolean clerkVerified;
    private String clerkRemarks;

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public Boolean getClerkVerified() {
        return clerkVerified;
    }

    public void setClerkVerified(Boolean clerkVerified) {
        this.clerkVerified = clerkVerified;
    }

    public String getClerkRemarks() {
        return clerkRemarks;
    }

    public void setClerkRemarks(String clerkRemarks) {
        this.clerkRemarks = clerkRemarks;
    }
}
