package com.maharashtra.rccms.dto;

public class OccupationResponse {
    private final Long id;
    private final String name;
    private final String localName;
    private final String shortName;
    private final String shortNameLocal;

    public OccupationResponse(Long id, String name, String localName, String shortName, String shortNameLocal) {
        this.id = id;
        this.name = name;
        this.localName = localName;
        this.shortName = shortName;
        this.shortNameLocal = shortNameLocal;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocalName() {
        return localName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getShortNameLocal() {
        return shortNameLocal;
    }
}
