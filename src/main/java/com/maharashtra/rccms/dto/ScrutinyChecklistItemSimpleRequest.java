package com.maharashtra.rccms.dto;

public class ScrutinyChecklistItemSimpleRequest {
    private String questionText;
    private String questionTextLocal;
    private Integer displayOrder;
    private Boolean active;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionTextLocal() {
        return questionTextLocal;
    }

    public void setQuestionTextLocal(String questionTextLocal) {
        this.questionTextLocal = questionTextLocal;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

