package com.maharashtra.rccms.dto;

import java.time.LocalDateTime;

/** Advocate profile fields for lookup APIs (password hash is never included). */
public class AdvocateLookupResponse {
    private final Long id;
    private final String fullName;
    private final String email;
    private final String mobileNumber;
    private final String address;
    private final String barCouncilNumber;
    private final String enrollmentNumber;
    private final String lawFirmName;
    private final LocalDateTime createdAt;

    public AdvocateLookupResponse(
            Long id,
            String fullName,
            String email,
            String mobileNumber,
            String address,
            String barCouncilNumber,
            String enrollmentNumber,
            String lawFirmName,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.barCouncilNumber = barCouncilNumber;
        this.enrollmentNumber = enrollmentNumber;
        this.lawFirmName = lawFirmName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
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

    public String getBarCouncilNumber() {
        return barCouncilNumber;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public String getLawFirmName() {
        return lawFirmName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
