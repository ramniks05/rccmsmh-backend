package com.maharashtra.rccms.util;

import com.maharashtra.rccms.model.AdvocateRegistration;

import java.util.ArrayList;
import java.util.List;

public final class AdvocateRegistrationSupport {

    private AdvocateRegistrationSupport() {
    }

    public static String buildFullName(String firstName, String middleName, String lastName) {
        List<String> parts = new ArrayList<>();
        addPart(parts, firstName);
        addPart(parts, middleName);
        addPart(parts, lastName);
        if (parts.isEmpty()) {
            return null;
        }
        return String.join(" ", parts);
    }

    public static String buildAddress(
            String addressLine1,
            String addressLine2,
            String addressLine3,
            String village,
            String subDistrictName,
            String districtName,
            String stateName,
            String pinCode
    ) {
        List<String> parts = new ArrayList<>();
        addPart(parts, addressLine1);
        addPart(parts, addressLine2);
        addPart(parts, addressLine3);
        if (hasText(village)) {
            parts.add(village);
        }
        if (hasText(subDistrictName)) {
            parts.add(subDistrictName);
        }
        if (hasText(districtName)) {
            parts.add(districtName);
        }
        if (hasText(stateName)) {
            parts.add(stateName);
        }
        if (hasText(pinCode)) {
            parts.add(pinCode);
        }
        if (parts.isEmpty()) {
            return null;
        }
        return String.join(", ", parts);
    }

    public static void syncDerivedFields(AdvocateRegistration row) {
        String fullName = buildFullName(row.getFirstName(), row.getMiddleName(), row.getLastName());
        if (fullName != null) {
            row.setFullName(fullName);
        }
        String enrollment = trimToNull(row.getBarEnrollmentNumber());
        if (enrollment != null) {
            row.setEnrollmentNumber(enrollment);
            row.setBarCouncilNumber(enrollment);
        }
        String address = buildAddress(
                row.getAddressLine1(),
                row.getAddressLine2(),
                row.getAddressLine3(),
                row.getVillage(),
                null,
                row.getDistrictName(),
                row.getStateName(),
                row.getPinCode()
        );
        if (address != null) {
            row.setAddress(address);
        }
        row.setProfileComplete(isProfileComplete(row));
    }

    public static boolean isProfileComplete(AdvocateRegistration row) {
        return hasText(row.getFirstName())
                && hasText(row.getLastName())
                && hasText(row.getMobileNumber())
                && hasText(row.getEmail())
                && row.getGender() != null
                && hasText(row.getPinCode())
                && hasText(row.getStateName())
                && hasText(row.getDistrictName())
                && hasText(row.getVillage())
                && hasText(row.getAddressLine1());
    }

    private static void addPart(List<String> parts, String value) {
        String t = trimToNull(value);
        if (t != null) {
            parts.add(t);
        }
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    public static void validateMobile(String mobile) {
        String m = trimToNull(mobile);
        if (m == null || !m.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Mobile number must be 10 digits.");
        }
    }

    public static void validateEmail(String email) {
        String e = trimToNull(email);
        if (e == null || !e.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Valid email is required.");
        }
    }

    public static void validatePinCode(String pinCode) {
        String p = trimToNull(pinCode);
        if (p == null || !p.matches("^\\d{6}$")) {
            throw new IllegalArgumentException("Pin code must be 6 digits.");
        }
    }
}
