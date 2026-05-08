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

    @Column(name = "first_name", length = 128)
    private String firstName;

    @Column(name = "middle_name", length = 128)
    private String middleName;

    @Column(name = "last_name", length = 128)
    private String lastName;

    @Column(length = 6)
    private String pincode;

    @Column(length = 255)
    private String district;

    @Column(length = 255)
    private String taluka;

    @Column(length = 255)
    private String village;

    @Column(name = "village_value", length = 512)
    private String villageValue;

    @Column(length = 255)
    private String email;

    @Column(length = 32)
    private String mobile;

    @Column(length = 10)
    private String dob;

    @Column(length = 16)
    private String age;

    @Column(length = 255)
    private String occupation;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTaluka() {
        return taluka;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getVillageValue() {
        return villageValue;
    }

    public void setVillageValue(String villageValue) {
        this.villageValue = villageValue;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
