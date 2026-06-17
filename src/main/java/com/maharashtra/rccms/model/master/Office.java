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

@Entity
@Table(name = "master_office")
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "office_type_id", nullable = false)
    private OfficeType officeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id")
    private Division division;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taluka_id")
    private Taluka taluka;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "office_code", length = 64)
    private String officeCode;

    @Column(name = "local_name", length = 512)
    private String localName;

    @Column(name = "short_name", length = 64)
    private String shortName;

    @Column(name = "short_name_local", length = 128)
    private String shortNameLocal;

    @Column(name = "office_address", length = 512)
    private String officeAddress;

    @Column(name = "office_address_local", length = 512)
    private String officeAddressLocal;

    @Column(length = 190)
    private String email;

    @Column(name = "office_contact_no", length = 32)
    private String officeContactNo;

    /** Denormalized state LGD code for import / integration lookups. */
    @Column(name = "state_lgd_code", length = 64)
    private String stateLgdCode;

    /** Denormalized district LGD code for import / integration lookups. */
    @Column(name = "district_lgd_code", length = 64)
    private String districtLgdCode;

    /** Denormalized taluka LGD code for import / integration lookups. */
    @Column(name = "taluka_lgd_code", length = 64)
    private String talukaLgdCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public OfficeType getOfficeType() {
        return officeType;
    }

    public void setOfficeType(OfficeType officeType) {
        this.officeType = officeType;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Taluka getTaluka() {
        return taluka;
    }

    public void setTaluka(Taluka taluka) {
        this.taluka = taluka;
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

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortNameLocal() {
        return shortNameLocal;
    }

    public void setShortNameLocal(String shortNameLocal) {
        this.shortNameLocal = shortNameLocal;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getOfficeAddressLocal() {
        return officeAddressLocal;
    }

    public void setOfficeAddressLocal(String officeAddressLocal) {
        this.officeAddressLocal = officeAddressLocal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOfficeContactNo() {
        return officeContactNo;
    }

    public void setOfficeContactNo(String officeContactNo) {
        this.officeContactNo = officeContactNo;
    }

    public String getStateLgdCode() {
        return stateLgdCode;
    }

    public void setStateLgdCode(String stateLgdCode) {
        this.stateLgdCode = stateLgdCode;
    }

    public String getDistrictLgdCode() {
        return districtLgdCode;
    }

    public void setDistrictLgdCode(String districtLgdCode) {
        this.districtLgdCode = districtLgdCode;
    }

    public String getTalukaLgdCode() {
        return talukaLgdCode;
    }

    public void setTalukaLgdCode(String talukaLgdCode) {
        this.talukaLgdCode = talukaLgdCode;
    }
}

