package com.maharashtra.rccms.dto;

public class EmployeeUpdateRequest {
    private String employeeCode;
    private String fullName;
    private String fullNameLocal;
    private String mobile;
    private String email;
    private Boolean isActive;

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullNameLocal() {
        return fullNameLocal;
    }

    public void setFullNameLocal(String fullNameLocal) {
        this.fullNameLocal = fullNameLocal;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}

