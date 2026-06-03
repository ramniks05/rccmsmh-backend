package com.maharashtra.rccms.util;

import com.maharashtra.rccms.model.Employee;
import com.maharashtra.rccms.repository.EmployeeRepository;

import java.util.Optional;

public final class EmployeeLoginSupport {

    public static final String OFFICER_LOCAL_DOMAIN = "@officer.local";
    public static final String DEFAULT_OFFICER_PASSWORD = "Officer@123";

    private EmployeeLoginSupport() {
    }

    public static String buildLoginId(String email, String employeeCode) {
        if (hasText(email)) {
            return email.trim().toLowerCase();
        }
        if (!hasText(employeeCode)) {
            throw new IllegalArgumentException("employeeCode is required when email is not provided.");
        }
        return employeeCode.trim().toLowerCase() + OFFICER_LOCAL_DOMAIN;
    }

    public static Optional<Employee> findByLoginId(EmployeeRepository employeeRepository, String loginId) {
        if (!hasText(loginId)) {
            return Optional.empty();
        }
        String normalized = loginId.trim().toLowerCase();
        if (normalized.endsWith(OFFICER_LOCAL_DOMAIN)) {
            String employeeCode = normalized.substring(0, normalized.length() - OFFICER_LOCAL_DOMAIN.length()).trim();
            if (hasText(employeeCode)) {
                return employeeRepository.findFirstByEmployeeCodeIgnoreCase(employeeCode);
            }
            return Optional.empty();
        }
        return employeeRepository.findFirstByEmailIgnoreCase(normalized);
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
