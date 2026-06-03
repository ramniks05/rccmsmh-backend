package com.maharashtra.rccms.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserRole {
    ADVOCATE,
    PARTY_IN_PERSON,
    PARTY_IN_PERSON_REPRESENTATIVE,
    OFFICER,
    ADMIN;

    @JsonCreator
    public static UserRole fromJson(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String normalized = value.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        return UserRole.valueOf(normalized);
    }
}
