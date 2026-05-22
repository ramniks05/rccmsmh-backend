package com.maharashtra.rccms.dto;

import com.maharashtra.rccms.model.UserRole;

public class RegistrationResponse {
    private Long id;
    private UserRole role;
    private String message;
    private boolean profileComplete;

    public RegistrationResponse(Long id, UserRole role, String message) {
        this(id, role, message, true);
    }

    public RegistrationResponse(Long id, UserRole role, String message, boolean profileComplete) {
        this.id = id;
        this.role = role;
        this.message = message;
        this.profileComplete = profileComplete;
    }

    public Long getId() {
        return id;
    }

    public UserRole getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }

    public boolean isProfileComplete() {
        return profileComplete;
    }
}
