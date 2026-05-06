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

@Entity
@Table(name = "application_respondent")
@SuppressWarnings("null")
public class ApplicationRespondent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private FilingApplication application;

    @Column(name = "line_no", nullable = false)
    private Integer lineNo;

    /** Optional: echo UI temp row id for stable mapping on round-trips */
    @Column(name = "client_row_key", length = 64)
    private String clientRowKey;

    @Column(nullable = false, length = 512)
    private String name;

    @Column(length = 32)
    private String mobile;

    @Column(length = 1024)
    private String address;

    public Long getId() {
        return id;
    }

    public FilingApplication getApplication() {
        return application;
    }

    public void setApplication(FilingApplication application) {
        this.application = application;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public String getClientRowKey() {
        return clientRowKey;
    }

    public void setClientRowKey(String clientRowKey) {
        this.clientRowKey = clientRowKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
