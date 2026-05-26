package com.maharashtra.rccms.filing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maharashtra.rccms.dto.caseflow.RoznamaTableEntryResponse;
import com.maharashtra.rccms.model.caseflow.CaseHearing;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheet;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheetHistory;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheetStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class RoznamaContentHelper {

    private static final TypeReference<List<Map<String, Object>>> TABLE_JSON_TYPE =
            new TypeReference<List<Map<String, Object>>>() {};

    private RoznamaContentHelper() {
    }

    public static List<RoznamaTableEntryResponse> buildTableRows(
            List<CaseHearing> hearings,
            CaseHearing activeHearing,
            CaseOrderSheet sheet,
            List<CaseOrderSheetHistory> histories
    ) {
        Map<Long, SignedRoznamaSnapshot> signedByHearing = signedSnapshotsByHearing(sheet, histories);
        List<RoznamaTableEntryResponse> rows = new ArrayList<>();
        int line = 1;

        if (hearings != null) {
            for (CaseHearing hearing : hearings.stream()
                    .sorted(Comparator.comparingInt(CaseHearing::getHearingNo))
                    .toList()) {
                SignedRoznamaSnapshot snap = signedByHearing.get(hearing.getId());
                if (snap != null) {
                    rows.add(toRow(line++, hearing, snap.content, snap.status, snap.hearingOutcome, true));
                }
            }
        }

        if (activeHearing != null && !signedByHearing.containsKey(activeHearing.getId())) {
            String draft = resolveEditableDraftForHearing(activeHearing, sheet, rows);
            String status = "PO_DRAFT";
            String outcome = null;
            if (sheet != null && sheet.getCurrentHearing() != null
                    && Objects.equals(sheet.getCurrentHearing().getId(), activeHearing.getId())) {
                if (sheet.getStatus() != null) {
                    status = sheet.getStatus().name();
                }
                if (sheet.getHearingOutcome() != null) {
                    outcome = sheet.getHearingOutcome().name();
                }
            }
            rows.add(toRow(line, activeHearing, draft, status, outcome, false));
        }

        renumber(rows);
        return rows;
    }

    public static String toContentJson(List<RoznamaTableEntryResponse> rows) {
        if (rows == null || rows.isEmpty()) {
            return "[]";
        }
        List<Map<String, Object>> payload = new ArrayList<>();
        for (RoznamaTableEntryResponse row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("lineNo", row.getLineNo());
            if (row.getHearingId() != null) {
                map.put("hearingId", row.getHearingId());
            }
            if (row.getHearingNo() != null) {
                map.put("hearingNo", row.getHearingNo());
            }
            String date = row.getDate();
            if (date == null && row.getHearingDate() != null) {
                date = row.getHearingDate().toString();
            }
            if (date != null) {
                map.put("date", date);
            }
            map.put("content", row.getContent() != null ? row.getContent() : "");
            if (row.getStatus() != null) {
                map.put("status", row.getStatus());
            }
            if (row.getHearingOutcome() != null) {
                map.put("hearingOutcome", row.getHearingOutcome());
            }
            map.put("readOnly", row.isReadOnly());
            payload.add(map);
        }
        try {
            return new ObjectMapper().writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Could not serialize roznamma table content.", ex);
        }
    }

    public static List<RoznamaTableEntryResponse> parseContentJson(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ArrayList<>();
        }
        String trimmed = raw.trim();
        if (!trimmed.startsWith("[")) {
            RoznamaTableEntryResponse single = new RoznamaTableEntryResponse();
            single.setLineNo(1);
            single.setContent(trimmed);
            single.setReadOnly(false);
            return List.of(single);
        }
        try {
            List<Map<String, Object>> maps = new ObjectMapper().readValue(trimmed, TABLE_JSON_TYPE);
            List<RoznamaTableEntryResponse> rows = new ArrayList<>();
            int line = 1;
            for (Map<String, Object> map : maps) {
                RoznamaTableEntryResponse row = new RoznamaTableEntryResponse();
                row.setLineNo(intVal(map.get("lineNo"), line++));
                row.setHearingId(longVal(map.get("hearingId")));
                row.setHearingNo(intVal(map.get("hearingNo"), null));
                String date = stringVal(map.get("date"));
                row.setDate(date);
                if (date != null) {
                    try {
                        row.setHearingDate(LocalDate.parse(date));
                    } catch (Exception ignored) {
                        // keep date string only
                    }
                }
                row.setContent(stringVal(map.get("content")));
                row.setStatus(stringVal(map.get("status")));
                row.setHearingOutcome(stringVal(map.get("hearingOutcome")));
                Object ro = map.get("readOnly");
                row.setReadOnly(ro instanceof Boolean b && b);
                rows.add(row);
            }
            renumber(rows);
            return rows;
        } catch (Exception ex) {
            RoznamaTableEntryResponse single = new RoznamaTableEntryResponse();
            single.setLineNo(1);
            single.setContent(trimmed);
            single.setReadOnly(false);
            return List.of(single);
        }
    }

    public static String mergeSaveContent(
            String requestContent,
            List<RoznamaTableEntryResponse> existingRows,
            CaseHearing activeHearing
    ) {
        List<RoznamaTableEntryResponse> parsed = parseContentJson(requestContent);
        List<RoznamaTableEntryResponse> merged = new ArrayList<>();
        for (RoznamaTableEntryResponse prior : existingRows) {
            if (prior.isReadOnly()) {
                merged.add(prior);
            }
        }
        RoznamaTableEntryResponse current = parsed.stream()
                .filter(r -> !r.isReadOnly())
                .reduce((first, second) -> second)
                .orElseGet(() -> parsed.isEmpty() ? null : parsed.get(parsed.size() - 1));
        if (current == null) {
            current = new RoznamaTableEntryResponse();
            current.setContent(requestContent);
        }
        if (activeHearing != null) {
            current.setHearingId(activeHearing.getId());
            current.setHearingNo(activeHearing.getHearingNo());
            current.setHearingDate(activeHearing.getHearingDate());
            current.setDate(activeHearing.getHearingDate() != null ? activeHearing.getHearingDate().toString() : null);
        }
        current.setReadOnly(false);
        if (current.getStatus() == null) {
            current.setStatus("PO_DRAFT");
        }
        merged.add(current);
        renumber(merged);
        return toContentJson(merged);
    }

    public static String priorSignedRowsJson(List<RoznamaTableEntryResponse> rows) {
        List<RoznamaTableEntryResponse> prior = rows.stream().filter(RoznamaTableEntryResponse::isReadOnly).toList();
        return toContentJson(prior);
    }

    private static Map<Long, SignedRoznamaSnapshot> signedSnapshotsByHearing(
            CaseOrderSheet sheet,
            List<CaseOrderSheetHistory> histories
    ) {
        Map<Long, SignedRoznamaSnapshot> out = new LinkedHashMap<>();
        if (sheet != null
                && sheet.getStatus() == CaseOrderSheetStatus.PO_SIGNED
                && sheet.getCurrentHearing() != null) {
            Long hid = sheet.getCurrentHearing().getId();
            String content = extractSignedContentForHearing(hid, sheet.getFinalContent(), sheet.getContent(), sheet.getDraftContent());
            String outcome = sheet.getHearingOutcome() != null ? sheet.getHearingOutcome().name() : null;
            out.put(hid, new SignedRoznamaSnapshot(content, "PO_SIGNED", outcome, true));
        }
        if (histories != null) {
            for (CaseOrderSheetHistory row : histories) {
                if (row.getCaseHearing() == null || row.getCaseHearing().getId() == null) {
                    continue;
                }
                if (!"PO_SIGNED".equals(parseHistoryStatus(row.getRemarks()))) {
                    continue;
                }
                Long hid = row.getCaseHearing().getId();
                out.putIfAbsent(hid, new SignedRoznamaSnapshot(
                        row.getContent(),
                        "PO_SIGNED",
                        null,
                        true
                ));
            }
        }
        return out;
    }

    private static String resolveEditableDraftForHearing(
            CaseHearing activeHearing,
            CaseOrderSheet sheet,
            List<RoznamaTableEntryResponse> priorRows
    ) {
        if (sheet != null && sheet.getCurrentHearing() != null
                && Objects.equals(sheet.getCurrentHearing().getId(), activeHearing.getId())) {
            String draft = firstNonBlank(sheet.getDraftContent(), sheet.getContent());
            if (draft != null) {
                List<RoznamaTableEntryResponse> parsed = parseContentJson(draft);
                for (RoznamaTableEntryResponse row : parsed) {
                    if (!row.isReadOnly()
                            && (row.getHearingId() == null || Objects.equals(row.getHearingId(), activeHearing.getId()))) {
                        return row.getContent() != null ? row.getContent() : "";
                    }
                }
                if (!parsed.isEmpty() && parsed.size() == 1 && !parsed.get(0).isReadOnly()) {
                    return parsed.get(0).getContent() != null ? parsed.get(0).getContent() : "";
                }
            }
        }
        for (RoznamaTableEntryResponse row : priorRows) {
            if (!row.isReadOnly() && Objects.equals(row.getHearingId(), activeHearing.getId())) {
                return row.getContent() != null ? row.getContent() : "";
            }
        }
        return "";
    }

    private static RoznamaTableEntryResponse toRow(
            int line,
            CaseHearing hearing,
            String content,
            String status,
            String hearingOutcome,
            boolean readOnly
    ) {
        RoznamaTableEntryResponse row = new RoznamaTableEntryResponse();
        row.setLineNo(line);
        row.setHearingId(hearing.getId());
        row.setHearingNo(hearing.getHearingNo());
        row.setHearingDate(hearing.getHearingDate());
        row.setDate(hearing.getHearingDate() != null ? hearing.getHearingDate().toString() : null);
        row.setContent(content != null ? content : "");
        row.setStatus(status);
        row.setHearingOutcome(hearingOutcome);
        row.setReadOnly(readOnly);
        return row;
    }

    private static void renumber(List<RoznamaTableEntryResponse> rows) {
        int line = 1;
        for (RoznamaTableEntryResponse row : rows) {
            row.setLineNo(line++);
        }
    }

    private static String parseHistoryStatus(String remarks) {
        if (remarks == null) {
            return null;
        }
        int sep = remarks.indexOf(" | ");
        return (sep >= 0 ? remarks.substring(0, sep) : remarks).trim();
    }

    private static String stringVal(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private static Integer intVal(Object value, Integer fallback) {
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private static Long longVal(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        if (value != null) {
            try {
                return Long.parseLong(String.valueOf(value));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static String extractSignedContentForHearing(Long hearingId, String... jsonOrTextSources) {
        for (String source : jsonOrTextSources) {
            if (source == null || source.isBlank()) {
                continue;
            }
            if (source.trim().startsWith("[")) {
                for (RoznamaTableEntryResponse row : parseContentJson(source)) {
                    if (Objects.equals(row.getHearingId(), hearingId) && row.getContent() != null) {
                        return row.getContent();
                    }
                }
            } else {
                return source.trim();
            }
        }
        return null;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private static final class SignedRoznamaSnapshot {
        private final String content;
        private final String status;
        private final String hearingOutcome;
        private final boolean readOnly;

        private SignedRoznamaSnapshot(String content, String status, String hearingOutcome, boolean readOnly) {
            this.content = content;
            this.status = status;
            this.hearingOutcome = hearingOutcome;
            this.readOnly = readOnly;
        }
    }
}
