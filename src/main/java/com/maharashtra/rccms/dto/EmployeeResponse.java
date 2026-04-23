package com.maharashtra.rccms.dto;

public class EmployeeResponse {
    private final Long id;
    private final String employeeCode;
    private final String fullName;
    private final String fullNameLocal;
    private final String mobile;
    private final String email;
    private final Boolean isActive;

    public EmployeeResponse(Long id, String employeeCode, String fullName, String fullNameLocal, String mobile, String email, Boolean isActive) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.fullNameLocal = fullNameLocal;
        this.mobile = mobile;
        this.email = email;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFullNameLocal() {
        return fullNameLocal;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIsActive() {
        return isActive;
    }
}

