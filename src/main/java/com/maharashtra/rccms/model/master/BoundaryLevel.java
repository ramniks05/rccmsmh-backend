package com.maharashtra.rccms.model.master;

/**
 * Geographic boundary levels used by office types and office location resolution.
 */
public final class BoundaryLevel {

    public static final String STATE = "STATE";
    public static final String DIVISION = "DIVISION";
    public static final String DISTRICT = "DISTRICT";
    public static final String TALUKA = "TALUKA";
    public static final String VILLAGE = "VILLAGE";

    private BoundaryLevel() {
    }

    public static String normalize(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("boundaryLevel is required");
        }
        String level = raw.trim().toUpperCase();
        if (!isValid(level)) {
            throw new IllegalArgumentException(
                    "Invalid boundaryLevel. Use one of: STATE, DIVISION, DISTRICT, TALUKA, VILLAGE"
            );
        }
        return level;
    }

    public static boolean isValid(String level) {
        return STATE.equals(level)
                || DIVISION.equals(level)
                || DISTRICT.equals(level)
                || TALUKA.equals(level)
                || VILLAGE.equals(level);
    }
}
