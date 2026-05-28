package com.maharashtra.rccms.filing;

import com.maharashtra.rccms.dto.filing.DisputedLandPayload;
import com.maharashtra.rccms.model.filing.ApplicationDisputedLand;

import java.util.List;
import java.util.Map;

/**
 * Urban ePCIS returns a sub-office code (e.g. 0101) and a separate primary/parent office code
 * for case routing. {@code master_office} should be resolved using the primary code.
 */
public final class OfficeCodeResolver {

    private static final List<String> PRIMARY_OFFICE_KEYS = List.of(
            "primaryOfficeCode", "primary_office_code", "Primary_Office_Code",
            "primary_office", "parent_office_code", "parentOfficeCode",
            "primaryOffice", "PrimaryOfficeCode"
    );

    private static final List<String> SUB_OFFICE_KEYS = List.of(
            "officeCode", "office_code", "Office_Code", "office", "subOfficeCode", "sub_office_code"
    );

    private OfficeCodeResolver() {
    }

    /** Code used to lookup {@code master_office} (primary preferred). */
    public static String masterLookupCode(String primaryOfficeCode, String officeCode, Map<String, Object> landDetail) {
        String primary = firstNonBlank(
                trimToNull(primaryOfficeCode),
                pickString(landDetail, PRIMARY_OFFICE_KEYS)
        );
        if (primary != null) {
            return primary;
        }
        return trimToNull(officeCode);
    }

    public static String primaryOfficeCode(DisputedLandPayload payload) {
        if (payload == null) {
            return null;
        }
        String explicit = trimToNull(payload.getPrimaryOfficeCode());
        if (explicit != null) {
            return explicit;
        }
        return pickString(payload.getLandDetail(), PRIMARY_OFFICE_KEYS);
    }

    public static String subOfficeCode(DisputedLandPayload payload) {
        if (payload == null) {
            return null;
        }
        String explicit = trimToNull(payload.getOfficeCode());
        if (explicit != null) {
            return explicit;
        }
        return pickString(payload.getLandDetail(), SUB_OFFICE_KEYS);
    }

    public static String masterLookupCode(ApplicationDisputedLand land, FilingJsonCodec codec) {
        if (land == null) {
            return null;
        }
        Map<String, Object> detail = codec != null ? codec.readMap(land.getLandDetailJson()) : null;
        return masterLookupCode(land.getPrimaryOfficeCode(), land.getOfficeCode(), detail);
    }

    @SuppressWarnings("unchecked")
    private static String pickString(Map<String, Object> map, List<String> keys) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        for (String key : keys) {
            Object value = map.get(key);
            String text = stringify(value);
            if (hasText(text)) {
                return text;
            }
        }
        for (Object value : map.values()) {
            if (value instanceof Map<?, ?> nested) {
                String text = pickString((Map<String, Object>) nested, keys);
                if (hasText(text)) {
                    return text;
                }
            }
            if (value instanceof List<?> list) {
                for (Object item : list) {
                    if (item instanceof Map<?, ?> nested) {
                        String text = pickString((Map<String, Object>) nested, keys);
                        if (hasText(text)) {
                            return text;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static String stringify(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String s) {
            return s.trim();
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        return null;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            String trimmed = trimToNull(value);
            if (trimmed != null) {
                return trimmed;
            }
        }
        return null;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean hasText(String value) {
        return trimToNull(value) != null;
    }
}
