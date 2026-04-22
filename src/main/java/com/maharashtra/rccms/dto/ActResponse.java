package com.maharashtra.rccms.dto;

public class ActResponse {
    private final Long id;
    private final String actCode;
    private final String actName;
    private final String actNameLocal;

    public ActResponse(Long id, String actCode, String actName, String actNameLocal) {
        this.id = id;
        this.actCode = actCode;
        this.actName = actName;
        this.actNameLocal = actNameLocal;
    }

    public Long getId() {
        return id;
    }

    public String getActCode() {
        return actCode;
    }

    public String getActName() {
        return actName;
    }

    public String getActNameLocal() {
        return actNameLocal;
    }
}

