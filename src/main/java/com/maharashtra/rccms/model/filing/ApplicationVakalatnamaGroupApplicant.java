package com.maharashtra.rccms.model.filing;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "application_vakalatnama_group_applicant")
@SuppressWarnings("null")
public class ApplicationVakalatnamaGroupApplicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vakalatnama_group_id", nullable = false)
    private ApplicationVakalatnamaGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_applicant_id", nullable = false)
    private ApplicationApplicant applicationApplicant;

    public Long getId() {
        return id;
    }

    public ApplicationVakalatnamaGroup getGroup() {
        return group;
    }

    public void setGroup(ApplicationVakalatnamaGroup group) {
        this.group = group;
    }

    public ApplicationApplicant getApplicationApplicant() {
        return applicationApplicant;
    }

    public void setApplicationApplicant(ApplicationApplicant applicationApplicant) {
        this.applicationApplicant = applicationApplicant;
    }
}
