package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.BoundaryMasterResponse;
import com.maharashtra.rccms.dto.OfficeResponse;
import com.maharashtra.rccms.dto.PincodeLookupResponse;
import com.maharashtra.rccms.model.master.Department;
import com.maharashtra.rccms.model.master.District;
import com.maharashtra.rccms.model.master.Division;
import com.maharashtra.rccms.model.master.Office;
import com.maharashtra.rccms.model.master.OfficeType;
import com.maharashtra.rccms.model.master.State;
import com.maharashtra.rccms.model.master.Subdistrict;
import com.maharashtra.rccms.model.master.Taluka;
import com.maharashtra.rccms.repository.DistrictRepository;
import com.maharashtra.rccms.repository.OfficeRepository;
import com.maharashtra.rccms.repository.SubdistrictRepository;
import com.maharashtra.rccms.repository.TalukaRepository;
import com.maharashtra.rccms.service.PincodeLookupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Read-only lookup APIs for logged-in users (advocate/party/etc.)
 * to build filing forms: district -> subdistrict -> taluka -> office.
 */
@RestController
@RequestMapping("/api/lookups")
@SuppressWarnings("null")
public class LocationLookupController {

    private final DistrictRepository districtRepository;
    private final SubdistrictRepository subdistrictRepository;
    private final TalukaRepository talukaRepository;
    private final OfficeRepository officeRepository;
    private final PincodeLookupService pincodeLookupService;

    public LocationLookupController(
            DistrictRepository districtRepository,
            SubdistrictRepository subdistrictRepository,
            TalukaRepository talukaRepository,
            OfficeRepository officeRepository,
            PincodeLookupService pincodeLookupService
    ) {
        this.districtRepository = districtRepository;
        this.subdistrictRepository = subdistrictRepository;
        this.talukaRepository = talukaRepository;
        this.officeRepository = officeRepository;
        this.pincodeLookupService = pincodeLookupService;
    }

    /**
     * District dropdown.
     * Example: /api/lookups/districts?stateId=1 (optional divisionId).
     */
    @GetMapping("/districts")
    public ResponseEntity<?> districts(
            @RequestParam("stateId") Long stateId,
            @RequestParam(name = "divisionId", required = false) Long divisionId
    ) {
        List<BoundaryMasterResponse> items = (divisionId == null
                ? districtRepository.findByStateIdOrderByNameAsc(stateId)
                : districtRepository.findByStateIdAndDivisionIdOrderByNameAsc(stateId, divisionId))
                .stream()
                .map(LocationLookupController::toDistrictResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    /**
     * Subdistrict dropdown.
     * Example: /api/lookups/subdistricts?districtId=10
     */
    @GetMapping("/subdistricts")
    public ResponseEntity<?> subdistricts(@RequestParam("districtId") Long districtId) {
        List<BoundaryMasterResponse> items = subdistrictRepository.findByDistrictIdOrderByNameAsc(districtId).stream()
                .map(LocationLookupController::toSubdistrictResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    /**
     * Taluka dropdown.
     * Example: /api/lookups/talukas?districtId=10&subdistrictId=3 (subdistrictId optional)
     */
    @GetMapping("/talukas")
    public ResponseEntity<?> talukas(
            @RequestParam("districtId") Long districtId,
            @RequestParam(name = "subdistrictId", required = false) Long subdistrictId
    ) {
        List<BoundaryMasterResponse> items = (subdistrictId == null
                ? talukaRepository.findByDistrictIdOrderByNameAsc(districtId)
                : talukaRepository.findByDistrictIdAndSubdistrictIdOrderByNameAsc(districtId, subdistrictId))
                .stream()
                .map(LocationLookupController::toTalukaResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    /**
     * Office dropdown by boundary selection.
     * Example: /api/lookups/offices?level=DISTRICT&locationId=10
     * Optional: departmentId=...
     */
    @GetMapping("/offices")
    public ResponseEntity<?> offices(
            @RequestParam("level") String level,
            @RequestParam("locationId") Long locationId,
            @RequestParam(name = "departmentId", required = false) Long departmentId
    ) {
        String normalizedLevel = level == null ? null : level.trim().toUpperCase();
        List<Office> offices = (departmentId == null)
                ? officeRepository.findByLevelAndLocationIdOrderByNameAsc(normalizedLevel, locationId)
                : officeRepository.findByDepartmentIdAndLevelAndLocationIdOrderByNameAsc(departmentId, normalizedLevel, locationId);

        List<OfficeResponse> items = offices.stream().map(LocationLookupController::toOfficeResponse).toList();
        return ResponseEntity.ok(items);
    }

    /**
     * Pincode-based address lookup for applicant/respondent forms.
     * Example: /api/lookups/pincode-details?pincode=413402
     */
    @GetMapping("/pincode-details")
    public ResponseEntity<?> pincodeDetails(@RequestParam("pincode") String pincode) {
        try {
            PincodeLookupResponse response = pincodeLookupService.lookupByPincode(pincode);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
        }
    }

    private static BoundaryMasterResponse toDistrictResponse(District d) {
        State state = d.getState();
        Division division = d.getDivision();
        return new BoundaryMasterResponse(
                d.getId(),
                d.getName(),
                d.getLocalName(),
                d.getLgdCode(),
                state == null ? null : state.getId(),
                division == null ? null : division.getId(),
                d.getId(),
                null,
                null
        );
    }

    private static BoundaryMasterResponse toSubdistrictResponse(Subdistrict s) {
        District d = s.getDistrict();
        State state = d == null ? null : d.getState();
        Division division = d == null ? null : d.getDivision();
        return new BoundaryMasterResponse(
                s.getId(),
                s.getName(),
                s.getLocalName(),
                s.getLgdCode(),
                state == null ? null : state.getId(),
                division == null ? null : division.getId(),
                d == null ? null : d.getId(),
                s.getId(),
                null
        );
    }

    private static BoundaryMasterResponse toTalukaResponse(Taluka t) {
        District d = t.getDistrict();
        Subdistrict sd = t.getSubdistrict();
        State state = d == null ? null : d.getState();
        Division division = d == null ? null : d.getDivision();
        return new BoundaryMasterResponse(
                t.getId(),
                t.getName(),
                t.getLocalName(),
                t.getLgdCode(),
                state == null ? null : state.getId(),
                division == null ? null : division.getId(),
                d == null ? null : d.getId(),
                sd == null ? null : sd.getId(),
                t.getId()
        );
    }

    private static OfficeResponse toOfficeResponse(Office o) {
        Department d = o.getDepartment();
        OfficeType ot = o.getOfficeType();
        return new OfficeResponse(
                o.getId(),
                d == null ? null : d.getId(),
                d == null ? null : d.getName(),
                d == null ? null : d.getLocalName(),
                ot == null ? null : ot.getId(),
                ot == null ? null : ot.getName(),
                ot == null ? null : ot.getLocalName(),
                o.getLevel(),
                o.getLocationId(),
                o.getName(),
                o.getLocalName(),
                o.getShortName(),
                o.getShortNameLocal()
        );
    }
}

