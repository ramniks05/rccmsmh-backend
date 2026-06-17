package com.maharashtra.rccms.dto;

/**
 * Create payload for boundary masters without LGD code (e.g. division).
 */
public class BoundaryNamedCreateRequest {
    private String name;
    private String localName;

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
}
