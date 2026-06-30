package com.maharashtra.rccms.util;

import com.maharashtra.rccms.model.EmployeePosting;
import com.maharashtra.rccms.model.master.Designation;

import java.util.Locale;
import java.util.Objects;

/**
 * Presiding Officer (PO) is the officer posted with designation DySLR at an office.
 * Legacy deployments may still use master designation id {@code 1}.
 */
public final class PresidingOfficerDesignationSupport {

    private static final Long LEGACY_PO_DESIGNATION_ID = 1L;
    private static final String DYSLR_TOKEN = "DYSLR";

    private PresidingOfficerDesignationSupport() {
    }

    public static boolean isPresidingOfficer(EmployeePosting posting) {
        return posting != null && isPresidingOfficer(posting.getDesignation());
    }

    public static boolean isPresidingOfficer(Designation designation) {
        if (designation == null) {
            return false;
        }
        if (Objects.equals(designation.getId(), LEGACY_PO_DESIGNATION_ID)) {
            return true;
        }
        return matchesDySlr(designation.getShortName())
                || matchesDySlr(designation.getName())
                || matchesDySlr(designation.getLocalName())
                || matchesDySlr(designation.getShortNameLocal());
    }

    private static boolean matchesDySlr(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT)
                .replace(".", "")
                .replace(" ", "")
                .replace("-", "");
        return normalized.contains(DYSLR_TOKEN);
    }
}
