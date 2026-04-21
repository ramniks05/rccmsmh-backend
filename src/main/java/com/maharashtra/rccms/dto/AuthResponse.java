package com.maharashtra.rccms.dto;

public class AuthResponse {
    private final String accessToken;
    private final String tokenType;
    private final String role;
    private final String displayName;

    public AuthResponse(String accessToken, String tokenType, String role, String displayName) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.role = role;
        this.displayName = displayName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getRole() {
        return role;
    }

    public String getDisplayName() {
        return displayName;
    }
}
