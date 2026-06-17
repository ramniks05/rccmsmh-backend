package com.maharashtra.rccms.dto;

public class DepartmentResponse {
    private final Long id;
    private final String name;
    private final String localName;
    private final Long stateId;

    public DepartmentResponse(Long id, String name, String localName, Long stateId) {
        this.id = id;
        this.name = name;
        this.localName = localName;
        this.stateId = stateId;
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

    public Long getStateId() {
        return stateId;
    }
}
