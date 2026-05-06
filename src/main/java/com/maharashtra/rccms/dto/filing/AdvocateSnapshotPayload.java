package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdvocateSnapshotPayload {

    private Long id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String address;
    private String barCouncilNumber;
    private String enrollmentNumber;
    private String lawFirmName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBarCouncilNumber() {
        return barCouncilNumber;
    }

    public void setBarCouncilNumber(String barCouncilNumber) {
        this.barCouncilNumber = barCouncilNumber;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }

    public String getLawFirmName() {
        return lawFirmName;
    }

    public void setLawFirmName(String lawFirmName) {
        this.lawFirmName = lawFirmName;
    }
}
