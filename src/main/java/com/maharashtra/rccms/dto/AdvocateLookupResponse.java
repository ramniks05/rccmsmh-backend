package com.maharashtra.rccms.dto;

import java.time.LocalDateTime;

/** Advocate profile fields for lookup APIs (password hash is never included). */
public class AdvocateLookupResponse {
    private final Long id;
    private final String fullName;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String email;
    private final String mobileNumber;
    private final String address;
    private final String barEnrollmentState;
    private final String barEnrollmentStateName;
    private final Integer barEnrollmentYear;
    private final String barEnrollmentNumber;
    private final String barCouncilNumber;
    private final String enrollmentNumber;
    private final String placeOfPracticeState;
    private final String placeOfPracticeStateName;
    private final String placeOfPracticeDistrict;
    private final String placeOfPracticeDistrictName;
    private final String lawFirmName;
    private final boolean profileComplete;
    private final LocalDateTime createdAt;

    public AdvocateLookupResponse(
            Long id,
            String fullName,
            String firstName,
            String middleName,
            String lastName,
            String email,
            String mobileNumber,
            String address,
            String barEnrollmentState,
            String barEnrollmentStateName,
            Integer barEnrollmentYear,
            String barEnrollmentNumber,
            String barCouncilNumber,
            String enrollmentNumber,
            String placeOfPracticeState,
            String placeOfPracticeStateName,
            String placeOfPracticeDistrict,
            String placeOfPracticeDistrictName,
            String lawFirmName,
            boolean profileComplete,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.fullName = fullName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.barEnrollmentState = barEnrollmentState;
        this.barEnrollmentStateName = barEnrollmentStateName;
        this.barEnrollmentYear = barEnrollmentYear;
        this.barEnrollmentNumber = barEnrollmentNumber;
        this.barCouncilNumber = barCouncilNumber;
        this.enrollmentNumber = enrollmentNumber;
        this.placeOfPracticeState = placeOfPracticeState;
        this.placeOfPracticeStateName = placeOfPracticeStateName;
        this.placeOfPracticeDistrict = placeOfPracticeDistrict;
        this.placeOfPracticeDistrictName = placeOfPracticeDistrictName;
        this.lawFirmName = lawFirmName;
        this.profileComplete = profileComplete;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getBarEnrollmentState() {
        return barEnrollmentState;
    }

    public String getBarEnrollmentStateName() {
        return barEnrollmentStateName;
    }

    public Integer getBarEnrollmentYear() {
        return barEnrollmentYear;
    }

    public String getBarEnrollmentNumber() {
        return barEnrollmentNumber;
    }

    public String getBarCouncilNumber() {
        return barCouncilNumber;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public String getPlaceOfPracticeState() {
        return placeOfPracticeState;
    }

    public String getPlaceOfPracticeStateName() {
        return placeOfPracticeStateName;
    }

    public String getPlaceOfPracticeDistrict() {
        return placeOfPracticeDistrict;
    }

    public String getPlaceOfPracticeDistrictName() {
        return placeOfPracticeDistrictName;
    }

    public String getLawFirmName() {
        return lawFirmName;
    }

    public boolean isProfileComplete() {
        return profileComplete;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
