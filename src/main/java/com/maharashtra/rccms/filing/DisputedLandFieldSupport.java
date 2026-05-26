package com.maharashtra.rccms.filing;

import com.maharashtra.rccms.dto.filing.DisputedLandPayload;
import com.maharashtra.rccms.model.filing.ApplicationDisputedLand;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps land-record / UI aliases into normalized disputed-land columns and back for preview.
 */
public final class DisputedLandFieldSupport {

    private static final List<String> TOTAL_AREA_KEYS = List.of(
            "totalArea", "total_area", "Total_Area", "TotalArea", "total_area_ha",
            "totalAreaHa", "areaTotal", "total_extent", "TotalExtent"
    );
    private static final List<String> DISPUTED_AREA_KEYS = List.of(
            "disputedArea", "disputed_area", "Disputed_Area", "DisputedArea",
            "disputed_area_ha", "disputedAreaHa", "areaDisputed"
    );
    private static final List<String> AREA_UNIT_KEYS = List.of(
            "areaUnit", "area_unit", "Area_Unit", "unit", "area_unit_name", "AreaUnit"
    );
    private static final List<String> HOLDERS_KEYS = List.of(
            "landHoldersText", "land_holders_text", "landHolders", "land_holders",
            "holders", "holderNames", "ownerName", "Owner_Name", "owners"
    );

    private DisputedLandFieldSupport() {
    }

    public static void persistFromPayload(DisputedLandPayload payload, ApplicationDisputedLand row, FilingJsonCodec codec) {
        Map<String, Object> detail = mergeDetail(payload);

        String totalArea = firstNonBlank(payload.getTotalArea(), pickString(detail, TOTAL_AREA_KEYS));
        String disputedArea = firstNonBlank(payload.getDisputedArea(), pickString(detail, DISPUTED_AREA_KEYS));
        String areaUnit = firstNonBlank(payload.getAreaUnit(), pickString(detail, AREA_UNIT_KEYS));
        String holders = firstNonBlank(payload.getLandHoldersText(), pickString(detail, HOLDERS_KEYS));

        row.setTotalArea(trimToNull(totalArea));
        row.setDisputedArea(trimToNull(disputedArea));
        row.setAreaUnit(trimToNull(areaUnit));
        row.setLandHoldersText(trimToNull(holders));

        if (!detail.isEmpty()) {
            row.setLandDetailJson(codec.toJson(detail));
        } else if (payload.getLandDetail() != null && !payload.getLandDetail().isEmpty()) {
            row.setLandDetailJson(codec.toJson(payload.getLandDetail()));
        }
    }

    public static void enrichPayload(ApplicationDisputedLand row, DisputedLandPayload dto, FilingJsonCodec codec) {
        Map<String, Object> detail = codec.readMap(row.getLandDetailJson());
        if (detail == null) {
            detail = dto.getLandDetail() != null ? new LinkedHashMap<>(dto.getLandDetail()) : new LinkedHashMap<>();
        } else if (dto.getLandDetail() != null) {
            detail.putAll(dto.getLandDetail());
        }
        dto.setLandDetail(detail.isEmpty() ? null : detail);

        if (!hasText(dto.getTotalArea())) {
            dto.setTotalArea(firstNonBlank(row.getTotalArea(), pickString(detail, TOTAL_AREA_KEYS)));
        }
        if (!hasText(dto.getDisputedArea())) {
            dto.setDisputedArea(firstNonBlank(row.getDisputedArea(), pickString(detail, DISPUTED_AREA_KEYS)));
        }
        if (!hasText(dto.getAreaUnit())) {
            dto.setAreaUnit(firstNonBlank(row.getAreaUnit(), pickString(detail, AREA_UNIT_KEYS)));
        }
        if (!hasText(dto.getLandHoldersText())) {
            dto.setLandHoldersText(firstNonBlank(row.getLandHoldersText(), pickString(detail, HOLDERS_KEYS)));
        }
    }

    public static void mergeSnapshotLand(DisputedLandPayload target, DisputedLandPayload snapshot) {
        if (target == null || snapshot == null) {
            return;
        }
        if (!hasText(target.getTotalArea())) {
            target.setTotalArea(snapshot.getTotalArea());
        }
        if (!hasText(target.getDisputedArea())) {
            target.setDisputedArea(snapshot.getDisputedArea());
        }
        if (!hasText(target.getAreaUnit())) {
            target.setAreaUnit(snapshot.getAreaUnit());
        }
        if (!hasText(target.getLandHoldersText())) {
            target.setLandHoldersText(snapshot.getLandHoldersText());
        }
        if ((target.getLandDetail() == null || target.getLandDetail().isEmpty())
                && snapshot.getLandDetail() != null && !snapshot.getLandDetail().isEmpty()) {
            target.setLandDetail(new LinkedHashMap<>(snapshot.getLandDetail()));
        }
        enrichPayloadFromDetailOnly(target);
    }

    private static void enrichPayloadFromDetailOnly(DisputedLandPayload dto) {
        Map<String, Object> detail = dto.getLandDetail();
        if (detail == null || detail.isEmpty()) {
            return;
        }
        if (!hasText(dto.getTotalArea())) {
            dto.setTotalArea(pickString(detail, TOTAL_AREA_KEYS));
        }
        if (!hasText(dto.getDisputedArea())) {
            dto.setDisputedArea(pickString(detail, DISPUTED_AREA_KEYS));
        }
        if (!hasText(dto.getAreaUnit())) {
            dto.setAreaUnit(pickString(detail, AREA_UNIT_KEYS));
        }
        if (!hasText(dto.getLandHoldersText())) {
            dto.setLandHoldersText(pickString(detail, HOLDERS_KEYS));
        }
    }

    private static Map<String, Object> mergeDetail(DisputedLandPayload payload) {
        Map<String, Object> detail = new LinkedHashMap<>();
        if (payload.getLandDetail() != null) {
            detail.putAll(payload.getLandDetail());
        }
        if (payload.getExtraFields() != null) {
            detail.putAll(payload.getExtraFields());
        }
        return detail;
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
