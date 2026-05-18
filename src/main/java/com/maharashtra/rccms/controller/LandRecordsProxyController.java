package com.maharashtra.rccms.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maharashtra.rccms.service.LandRecordsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
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
    private final String satbaraPdfPath;
    private final String satbaraPdfFallbackPath;

    public LandRecordsProxyController(
            LandRecordsClient landRecordsClient,
            @Value("${rccms.land-records.rural.satbara-pdf-path:/eferfar/getSatbaraPDF}") String satbaraPdfPath,
            @Value("${rccms.land-records.rural.satbara-pdf-fallback-path:/eferfar/getDigitallySignedSatbaraPDF}") String satbaraPdfFallbackPath
    ) {
        this.landRecordsClient = landRecordsClient;
        this.satbaraPdfPath = satbaraPdfPath;
        this.satbaraPdfFallbackPath = satbaraPdfFallbackPath;
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

    /**
     * Rural 7/12 land detail by survey (G2B: GET /home/GetLandDetailSurvyWise).
     * Upstream uses {@code Lgd_code}, {@code pin}, {@code pin1}, {@code pin2}.
     */
    @GetMapping("/rural/land-detail-survey-wise")
    public ResponseEntity<?> ruralLandDetailSurveyWise(
            @RequestParam("villageLgdCode") String villageLgdCode,
            @RequestParam("pin") String pin,
            @RequestParam(name = "pin1", required = false) String pin1,
            @RequestParam(name = "pin2", required = false) String pin2
    ) {
        return ruralLandDetailSurveyWiseInternal(villageLgdCode, pin, pin1, pin2);
    }

    @PostMapping("/rural/land-detail-survey-wise")
    public ResponseEntity<?> ruralLandDetailSurveyWisePost(
            @RequestParam("villageLgdCode") String villageLgdCode,
            @RequestParam("pin") String pin,
            @RequestParam(name = "pin1", required = false) String pin1,
            @RequestParam(name = "pin2", required = false) String pin2
    ) {
        return ruralLandDetailSurveyWiseInternal(villageLgdCode, pin, pin1, pin2);
    }

    private ResponseEntity<?> ruralLandDetailSurveyWiseInternal(
            String villageLgdCode,
            String pin,
            String pin1,
            String pin2
    ) {
        JsonNode res = landRecordsClient.getG2bLandDetailSurvyWise(villageLgdCode, pin, pin1, pin2);
        return unwrapG2bLandDetail(res);
    }

    /**
     * Check whether Satbara is digitally signed (upstream: POST /eferfar/checkIfSatbaraIsDigitallySigned).
     */
    @GetMapping("/rural/check-digitally-signed-satbara")
    public ResponseEntity<?> ruralCheckDigitallySignedSatbara(
            @RequestParam("villageLgdCode") String villageLgdCode,
            @RequestParam("pin") String pin,
            @RequestParam(name = "pin1", required = false) String pin1,
            @RequestParam(name = "pin2", required = false) String pin2,
            @RequestParam(name = "pin3", required = false) String pin3,
            @RequestParam(name = "pin4", required = false) String pin4,
            @RequestParam(name = "pin5", required = false) String pin5,
            @RequestParam(name = "pin6", required = false) String pin6,
            @RequestParam(name = "pin7", required = false) String pin7,
            @RequestParam(name = "pin8", required = false) String pin8
    ) {
        return ruralCheckDigitallySignedSatbaraInternal(
                villageLgdCode, pin, pin1, pin2, pin3, pin4, pin5, pin6, pin7, pin8
        );
    }

    @PostMapping("/rural/check-digitally-signed-satbara")
    public ResponseEntity<?> ruralCheckDigitallySignedSatbaraPost(
            @RequestParam("villageLgdCode") String villageLgdCode,
            @RequestParam("pin") String pin,
            @RequestParam(name = "pin1", required = false) String pin1,
            @RequestParam(name = "pin2", required = false) String pin2,
            @RequestParam(name = "pin3", required = false) String pin3,
            @RequestParam(name = "pin4", required = false) String pin4,
            @RequestParam(name = "pin5", required = false) String pin5,
            @RequestParam(name = "pin6", required = false) String pin6,
            @RequestParam(name = "pin7", required = false) String pin7,
            @RequestParam(name = "pin8", required = false) String pin8
    ) {
        return ruralCheckDigitallySignedSatbaraInternal(
                villageLgdCode, pin, pin1, pin2, pin3, pin4, pin5, pin6, pin7, pin8
        );
    }

    /**
     * Digitally signed Satbara PDF view (same response shape as {@link #urbanNoticeNineView}).
     * Upstream: POST /eferfar/getSatbaraPDF (configurable).
     */
    @GetMapping({"/rural/satbara-pdf-view", "/rural/digitally-signed-satbara-pdf"})
    public ResponseEntity<?> ruralSatbaraPdfView(
            @RequestParam("villageLgdCode") String villageLgdCode,
            @RequestParam("pin") String pin,
            @RequestParam(name = "pin1", required = false) String pin1,
            @RequestParam(name = "pin2", required = false) String pin2,
            @RequestParam(name = "pin3", required = false) String pin3,
            @RequestParam(name = "pin4", required = false) String pin4,
            @RequestParam(name = "pin5", required = false) String pin5,
            @RequestParam(name = "pin6", required = false) String pin6,
            @RequestParam(name = "pin7", required = false) String pin7,
            @RequestParam(name = "pin8", required = false) String pin8
    ) {
        return ruralSatbaraPdfViewInternal(
                villageLgdCode, pin, pin1, pin2, pin3, pin4, pin5, pin6, pin7, pin8
        );
    }

    @PostMapping({"/rural/satbara-pdf-view", "/rural/digitally-signed-satbara-pdf"})
    public ResponseEntity<?> ruralSatbaraPdfViewPost(
            @RequestParam("villageLgdCode") String villageLgdCode,
            @RequestParam("pin") String pin,
            @RequestParam(name = "pin1", required = false) String pin1,
            @RequestParam(name = "pin2", required = false) String pin2,
            @RequestParam(name = "pin3", required = false) String pin3,
            @RequestParam(name = "pin4", required = false) String pin4,
            @RequestParam(name = "pin5", required = false) String pin5,
            @RequestParam(name = "pin6", required = false) String pin6,
            @RequestParam(name = "pin7", required = false) String pin7,
            @RequestParam(name = "pin8", required = false) String pin8
    ) {
        return ruralSatbaraPdfViewInternal(
                villageLgdCode, pin, pin1, pin2, pin3, pin4, pin5, pin6, pin7, pin8
        );
    }

    private ResponseEntity<?> ruralCheckDigitallySignedSatbaraInternal(
            String villageLgdCode,
            String pin,
            String pin1,
            String pin2,
            String pin3,
            String pin4,
            String pin5,
            String pin6,
            String pin7,
            String pin8
    ) {
        JsonNode res = landRecordsClient.postForm(
                "/eferfar/checkIfSatbaraIsDigitallySigned",
                satbaraPinForm(villageLgdCode, pin, pin1, pin2, pin3, pin4, pin5, pin6, pin7, pin8)
        );
        return unwrapData(res);
    }

    private ResponseEntity<?> ruralSatbaraPdfViewInternal(
            String villageLgdCode,
            String pin,
            String pin1,
            String pin2,
            String pin3,
            String pin4,
            String pin5,
            String pin6,
            String pin7,
            String pin8
    ) {
        Map<String, String> form = satbaraPinForm(
                villageLgdCode, pin, pin1, pin2, pin3, pin4, pin5, pin6, pin7, pin8
        );
        JsonNode res = landRecordsClient.postForm(satbaraPdfPath, form);
        if (isUpstreamHttpNotFound(res) && satbaraPdfFallbackPath != null && !satbaraPdfFallbackPath.isBlank()
                && !satbaraPdfFallbackPath.equals(satbaraPdfPath)) {
            res = landRecordsClient.postForm(satbaraPdfFallbackPath, form);
        }
        return unwrapSatbaraPdf(res);
    }

    private static Map<String, String> satbaraPinForm(
            String villageLgdCode,
            String pin,
            String pin1,
            String pin2,
            String pin3,
            String pin4,
            String pin5,
            String pin6,
            String pin7,
            String pin8
    ) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("lgd_code", villageLgdCode);
        form.put("pin", pin);
        form.put("pin1", emptyToBlank(pin1));
        form.put("pin2", emptyToBlank(pin2));
        form.put("pin3", emptyToBlank(pin3));
        form.put("pin4", emptyToBlank(pin4));
        form.put("pin5", emptyToBlank(pin5));
        form.put("pin6", emptyToBlank(pin6));
        form.put("pin7", emptyToBlank(pin7));
        form.put("pin8", emptyToBlank(pin8));
        return form;
    }

    private static String emptyToBlank(String value) {
        return value == null ? "" : value;
    }

    private static boolean isUpstreamHttpNotFound(JsonNode upstream) {
        return upstream != null && upstream.isObject()
                && upstream.has("httpStatus")
                && upstream.get("httpStatus").asInt() == 404;
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

    /**
     * Sub-CTS numbers for a village and parent CTS (upstream: POST /epcis/getSubCTSNoList).
     */
    @GetMapping("/urban/sub-cts-list")
    public ResponseEntity<?> urbanSubCtsList(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("ctsNo") String ctsNo
    ) {
        return urbanSubCtsListInternal(villageCode, ctsNo);
    }

    @PostMapping("/urban/sub-cts-list")
    public ResponseEntity<?> urbanSubCtsListPost(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("ctsNo") String ctsNo
    ) {
        return urbanSubCtsListInternal(villageCode, ctsNo);
    }

    private ResponseEntity<?> urbanSubCtsListInternal(String villageCode, String ctsNo) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getSubCTSNoList",
                Map.of("village_code", villageCode, "cts_no", ctsNo)
        );
        return unwrapData(res);
    }

    @GetMapping("/urban/mutation-detail")
    public ResponseEntity<?> urbanMutationDetail(@RequestParam("inwardNumber") String inwardNumber) {
        return urbanMutationDetailInternal(inwardNumber);
    }

    @PostMapping("/urban/mutation-detail")
    public ResponseEntity<?> urbanMutationDetailPost(@RequestParam("inwardNumber") String inwardNumber) {
        return urbanMutationDetailInternal(inwardNumber);
    }

    private ResponseEntity<?> urbanMutationDetailInternal(String inwardNumber) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getMutationDetailsByInwardNo",
                Map.of("inward_number", inwardNumber)
        );
        if (extractHttpStatus(res) == 404) {
            res = landRecordsClient.postForm(
                    "/epcis/getMutationDetailsByInwardNo",
                    Map.of("inward_no", inwardNumber)
            );
        }
        if (extractHttpStatus(res) == 404) {
            res = landRecordsClient.postForm(
                    "/epcis/getMutationDetailsByInwardNo",
                    Map.of("inwardNumber", inwardNumber)
            );
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/urban/mutations")
    public ResponseEntity<?> urbanMutations(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("ctsNo") String ctsNo
    ) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getMutationWithRegisterInwardNoFromvcAndCTSNo",
                Map.of("village_code", villageCode, "cts_no", ctsNo)
        );
        return unwrapData(res);
    }

    /**
     * Mutations with applicant details for a village + CTS (upstream: POST /epcis/getMutationWithApplicantBasedOnCTSNo).
     * Request: {@code village_code}, {@code cts_no}. Response {@code data} items include {@code inward_number}.
     */
    @GetMapping("/urban/mutations/applicant-by-cts")
    public ResponseEntity<?> urbanMutationsApplicantByCts(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("ctsNo") String ctsNo
    ) {
        return urbanMutationsApplicantByCtsInternal(villageCode, ctsNo);
    }

    @PostMapping("/urban/mutations/applicant-by-cts")
    public ResponseEntity<?> urbanMutationsApplicantByCtsPost(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("ctsNo") String ctsNo
    ) {
        return urbanMutationsApplicantByCtsInternal(villageCode, ctsNo);
    }

    private ResponseEntity<?> urbanMutationsApplicantByCtsInternal(String villageCode, String ctsNo) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getMutationWithApplicantBasedOnCTSNo",
                Map.of("village_code", villageCode, "cts_no", ctsNo)
        );
        return unwrapData(res);
    }

    /**
     * Same upstream as {@link #urbanMutationsApplicantByCtsInternal}, but returns only inward number(s) from {@code data}.
     */
    @GetMapping("/urban/mutations/applicant-by-cts/inward-numbers")
    public ResponseEntity<?> urbanMutationsApplicantByCtsInwardNumbers(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("ctsNo") String ctsNo
    ) {
        return urbanMutationsApplicantByCtsInwardNumbersInternal(villageCode, ctsNo);
    }

    @PostMapping("/urban/mutations/applicant-by-cts/inward-numbers")
    public ResponseEntity<?> urbanMutationsApplicantByCtsInwardNumbersPost(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("ctsNo") String ctsNo
    ) {
        return urbanMutationsApplicantByCtsInwardNumbersInternal(villageCode, ctsNo);
    }

    private ResponseEntity<?> urbanMutationsApplicantByCtsInwardNumbersInternal(String villageCode, String ctsNo) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getMutationWithApplicantBasedOnCTSNo",
                Map.of("village_code", villageCode, "cts_no", ctsNo)
        );
        List<String> inwardNumbers = extractInwardNumbersFromUpstreamData(res);
        ObjectNode body = JsonNodeFactory.instance.objectNode();
        ArrayNode arr = JsonNodeFactory.instance.arrayNode();
        for (String n : inwardNumbers) {
            arr.add(n);
        }
        body.set("inwardNumbers", arr);
        if (!inwardNumbers.isEmpty()) {
            body.put("inwardNumber", inwardNumbers.get(0));
        } else {
            body.putNull("inwardNumber");
        }
        return ResponseEntity.ok(body);
    }

    private static List<String> extractInwardNumbersFromUpstreamData(JsonNode upstream) {
        List<String> out = new ArrayList<>();
        if (upstream == null || !upstream.isObject()) {
            return out;
        }
        JsonNode data = upstream.get("data");
        if (data == null || data.isNull()) {
            return out;
        }
        if (data.isArray()) {
            for (JsonNode item : data) {
                addInwardNumberIfPresent(item, out);
            }
        } else {
            addInwardNumberIfPresent(data, out);
        }
        return out;
    }

    private static void addInwardNumberIfPresent(JsonNode item, List<String> out) {
        if (item == null || !item.isObject()) {
            return;
        }
        JsonNode in = item.get("inward_number");
        if (in != null && in.isTextual()) {
            String s = in.asText().trim();
            if (!s.isEmpty()) {
                out.add(s);
            }
        }
    }

    @GetMapping("/urban/mutation-types")
    public ResponseEntity<?> urbanMutationTypes() {
        JsonNode res = landRecordsClient.postForm("/epcis/getAllMutationTypeList", null);
        return unwrapData(res);
    }

    /**
     * Generic property details lookup (upstream: POST /epcis/getPropertyDetails).
     * Accepts request params as-is from frontend so UI can pass evolving key names.
     */
    @GetMapping("/urban/property-details")
    public ResponseEntity<?> urbanPropertyDetailsGet(@RequestParam Map<String, String> params) {
        return urbanPropertyDetailsInternal(params);
    }

    @PostMapping("/urban/property-details")
    public ResponseEntity<?> urbanPropertyDetailsPost(@RequestParam Map<String, String> params) {
        return urbanPropertyDetailsInternal(params);
    }

    private ResponseEntity<?> urbanPropertyDetailsInternal(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "At least one request parameter is required."));
        }
        JsonNode res = landRecordsClient.postForm("/epcis/getPropertyDetails", params);
        return unwrapData(res);
    }

    /**
     * Application basic details by mutation type (upstream differs from applicant list below).
     */
    @GetMapping("/urban/mutations/by-type")
    public ResponseEntity<?> urbanMutationsByType(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("mutationTypeCode") String mutationTypeCode
    ) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getMutationWithApplicationBasicDtlsOnMutationType",
                Map.of("village_code", villageCode, "mutation_type_code", mutationTypeCode)
        );
        return unwrapData(res);
    }

    /**
     * Mutations with applicant details by village + mutation type (upstream: POST /epcis/getMutationWithApplicantBasedOnMutationType).
     * Form: {@code village_code}, {@code mutation_type_code}.
     */
    @GetMapping("/urban/mutations/applicant-by-type")
    public ResponseEntity<?> urbanMutationsApplicantByType(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("mutationTypeCode") String mutationTypeCode
    ) {
        return urbanMutationsApplicantByTypeInternal(villageCode, mutationTypeCode);
    }

    @PostMapping("/urban/mutations/applicant-by-type")
    public ResponseEntity<?> urbanMutationsApplicantByTypePost(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("mutationTypeCode") String mutationTypeCode
    ) {
        return urbanMutationsApplicantByTypeInternal(villageCode, mutationTypeCode);
    }

    private ResponseEntity<?> urbanMutationsApplicantByTypeInternal(
            String villageCode,
            String mutationTypeCode
    ) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getMutationWithApplicantBasedOnMutationType",
                Map.of("village_code", villageCode, "mutation_type_code", mutationTypeCode)
        );
        return unwrapData(res);
    }

    @GetMapping("/urban/mutations/applicant-by-type/inward-numbers")
    public ResponseEntity<?> urbanMutationsApplicantByTypeInwardNumbers(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("mutationTypeCode") String mutationTypeCode
    ) {
        return urbanMutationsApplicantByTypeInwardNumbersInternal(villageCode, mutationTypeCode);
    }

    @PostMapping("/urban/mutations/applicant-by-type/inward-numbers")
    public ResponseEntity<?> urbanMutationsApplicantByTypeInwardNumbersPost(
            @RequestParam("villageCode") String villageCode,
            @RequestParam("mutationTypeCode") String mutationTypeCode
    ) {
        return urbanMutationsApplicantByTypeInwardNumbersInternal(villageCode, mutationTypeCode);
    }

    private ResponseEntity<?> urbanMutationsApplicantByTypeInwardNumbersInternal(
            String villageCode,
            String mutationTypeCode
    ) {
        JsonNode res = landRecordsClient.postForm(
                "/epcis/getMutationWithApplicantBasedOnMutationType",
                Map.of("village_code", villageCode, "mutation_type_code", mutationTypeCode)
        );
        List<String> inwardNumbers = extractInwardNumbersFromUpstreamData(res);
        ObjectNode body = JsonNodeFactory.instance.objectNode();
        ArrayNode arr = JsonNodeFactory.instance.arrayNode();
        for (String n : inwardNumbers) {
            arr.add(n);
        }
        body.set("inwardNumbers", arr);
        if (!inwardNumbers.isEmpty()) {
            body.put("inwardNumber", inwardNumbers.get(0));
        } else {
            body.putNull("inwardNumber");
        }
        return ResponseEntity.ok(body);
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
    /**
     * Normalizes G2B GetLandDetailSurvyWise response to {@code Land_Detail} array when present.
     */
    private static ResponseEntity<?> unwrapG2bLandDetail(JsonNode upstream) {
        int httpStatus = extractHttpStatus(upstream);
        if (upstream != null && upstream.isObject()) {
            JsonNode landDetail = upstream.get("Land_Detail");
            if (landDetail != null && !landDetail.isNull()) {
                if (landDetail.isArray() && landDetail.size() == 0 && upstream.has("message")) {
                    String msg = upstream.get("message").asText("");
                    if (msg.toLowerCase().contains("not found") || msg.toLowerCase().startsWith("error")) {
                        return ResponseEntity.status(404).body(Map.of("error", msg.trim()));
                    }
                }
                return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(landDetail);
            }
            JsonNode message = upstream.get("message");
            if (message != null && message.isTextual()) {
                String msg = message.asText().trim();
                if (msg.toLowerCase().contains("not found") || msg.toLowerCase().startsWith("error")) {
                    return ResponseEntity.status(404).body(Map.of("error", msg));
                }
            }
            if (httpStatus >= 400) {
                return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(upstream);
            }
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(upstream);
    }

    private static ResponseEntity<?> unwrapData(JsonNode upstream) {
        int httpStatus = 200;
        if (upstream != null && upstream.isObject()) {
            JsonNode hs = upstream.get("httpStatus");
            if (hs != null && hs.canConvertToInt()) {
                httpStatus = hs.asInt(200);
            }
            if (httpStatus >= 400) {
                return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(upstream);
            }
            JsonNode data = upstream.get("data");
            if (data != null && !data.isNull()) {
                return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(moveSearchFieldFirst(data));
            }
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(upstream);
    }

    private static int extractHttpStatus(JsonNode upstream) {
        if (upstream != null && upstream.isObject()) {
            JsonNode hs = upstream.get("httpStatus");
            if (hs != null && hs.canConvertToInt()) {
                return hs.asInt(200);
            }
            JsonNode status = upstream.get("status");
            if (status != null && status.canConvertToInt()) {
                return status.asInt(200);
            }
        }
        return 200;
    }

    /**
     * Uses upstream JSON {@code status} when it signals an error (400/500), even if HTTP was 200.
     */
    private static int extractEffectiveStatus(JsonNode upstream) {
        if (upstream != null && upstream.isObject()) {
            JsonNode status = upstream.get("status");
            if (status != null && status.canConvertToInt()) {
                int s = status.asInt();
                if (s >= 400) {
                    return s;
                }
            }
        }
        return extractHttpStatus(upstream);
    }

    /**
     * Satbara PDF is consumed directly by UI (same pattern as Notice 9).
     * Normalizes decrypted payload to {@code { url }} or {@code { type, mimeType, base64, dataUrl }}.
     */
    private static ResponseEntity<?> unwrapSatbaraPdf(JsonNode upstream) {
        int httpStatus = extractHttpStatus(upstream);
        int effectiveStatus = extractEffectiveStatus(upstream);
        if (upstream != null && upstream.isObject()) {
            if (effectiveStatus >= 400) {
                JsonNode message = upstream.get("message");
                if (message != null && message.isTextual()) {
                    return ResponseEntity.status(HttpStatusCode.valueOf(effectiveStatus))
                            .body(Map.of("error", message.asText().trim()));
                }
                return ResponseEntity.status(HttpStatusCode.valueOf(effectiveStatus)).body(upstream);
            }
            if (httpStatus >= 400) {
                return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(upstream);
            }

            JsonNode data = upstream.get("data");
            JsonNode normalized = normalizeSatbaraPdfData(data);
            if (normalized != null) {
                return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(normalized);
            }
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(upstream);
    }

    private static JsonNode normalizeSatbaraPdfData(JsonNode data) {
        if (data == null || data.isNull()) {
            return null;
        }

        if (data.isTextual()) {
            String text = normalizeQuotedText(data.asText());
            String sanitized = sanitizeBase64Payload(text);
            if (looksLikeBase64Payload(sanitized)) {
                ObjectNode out = JsonNodeFactory.instance.objectNode();
                String mimeType = detectMimeTypeFromBase64(sanitized);
                out.put("type", "base64-file");
                out.put("mimeType", mimeType);
                out.put("base64", sanitized);
                if (mimeType.startsWith("image/") || "application/pdf".equals(mimeType)) {
                    out.put("dataUrl", "data:" + mimeType + ";base64," + sanitized);
                }
                return out;
            }
            ObjectNode out = JsonNodeFactory.instance.objectNode();
            out.put("url", text);
            return out;
        }

        if (data.isObject()) {
            JsonNode url = firstNonBlankText(
                    data.get("url"),
                    data.get("pdfUrl"),
                    data.get("satbaraUrl"),
                    data.get("satbara_pdf_url"),
                    data.get("viewUrl"),
                    data.get("link")
            );
            if (url != null) {
                ObjectNode out = JsonNodeFactory.instance.objectNode();
                out.put("url", url.asText());
                return out;
            }
            JsonNode embedded = firstNonBlankText(
                    data.get("base64"),
                    data.get("pdf"),
                    data.get("pdfBase64"),
                    data.get("satbaraPdf"),
                    data.get("satbara_pdf"),
                    data.get("file"),
                    data.get("data"),
                    data.get("content")
            );
            if (embedded != null) {
                return normalizeSatbaraPdfData(embedded);
            }
            return moveSearchFieldFirst(data);
        }

        if (data.isArray() && data.size() > 0) {
            JsonNode first = data.get(0);
            JsonNode normalizedFirst = normalizeSatbaraPdfData(first);
            if (normalizedFirst != null) {
                return normalizedFirst;
            }
        }

        return data;
    }

    /** Strip whitespace and Mahabhumi line-break escapes so base64 decodes in browsers. */
    private static String sanitizeBase64Payload(String value) {
        if (value == null) {
            return "";
        }
        return value.trim()
                .replace("\\r\\n", "")
                .replace("\\n", "")
                .replace("\\r", "")
                .replace("\r\n", "")
                .replace("\n", "")
                .replace("\r", "")
                .replaceAll("\\s+", "");
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
            if (httpStatus >= 400) {
                return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(upstream);
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
            String sanitized = sanitizeBase64Payload(text);
            if (looksLikeBase64Payload(sanitized)) {
                ObjectNode out = JsonNodeFactory.instance.objectNode();
                String mimeType = detectMimeTypeFromBase64(sanitized);
                out.put("type", "base64-file");
                out.put("mimeType", mimeType);
                out.put("base64", sanitized);
                if (mimeType.startsWith("image/") || "application/pdf".equals(mimeType)) {
                    out.put("dataUrl", "data:" + mimeType + ";base64," + sanitized);
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
        String t = sanitizeBase64Payload(s);
        if (t.length() < 64) return false;
        if (t.startsWith("JVBERi0")) return true;
        if (t.length() % 4 != 0) return false;
        return t.matches("^[A-Za-z0-9+/=]+$");
    }

    private static String detectMimeTypeFromBase64(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(sanitizeBase64Payload(base64));
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

    @GetMapping("/rural/satbara-pdf-view-debug")
    public ResponseEntity<?> ruralSatbaraPdfViewDebug(
            @RequestParam("villageLgdCode") String villageLgdCode,
            @RequestParam("pin") String pin,
            @RequestParam(name = "pin1", required = false) String pin1,
            @RequestParam(name = "pin2", required = false) String pin2,
            @RequestParam(name = "pin3", required = false) String pin3,
            @RequestParam(name = "pin4", required = false) String pin4,
            @RequestParam(name = "pin5", required = false) String pin5,
            @RequestParam(name = "pin6", required = false) String pin6,
            @RequestParam(name = "pin7", required = false) String pin7,
            @RequestParam(name = "pin8", required = false) String pin8
    ) {
        Map<String, String> form = satbaraPinForm(
                villageLgdCode, pin, pin1, pin2, pin3, pin4, pin5, pin6, pin7, pin8
        );
        JsonNode rawRes = landRecordsClient.postFormRaw(satbaraPdfPath, form);
        JsonNode decodedRes = landRecordsClient.postForm(satbaraPdfPath, form);

        ObjectNode out = JsonNodeFactory.instance.objectNode();
        out.set("rawUpstream", rawRes);
        out.set("decodedUpstream", decodedRes);
        out.put("upstreamPath", satbaraPdfPath);

        JsonNode rawData = rawRes != null && rawRes.isObject() ? rawRes.get("data") : null;
        if (rawData != null && rawData.isTextual()) {
            out.set("decryptDebug", landRecordsClient.debugDecryptData(rawData.asText()));
        }

        JsonNode decodedData = decodedRes != null && decodedRes.isObject() ? decodedRes.get("data") : null;
        JsonNode normalized = normalizeSatbaraPdfData(decodedData);
        if (normalized != null) {
            out.set("normalizedForUi", normalized);
        }

        return ResponseEntity.ok(out);
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

