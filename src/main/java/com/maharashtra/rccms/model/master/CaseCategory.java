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

/**
 * Case category / case type master (Suit, First Appeal, Second Appeal, ...).
 * <p>
 * Option A: each category points to the {@link OfficeType} that handles it initially (hearing / filing office type).
 * Optional {@link #nextCategory} links related case types in sequence.
 * </p>
 */
@Entity
@Table(name = "master_case_category")
public class CaseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "local_name", length = 512)
    private String localName;

    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hearing_office_type_id", nullable = false)
    private OfficeType hearingOfficeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_case_category_id")
    private CaseCategory nextCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public Integer getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public OfficeType getHearingOfficeType() {
        return hearingOfficeType;
    }

    public void setHearingOfficeType(OfficeType hearingOfficeType) {
        this.hearingOfficeType = hearingOfficeType;
    }

    public CaseCategory getNextCategory() {
        return nextCategory;
    }

    public void setNextCategory(CaseCategory nextCategory) {
        this.nextCategory = nextCategory;
    }
}
