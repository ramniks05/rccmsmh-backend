package com.maharashtra.rccms.dto;

import com.maharashtra.rccms.model.UserRole;

public class AuthLoginRequest {
    /** Which account type is signing in; drives which table / admin path is used. */
    private UserRole role;
    /** Registered users: email. Admin: configured admin user id. */
    private String loginId;
    private String password;

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
