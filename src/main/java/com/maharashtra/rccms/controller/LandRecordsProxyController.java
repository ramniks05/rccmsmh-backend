package com.maharashtra.rccms.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.maharashtra.rccms.service.LandRecordsClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(data);
            }
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(httpStatus)).body(upstream);
    }
}

