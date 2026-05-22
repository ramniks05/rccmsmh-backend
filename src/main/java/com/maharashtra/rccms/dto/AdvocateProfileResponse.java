package com.maharashtra.rccms.dto;

import java.time.LocalDateTime;

public class AdvocateProfileResponse {

    private Long id;
    private String userType;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String email;
    private String mobileNumber;
    /** State LGD code (e.g. "27"). */
    private String barEnrollmentState;
    private String barEnrollmentStateName;
    private Integer barEnrollmentYear;
    private String barEnrollmentNumber;
    /** State LGD code for place of practice. */
    private String placeOfPracticeState;
    private String placeOfPracticeStateName;
    /** District LGD code (e.g. "530"). */
    private String placeOfPracticeDistrict;
    private String placeOfPracticeDistrictName;
    private String barEnrollmentCertificateStorageKey;
    private String barEnrollmentCertificateFileName;
    private boolean barEnrollmentCertificateUploaded;
    private String gender;
    private String pinCode;
    private Long stateId;
    private String stateName;
    private Long districtId;
    private String districtName;
    private Long subdistrictId;
    private String subdistrictName;
    private String village;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String address;
    private String lawFirmName;
    private boolean profileComplete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
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

    public String getBarEnrollmentState() {
        return barEnrollmentState;
    }

    public void setBarEnrollmentState(String barEnrollmentState) {
        this.barEnrollmentState = barEnrollmentState;
    }

    public String getBarEnrollmentStateName() {
        return barEnrollmentStateName;
    }

    public void setBarEnrollmentStateName(String barEnrollmentStateName) {
        this.barEnrollmentStateName = barEnrollmentStateName;
    }

    public Integer getBarEnrollmentYear() {
        return barEnrollmentYear;
    }

    public void setBarEnrollmentYear(Integer barEnrollmentYear) {
        this.barEnrollmentYear = barEnrollmentYear;
    }

    public String getBarEnrollmentNumber() {
        return barEnrollmentNumber;
    }

    public void setBarEnrollmentNumber(String barEnrollmentNumber) {
        this.barEnrollmentNumber = barEnrollmentNumber;
    }

    public String getPlaceOfPracticeState() {
        return placeOfPracticeState;
    }

    public void setPlaceOfPracticeState(String placeOfPracticeState) {
        this.placeOfPracticeState = placeOfPracticeState;
    }

    public String getPlaceOfPracticeStateName() {
        return placeOfPracticeStateName;
    }

    public void setPlaceOfPracticeStateName(String placeOfPracticeStateName) {
        this.placeOfPracticeStateName = placeOfPracticeStateName;
    }

    public String getPlaceOfPracticeDistrict() {
        return placeOfPracticeDistrict;
    }

    public void setPlaceOfPracticeDistrict(String placeOfPracticeDistrict) {
        this.placeOfPracticeDistrict = placeOfPracticeDistrict;
    }

    public String getPlaceOfPracticeDistrictName() {
        return placeOfPracticeDistrictName;
    }

    public void setPlaceOfPracticeDistrictName(String placeOfPracticeDistrictName) {
        this.placeOfPracticeDistrictName = placeOfPracticeDistrictName;
    }

    public String getBarEnrollmentCertificateStorageKey() {
        return barEnrollmentCertificateStorageKey;
    }

    public void setBarEnrollmentCertificateStorageKey(String barEnrollmentCertificateStorageKey) {
        this.barEnrollmentCertificateStorageKey = barEnrollmentCertificateStorageKey;
    }

    public String getBarEnrollmentCertificateFileName() {
        return barEnrollmentCertificateFileName;
    }

    public void setBarEnrollmentCertificateFileName(String barEnrollmentCertificateFileName) {
        this.barEnrollmentCertificateFileName = barEnrollmentCertificateFileName;
    }

    public boolean isBarEnrollmentCertificateUploaded() {
        return barEnrollmentCertificateUploaded;
    }

    public void setBarEnrollmentCertificateUploaded(boolean barEnrollmentCertificateUploaded) {
        this.barEnrollmentCertificateUploaded = barEnrollmentCertificateUploaded;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Long getSubdistrictId() {
        return subdistrictId;
    }

    public void setSubdistrictId(Long subdistrictId) {
        this.subdistrictId = subdistrictId;
    }

    public String getSubdistrictName() {
        return subdistrictName;
    }

    public void setSubdistrictName(String subdistrictName) {
        this.subdistrictName = subdistrictName;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLawFirmName() {
        return lawFirmName;
    }

    public void setLawFirmName(String lawFirmName) {
        this.lawFirmName = lawFirmName;
    }

    public boolean isProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
