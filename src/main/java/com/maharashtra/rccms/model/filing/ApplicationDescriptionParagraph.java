package com.maharashtra.rccms.model.filing;

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

@Entity
@Table(
        name = "application_description_paragraph",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_app_desc_para",
                columnNames = {"application_id", "para_no"}
        )
)
@SuppressWarnings("null")
public class ApplicationDescriptionParagraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private FilingApplication application;

    @Column(name = "para_no", nullable = false)
    private Integer paraNo;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "text_mr", columnDefinition = "TEXT")
    private String textMr;

    public Long getId() {
        return id;
    }

    public FilingApplication getApplication() {
        return application;
    }

    public void setApplication(FilingApplication application) {
        this.application = application;
    }

    public Integer getParaNo() {
        return paraNo;
    }

    public void setParaNo(Integer paraNo) {
        this.paraNo = paraNo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextMr() {
        return textMr;
    }

    public void setTextMr(String textMr) {
        this.textMr = textMr;
    }
}
