package com.maharashtra.rccms.dto;

import com.maharashtra.rccms.model.UserRole;

public class RegistrationResponse {
    private Long id;
    private UserRole role;
    private String message;

    public RegistrationResponse(Long id, UserRole role, String message) {
        this.id = id;
        this.role = role;
        this.message = message;
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
}
