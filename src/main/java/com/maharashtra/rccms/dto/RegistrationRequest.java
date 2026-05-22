package com.maharashtra.rccms.dto;

import com.maharashtra.rccms.model.UserRole;

public class RegistrationRequest {

    private UserRole role;
    private String password;

    /** Legacy single field; optional when firstName/lastName are provided. */
    private String fullName;
    private String email;
    private String mobileNumber;
    private String address;

    /** Party in person: address from pincode lookup + address line (same shape as advocate profile). */
    private String pinCode;
    private String stateName;
    private String districtName;
    private String subdistrictName;
    private String village;
    private String addressLine1;

    // Advocate registration form (state/district = LGD codes from /api/lookups/states|districts)
    private String firstName;
    private String middleName;
    private String lastName;
    /** State LGD code, e.g. "27" (not display name). */
    private String barEnrollmentState;
    private Integer barEnrollmentYear;
    private String barEnrollmentNumber;
    /** State LGD code for place of practice, e.g. "27". */
    private String placeOfPracticeState;
    /** District LGD code, e.g. "530" (must belong to placeOfPracticeState). */
    private String placeOfPracticeDistrict;
    private String barEnrollmentCertificateStorageKey;
    private String barEnrollmentCertificateFileName;

    /** Legacy advocate fields (optional if barEnrollmentNumber is sent). */
    private String barCouncilNumber;
    private String enrollmentNumber;
    private String lawFirmName;

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
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

    public String getBarEnrollmentState() {
        return barEnrollmentState;
    }

    public void setBarEnrollmentState(String barEnrollmentState) {
        this.barEnrollmentState = barEnrollmentState;
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

    public String getPlaceOfPracticeDistrict() {
        return placeOfPracticeDistrict;
    }

    public void setPlaceOfPracticeDistrict(String placeOfPracticeDistrict) {
        this.placeOfPracticeDistrict = placeOfPracticeDistrict;
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
