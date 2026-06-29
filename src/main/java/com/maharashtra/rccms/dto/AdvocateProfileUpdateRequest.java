package com.maharashtra.rccms.dto;

/**
 * Profile completion after registration ({@code PUT /api/advocates/me/profile}).
 * <p>
 * Updatable: email, mobileNumber, gender, pinCode, address (stateName, districtName,
 * village, addressLine1).
 * <p>
 * Read-only (must match {@code GET /api/advocates/me/profile}): firstName, middleName, lastName.
 * Bar enrollment and place of practice are not part of this request.
 */
public class AdvocateProfileUpdateRequest {

    /** Echo from GET; must match registration — not updated. */
    private String firstName;
    /** Echo from GET; must match registration — not updated. */
    private String middleName;
    /** Echo from GET; must match registration — not updated. */
    private String lastName;
    private String mobileNumber;
    private String email;
    private String gender;
    private String pinCode;
    private Long stateId;
    private String stateName;
    private Long districtId;
    private String districtName;
    private String village;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String lawFirmName;

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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLawFirmName() {
        return lawFirmName;
    }

    public void setLawFirmName(String lawFirmName) {
        this.lawFirmName = lawFirmName;
    }
}
