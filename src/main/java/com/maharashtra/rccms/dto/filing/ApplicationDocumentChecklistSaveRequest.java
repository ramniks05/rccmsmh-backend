package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDocumentChecklistSaveRequest {
    private List<ApplicationDocumentChecklistEntryRequest> entries = new ArrayList<>();

    public List<ApplicationDocumentChecklistEntryRequest> getEntries() {
        return entries;
    }

    public void setEntries(List<ApplicationDocumentChecklistEntryRequest> entries) {
        this.entries = entries != null ? entries : new ArrayList<>();
    }
}
