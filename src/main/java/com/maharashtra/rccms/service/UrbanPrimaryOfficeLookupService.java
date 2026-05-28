package com.maharashtra.rccms.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves ePCIS primary (parent) office code from urban sub-office code (e.g. 0101)
 * using Mahabhumi {@code getOfficeByDistrict}.
 */
@Service
public class UrbanPrimaryOfficeLookupService {

    private static final Logger log = LoggerFactory.getLogger(UrbanPrimaryOfficeLookupService.class);

    private final LandRecordsClient landRecordsClient;

    public UrbanPrimaryOfficeLookupService(LandRecordsClient landRecordsClient) {
        this.landRecordsClient = landRecordsClient;
    }

    /**
     * @param districtCode ePCIS / LGD district code
     * @param subOfficeCode sub-office from frontend (stored in {@code officeCode})
     */
    public Optional<String> resolvePrimaryOfficeCode(String districtCode, String subOfficeCode) {
        String district = trimToNull(districtCode);
        String sub = trimToNull(subOfficeCode);
        if (district == null || sub == null) {
            return Optional.empty();
        }
        try {
            JsonNode upstream = landRecordsClient.postForm(
                    "/epcis/getOfficeByDistrict",
                    Map.of("district_code", district)
            );
            JsonNode list = extractList(upstream);
            if (list == null) {
                return Optional.empty();
            }
            for (JsonNode row : list) {
                if (!row.isObject()) {
                    continue;
                }
                String primary = primaryFromOfficeRow(row, sub);
                if (primary != null) {
                    return Optional.of(primary);
                }
            }
        } catch (Exception ex) {
            log.warn("Urban primary office lookup failed for district={} sub={}: {}",
                    district, sub, ex.getMessage());
        }
        return Optional.empty();
    }

    private static String primaryFromOfficeRow(JsonNode row, String subOfficeCode) {
        String subField = firstText(row,
                "sub_office_code", "subOfficeCode", "Sub_Office_Code",
                "village_office_code", "villageOfficeCode");
        String officeCode = firstText(row, "office_code", "officeCode", "Office_Code");
        String primaryField = firstText(row,
                "primary_office_code", "primaryOfficeCode", "Primary_Office_Code",
                "parent_office_code", "parentOfficeCode", "primary_office");

        if (equalsIgnoreCase(subField, subOfficeCode)) {
            if (primaryField != null && !equalsIgnoreCase(primaryField, subOfficeCode)) {
                return primaryField;
            }
            if (officeCode != null && !equalsIgnoreCase(officeCode, subOfficeCode)) {
                return officeCode;
            }
        }

        if (equalsIgnoreCase(officeCode, subOfficeCode)) {
            if (primaryField != null && !equalsIgnoreCase(primaryField, subOfficeCode)) {
                return primaryField;
            }
        }

        return null;
    }

    private static JsonNode extractList(JsonNode upstream) {
        if (upstream == null || !upstream.isObject()) {
            return null;
        }
        JsonNode data = upstream.get("data");
        if (data == null || data.isNull()) {
            return null;
        }
        if (data.isArray()) {
            return data;
        }
        if (data.isObject()) {
            for (String key : new String[] {"list", "offices", "officeList", "data", "rows"}) {
                JsonNode nested = data.get(key);
                if (nested != null && nested.isArray()) {
                    return nested;
                }
            }
        }
        return null;
    }

    private static String firstText(JsonNode row, String... fieldNames) {
        for (String name : fieldNames) {
            JsonNode node = row.get(name);
            if (node != null && node.isValueNode()) {
                String text = trimToNull(node.asText());
                if (text != null) {
                    return text;
                }
            }
        }
        return null;
    }

    private static boolean equalsIgnoreCase(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
