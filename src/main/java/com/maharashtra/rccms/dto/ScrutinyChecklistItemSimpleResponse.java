package com.maharashtra.rccms.dto;

public class ScrutinyChecklistItemSimpleResponse {
    private final Long id;
    private final Long caseCategoryId;
    private final Long subjectId;
    private final String questionText;
    private final String questionTextLocal;
    private final int displayOrder;
    private final boolean active;

    public ScrutinyChecklistItemSimpleResponse(
            Long id,
            Long caseCategoryId,
            Long subjectId,
            String questionText,
            String questionTextLocal,
            int displayOrder,
            boolean active
    ) {
        this.id = id;
        this.caseCategoryId = caseCategoryId;
        this.subjectId = subjectId;
        this.questionText = questionText;
        this.questionTextLocal = questionTextLocal;
        this.displayOrder = displayOrder;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public Long getCaseCategoryId() {
        return caseCategoryId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getQuestionTextLocal() {
        return questionTextLocal;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public boolean isActive() {
        return active;
    }
}

