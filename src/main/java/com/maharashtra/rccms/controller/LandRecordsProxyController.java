package com.maharashtra.rccms.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maharashtra.rccms.service.LandRecordsClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Map;

/**
 * Backend proxy endpoints for Mahabhumi land records (7/12 and property card).
 * Not public: protected by {@code anyRequest().authenticated()}.
 */
@RestController
@RequestMapping("/api/land-records")
@SuppressWarnings("null")
public class LandRecordsProxyController {

    private final LandRecordsClient landRecordsClient;

    public LandRecordsProxyController(LandRecordsClient landRecordsClient) {
        this.landRecordsClient = landRecordsClient;
    }

    // Rural (7/12)

    @GetMapping("/rural/districts")
    public ResponseEntity<?> ruralDistricts() {
        JsonNode res = landRecordsClient.postForm("/eferfar/getEferfarDistrictofState", null);
        return unwrapData(res);
    }

    @GetMapping("/rural/talukas")
    public ResponseEntity<?> ruralTalukas(@RequestParam("districtCode") String districtCode) {
        JsonNode res = landRecordsClient.postForm(
                "/eferfar/getEferfarTalukasOfDistrict",
                Map.of("district_code", districtCode)
        );
        return unwrapData(res);
    }

    @GetMapping("/rural/villages")
    public ResponseEntity<?> ruralVillages(
            @RequestParam("districtCode") String districtCode,
            @RequestParam("talukaCode") String talukaCode
    ) {
        JsonNode res = landRecordsClient.postForm(
                "/eferfar/getEferfarVillagesOfDistrictAndTaluka",
                Map.of("district_code", districtCode, "taluka_code", talukaCode)
        );
        return unwrapData(res);
    }

    @GetMapping("/rural/sub-survey-list")
    public ResponseEntity<?> ruralSubSurveyList(
            @RequestParam("villageLgdCode") String villageLgdCode,
            @RequestParam("pin") String pin
    ) {
        JsonNode res = landRecordsClient.postForm(
                "/eferfar/getSubSurveyList",
                Map.of("lgd_code", villageLgdCode, "pin", pin)
        );
        return unwrapData(res);
    }

    // Urban (Property Card / ePCIS)

    @GetMapping("/urban/districts")
    public ResponseEntity<?> urbanDistricts() {
        JsonNode res = landRecordsClient.postForm("/epcis/allDistrictList", null);
        return unwrapData(res);
    }

    @GetMapping("/urban/offices")
    public ResponseEntity<?> urbanOffices(@RequestParam("districtCode") String districtCode) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getOfficeByDistrict",
                Map.of("district_code", districtCode)
        );
        return unwrapData(res);
    }

    @GetMapping("/urban/villages")
    public ResponseEntity<?> urbanVillages(@RequestParam("officeCode") String officeCode) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getVillageByOffice",
                Map.of("office_code", officeCode)
        );
        return unwrapData(res);
    }

    @GetMapping("/urban/cts-list")
    public ResponseEntity<?> urbanCtsList(
            @RequestParam("villageCode") String villageCode,
            @RequestParam(name = "ctsNo", required = false) String ctsNo
    ) {
        Map<String, String> form = (ctsNo == null || ctsNo.trim().isEmpty())
                ? Map.of("village_code", villageCode)
                : Map.of("village_code", villageCode, "cts_no", ctsNo);

        JsonNode res = landRecordsClient.postForm("/epcis/getCSNOList", form);
        return unwrapData(res);
    }

    @GetMapping("/urban/notice-nine-view")
    public ResponseEntity<?> urbanNoticeNineView(@RequestParam("inwardNumber") String inwardNumber) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getNoticeNineView",
                Map.of("inward_number", inwardNumber)
        );
        return unwrapNoticeNine(res);
    }

    /**
     * Frontend expects a plain JSON array/object (not wrapped in {status,data,...}).
     * If upstream payload has a "data" field, return it directly.
     * Otherwise return the full payload (error/debug).
     */
    private static ResponseEntity<?> unwrapData(JsonNode upstream) {
        int httpStatus = 200;
        if (upstream != null && upstream.isObject()) {
            JsonNode hs = upstream.get("httpStatus");
            if (hs != null && hs.canConvertToInt()) {
                httpStatus = hs.asInt(200);
            }
            JsonNode data = upstream.get("data");
            if (data != null && !data.isNull()) {
                return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(moveSearchFieldFirst(data));
            }
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(upstream);
    }

    /**
     * Notice 9 is consumed directly by UI and should come back in plain/decrypted form.
     * Normalizes multiple upstream shapes to { "url": "<value>" } when possible.
     */
    private static ResponseEntity<?> unwrapNoticeNine(JsonNode upstream) {
        int httpStatus = 200;
        if (upstream != null && upstream.isObject()) {
            JsonNode hs = upstream.get("httpStatus");
            if (hs != null && hs.canConvertToInt()) {
                httpStatus = hs.asInt(200);
            }

            JsonNode data = upstream.get("data");
            JsonNode normalized = normalizeNoticeNineData(data);
            if (normalized != null) {
                return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(normalized);
            }
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(upstream);
    }

    private static JsonNode normalizeNoticeNineData(JsonNode data) {
        if (data == null || data.isNull()) return null;

        // Decrypted plain string URL/text.
        if (data.isTextual()) {
            String text = normalizeQuotedText(data.asText());
            if (looksLikeBase64Payload(text)) {
                ObjectNode out = JsonNodeFactory.instance.objectNode();
                String mimeType = detectMimeTypeFromBase64(text);
                out.put("type", "base64-file");
                out.put("mimeType", mimeType);
                out.put("base64", text);
                if (mimeType.startsWith("image/")) {
                    out.put("dataUrl", "data:" + mimeType + ";base64," + text);
                }
                return out;
            }
            ObjectNode out = JsonNodeFactory.instance.objectNode();
            out.put("url", text);
            return out;
        }

        // Already object-based payload; try common URL field names first.
        if (data.isObject()) {
            JsonNode url = firstNonBlankText(
                    data.get("url"),
                    data.get("notice9Url"),
                    data.get("notice_9_url"),
                    data.get("noticeNineUrl"),
                    data.get("notice_url"),
                    data.get("viewUrl"),
                    data.get("link")
            );
            if (url != null) {
                ObjectNode out = JsonNodeFactory.instance.objectNode();
                out.put("url", url.asText());
                return out;
            }
            return moveSearchFieldFirst(data);
        }

        // Sometimes upstream returns a single-item list.
        if (data.isArray() && data.size() > 0) {
            JsonNode first = data.get(0);
            JsonNode normalizedFirst = normalizeNoticeNineData(first);
            if (normalizedFirst != null) return normalizedFirst;
        }

        return data;
    }

    private static boolean looksLikeBase64Payload(String s) {
        if (s == null) return false;
        String t = s.replaceAll("\\s+", "");
        if (t.length() < 64 || (t.length() % 4 != 0)) return false;
        return t.matches("^[A-Za-z0-9+/=]+$");
    }

    private static String detectMimeTypeFromBase64(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64.replaceAll("\\s+", ""));
            if (bytes.length >= 4) {
                // JPEG FF D8 FF
                if ((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF) {
                    return "image/jpeg";
                }
                // PNG 89 50 4E 47
                if ((bytes[0] & 0xFF) == 0x89 && (bytes[1] & 0xFF) == 0x50 && (bytes[2] & 0xFF) == 0x4E && (bytes[3] & 0xFF) == 0x47) {
                    return "image/png";
                }
                // PDF 25 50 44 46
                if ((bytes[0] & 0xFF) == 0x25 && (bytes[1] & 0xFF) == 0x50 && (bytes[2] & 0xFF) == 0x44 && (bytes[3] & 0xFF) == 0x46) {
                    return "application/pdf";
                }
            }
        } catch (Exception ignore) {
            // Fallback below.
        }
        return "application/octet-stream";
    }

    private static String normalizeQuotedText(String text) {
        if (text == null) return "";
        String t = text.trim();

        // If upstream decrypted value is itself a quoted JSON string, unwrap once.
        if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
            t = t.substring(1, t.length() - 1).replace("\\\"", "\"").replace("\\/", "/");
        }

        // Sometimes value comes like data:image\/jpg...
        t = t.replace("\\/", "/");
        return t;
    }

    private static JsonNode firstNonBlankText(JsonNode... nodes) {
        if (nodes == null) return null;
        for (JsonNode n : nodes) {
            if (n != null && n.isTextual() && !n.asText().trim().isEmpty()) {
                return n;
            }
        }
        return null;
    }

    @GetMapping("/urban/notice-nine-view-debug")
    public ResponseEntity<?> urbanNoticeNineViewDebug(@RequestParam("inwardNumber") String inwardNumber) {
        JsonNode rawRes = landRecordsClient.postFormRaw(
                "/epcis/getNoticeNineView",
                Map.of("inward_number", inwardNumber)
        );
        JsonNode decodedRes = landRecordsClient.postForm(
                "/epcis/getNoticeNineView",
                Map.of("inward_number", inwardNumber)
        );

        ObjectNode out = JsonNodeFactory.instance.objectNode();
        out.set("rawUpstream", rawRes);
        out.set("decodedUpstream", decodedRes);

        JsonNode rawData = rawRes != null && rawRes.isObject() ? rawRes.get("data") : null;
        if (rawData != null && rawData.isTextual()) {
            out.set("decryptDebug", landRecordsClient.debugDecryptData(rawData.asText()));
        }

        return ResponseEntity.ok(out);
    }

    /**
     * Keeps JSON content same, but if an object contains "search", places it first.
     */
    private static JsonNode moveSearchFieldFirst(JsonNode node) {
        if (node == null) return null;

        if (node.isArray()) {
            ArrayNode out = JsonNodeFactory.instance.arrayNode();
            for (JsonNode item : node) {
                out.add(moveSearchFieldFirst(item));
            }
            return out;
        }

        if (node.isObject()) {
            ObjectNode src = (ObjectNode) node;
            ObjectNode out = JsonNodeFactory.instance.objectNode();

            JsonNode search = src.get("search");
            if (search != null) {
                out.set("search", moveSearchFieldFirst(search));
            }

            src.fields().forEachRemaining(entry -> {
                if (!"search".equals(entry.getKey())) {
                    out.set(entry.getKey(), moveSearchFieldFirst(entry.getValue()));
                }
            });
            return out;
        }

        return node;
    }
}

