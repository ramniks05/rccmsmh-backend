package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.BoundaryMasterResponse;
import com.maharashtra.rccms.dto.OfficeResponse;
import com.maharashtra.rccms.dto.PincodeLookupResponse;
import com.maharashtra.rccms.model.master.BoundaryLevel;
import com.maharashtra.rccms.model.master.District;
import com.maharashtra.rccms.model.master.Division;
import com.maharashtra.rccms.model.master.Office;
import com.maharashtra.rccms.model.master.OfficeType;
import com.maharashtra.rccms.model.master.State;
import com.maharashtra.rccms.model.master.Taluka;
import com.maharashtra.rccms.repository.DistrictRepository;
import com.maharashtra.rccms.repository.OfficeRepository;
import com.maharashtra.rccms.repository.OfficeTypeRepository;
import com.maharashtra.rccms.repository.StateRepository;
import com.maharashtra.rccms.repository.TalukaRepository;
import com.maharashtra.rccms.service.PincodeLookupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Read-only boundary lookups. States, districts, and pincode are public
 * (registration/profile forms). Other lookups require login.
 */
@RestController
@RequestMapping("/api/lookups")
@SuppressWarnings("null")
public class LocationLookupController {

    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final TalukaRepository talukaRepository;
    private final OfficeRepository officeRepository;
    private final OfficeTypeRepository officeTypeRepository;
    private final PincodeLookupService pincodeLookupService;

    public LocationLookupController(
            StateRepository stateRepository,
            DistrictRepository districtRepository,
            TalukaRepository talukaRepository,
            OfficeRepository officeRepository,
            OfficeTypeRepository officeTypeRepository,
            PincodeLookupService pincodeLookupService
    ) {
        this.stateRepository = stateRepository;
        this.districtRepository = districtRepository;
        this.talukaRepository = talukaRepository;
        this.officeRepository = officeRepository;
        this.officeTypeRepository = officeTypeRepository;
        this.pincodeLookupService = pincodeLookupService;
    }

    /**
     * State dropdown (public — used on registration and profile forms).
     * Example: GET /api/lookups/states
     */
    @GetMapping("/states")
    public ResponseEntity<?> states() {
        List<BoundaryMasterResponse> items = stateRepository.findAll().stream()
                .sorted(Comparator.comparing(State::getName, String.CASE_INSENSITIVE_ORDER))
                .map(LocationLookupController::toStateResponse)
                .toList();
        return ResponseEntity.ok(items);
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
     * Taluka dropdown under a district.
     * Example: /api/lookups/talukas?districtId=10
     */
    @GetMapping("/talukas")
    public ResponseEntity<?> talukas(@RequestParam("districtId") Long districtId) {
        List<BoundaryMasterResponse> items = talukaRepository.findByDistrictIdOrderByNameAsc(districtId).stream()
                .map(LocationLookupController::toTalukaResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    /**
     * Office dropdown by office type.
     * Preferred: /api/lookups/offices?officeTypeId=4
     * Legacy: /api/lookups/offices?boundaryLevel=DISTRICT
     * Optional: departmentId=...
     */
    @GetMapping("/offices")
    public ResponseEntity<?> offices(
            @RequestParam(name = "officeTypeId", required = false) Long officeTypeId,
            @RequestParam(name = "boundaryLevel", required = false) String boundaryLevel,
            @RequestParam(name = "level", required = false) String level,
            @RequestParam(name = "departmentId", required = false) Long departmentId
    ) {
        try {
            List<Office> offices;
            if (officeTypeId != null) {
                offices = (departmentId == null)
                        ? officeRepository.findByOfficeTypeIdOrderByNameAsc(officeTypeId)
                        : officeRepository.findByDepartmentIdAndOfficeTypeIdOrderByNameAsc(
                                departmentId, officeTypeId);
            } else {
                String normalizedLevel = resolveOfficeBoundaryLevel(null, boundaryLevel, level);
                offices = (departmentId == null)
                        ? officeRepository.findByOfficeType_BoundaryLevelOrderByNameAsc(normalizedLevel)
                        : officeRepository.findByDepartmentIdAndOfficeType_BoundaryLevelOrderByNameAsc(
                                departmentId, normalizedLevel);
            }

            List<OfficeResponse> items = offices.stream().map(OfficeResponse::from).toList();
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private String resolveOfficeBoundaryLevel(Long officeTypeId, String boundaryLevel, String legacyLevel) {
        if (officeTypeId != null) {
            OfficeType officeType = officeTypeRepository.findById(officeTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid officeTypeId"));
            if (officeType.getBoundaryLevel() == null || officeType.getBoundaryLevel().isBlank()) {
                throw new IllegalArgumentException("Selected office type has no boundaryLevel configured");
            }
            return BoundaryLevel.normalize(officeType.getBoundaryLevel());
        }
        String raw = boundaryLevel != null && !boundaryLevel.isBlank() ? boundaryLevel : legacyLevel;
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("officeTypeId or boundaryLevel is required");
        }
        return BoundaryLevel.normalize(raw);
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

    private static BoundaryMasterResponse toStateResponse(State state) {
        return new BoundaryMasterResponse(
                state.getId(),
                state.getName(),
                state.getLocalName(),
                state.getLgdCode(),
                state.getStateOrUT(),
                null,
                state.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private static BoundaryMasterResponse toDistrictResponse(District d) {
        State state = d.getState();
        Division division = d.getDivision();
        return new BoundaryMasterResponse(
                d.getId(),
                d.getName(),
                d.getLocalName(),
                d.getLgdCode(),
                null,
                division == null ? null : division.getDivisionCode(),
                state == null ? null : state.getId(),
                division == null ? null : division.getId(),
                d.getId(),
                null,
                null,
                null,
                null,
                null
        );
    }

    private static BoundaryMasterResponse toTalukaResponse(Taluka t) {
        District d = t.getDistrict();
        State state = d == null ? null : d.getState();
        Division division = d == null ? null : d.getDivision();
        String districtLgdCode = t.getDistrictLgdCode();
        if (districtLgdCode == null && d != null) {
            districtLgdCode = d.getLgdCode();
        }
        return new BoundaryMasterResponse(
                t.getId(),
                t.getName(),
                t.getLocalName(),
                t.getLgdCode(),
                null,
                division == null ? null : division.getDivisionCode(),
                state == null ? null : state.getId(),
                division == null ? null : division.getId(),
                d == null ? null : d.getId(),
                districtLgdCode,
                null,
                null,
                t.getId(),
                null
        );
    }
}
