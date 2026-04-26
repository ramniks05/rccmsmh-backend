package com.maharashtra.rccms.dto;

public class OfficeBranchResponse {
    private final Long id;
    private final Long officeId;
    private final String name;
    private final String localName;
    private final String shortName;
    private final String shortNameLocal;

    public OfficeBranchResponse(
            Long id,
            Long officeId,
            String name,
            String localName,
            String shortName,
            String shortNameLocal
    ) {
        this.id = id;
        this.officeId = officeId;
        this.name = name;
        this.localName = localName;
        this.shortName = shortName;
        this.shortNameLocal = shortNameLocal;
    }

    public Long getId() {
        return id;
    }

    public Long getOfficeId() {
        return officeId;
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

