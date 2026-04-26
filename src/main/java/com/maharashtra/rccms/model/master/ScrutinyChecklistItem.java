package com.maharashtra.rccms.model.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Scrutiny checklist questions configured per CaseCategory + Subject.
 * This is a single-table design: question text is stored directly here (no separate question master).
 */
@Entity
@Table(
        name = "master_scrutiny_checklist_item",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_scrutiny_item_category_subject_order",
                columnNames = {"case_category_id", "subject_id", "display_order"}
        )
)
public class ScrutinyChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_category_id", nullable = false)
    private CaseCategory caseCategory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "question_text", nullable = false, length = 1024)
    private String questionText;

    @Column(name = "question_text_local", length = 2048)
    private String questionTextLocal;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CaseCategory getCaseCategory() {
        return caseCategory;
    }

    public void setCaseCategory(CaseCategory caseCategory) {
        this.caseCategory = caseCategory;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

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

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

