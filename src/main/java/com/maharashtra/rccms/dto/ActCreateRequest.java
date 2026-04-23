package com.maharashtra.rccms.dto;

public class ActCreateRequest {
    private String actCode;
    private String actName;
    private String actNameLocal;

    public String getActCode() {
        return actCode;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActNameLocal() {
        return actNameLocal;
    }

    public void setActNameLocal(String actNameLocal) {
        this.actNameLocal = actNameLocal;
    }
}

