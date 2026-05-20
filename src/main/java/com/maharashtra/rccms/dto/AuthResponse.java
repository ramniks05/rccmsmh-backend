package com.maharashtra.rccms.dto;

public class AuthResponse {
    private final String accessToken;
    private final String tokenType;
    private final String role;
    private final String displayName;
    private final Long designationId;
    private final String designationName;
    private final Long officeId;
    private final String officeName;
    private final String officeCode;

    public AuthResponse(String accessToken, String tokenType, String role, String displayName) {
        this(accessToken, tokenType, role, displayName, null, null, null, null, null);
    }

    public AuthResponse(
            String accessToken,
            String tokenType,
            String role,
            String displayName,
            Long designationId,
            String designationName,
            Long officeId,
            String officeName,
            String officeCode
    ) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.role = role;
        this.displayName = displayName;
        this.designationId = designationId;
        this.designationName = designationName;
        this.officeId = officeId;
        this.officeName = officeName;
        this.officeCode = officeCode;
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

    public Long getDesignationId() {
        return designationId;
    }

    public String getDesignationName() {
        return designationName;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public String getOfficeCode() {
        return officeCode;
    }
}
