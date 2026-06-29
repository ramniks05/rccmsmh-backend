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
import com.maharashtra.rccms.model.master.Village;
import com.maharashtra.rccms.repository.DistrictRepository;
import com.maharashtra.rccms.repository.DivisionRepository;
import com.maharashtra.rccms.repository.OfficeRepository;
import com.maharashtra.rccms.repository.OfficeTypeRepository;
import com.maharashtra.rccms.repository.TalukaRepository;
import com.maharashtra.rccms.repository.VillageRepository;
import com.maharashtra.rccms.service.CoveredStateService;
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
 * Read-only boundary lookups. Hierarchy: state → division → district → taluka → village.
 */
@RestController
@RequestMapping("/api/lookups")
@SuppressWarnings("null")
public class LocationLookupController {

    private final DivisionRepository divisionRepository;
    private final DistrictRepository districtRepository;
    private final TalukaRepository talukaRepository;
    private final VillageRepository villageRepository;
    private final OfficeRepository officeRepository;
    private final OfficeTypeRepository officeTypeRepository;
    private final PincodeLookupService pincodeLookupService;
    private final CoveredStateService coveredStateService;

    public LocationLookupController(
            DivisionRepository divisionRepository,
            DistrictRepository districtRepository,
            TalukaRepository talukaRepository,
            VillageRepository villageRepository,
            OfficeRepository officeRepository,
            OfficeTypeRepository officeTypeRepository,
            PincodeLookupService pincodeLookupService,
            CoveredStateService coveredStateService
    ) {
        this.divisionRepository = divisionRepository;
        this.districtRepository = districtRepository;
        this.talukaRepository = talukaRepository;
        this.villageRepository = villageRepository;
        this.officeRepository = officeRepository;
        this.officeTypeRepository = officeTypeRepository;
        this.pincodeLookupService = pincodeLookupService;
        this.coveredStateService = coveredStateService;
    }

    /** Example: GET /api/lookups/states */
    @GetMapping("/states")
    public ResponseEntity<?> states() {
        List<BoundaryMasterResponse> items = coveredStateService.listStatesForDropdown().stream()
                .map(LocationLookupController::toStateResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    /** Example: GET /api/lookups/divisions?stateId=1 */
    @GetMapping("/divisions")
    public ResponseEntity<?> divisions(@RequestParam("stateId") Long stateId) {
        try {
            coveredStateService.requireCoveredStateById(stateId);
            List<BoundaryMasterResponse> items = divisionRepository.findByStateIdOrderByNameAsc(stateId).stream()
                    .map(LocationLookupController::toDivisionResponse)
                    .toList();
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * District dropdown filtered by state and optional division code.
     * Example: GET /api/lookups/districts?stateId=1&divisionCode=1
     */
    @GetMapping("/districts")
    public ResponseEntity<?> districts(
            @RequestParam("stateId") Long stateId,
            @RequestParam(name = "divisionCode", required = false) String divisionCode
    ) {
        try {
            coveredStateService.requireCoveredStateById(stateId);
            String normalizedDivisionCode = normalizeDivisionCode(divisionCode);
            List<District> districts = normalizedDivisionCode == null
                    ? districtRepository.findByStateIdOrderByNameAsc(stateId)
                    : districtRepository.findByStateIdAndDivisionCodeOrderByNameAsc(stateId, normalizedDivisionCode);
            List<BoundaryMasterResponse> items = districts.stream()
                    .map(LocationLookupController::toDistrictResponse)
                    .toList();
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /** Example: GET /api/lookups/talukas?districtId=10 */
    @GetMapping("/talukas")
    public ResponseEntity<?> talukas(@RequestParam("districtId") Long districtId) {
        List<BoundaryMasterResponse> items = talukaRepository.findByDistrictIdOrderByNameAsc(districtId).stream()
                .map(LocationLookupController::toTalukaResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    /**
     * Village dropdown under a taluka or district.
     * Examples:
     * GET /api/lookups/villages?talukaId=100
     * GET /api/lookups/villages?districtId=10
     */
    @GetMapping("/villages")
    public ResponseEntity<?> villages(
            @RequestParam(name = "talukaId", required = false) Long talukaId,
            @RequestParam(name = "districtId", required = false) Long districtId
    ) {
        try {
            if (talukaId == null && districtId == null) {
                throw new IllegalArgumentException("talukaId or districtId is required.");
            }
            List<BoundaryMasterResponse> items = loadVillages(talukaId, districtId).stream()
                    .map(LocationLookupController::toVillageResponse)
                    .toList();
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private List<Village> loadVillages(Long talukaId, Long districtId) {
        if (talukaId != null) {
            return villageRepository.findByTalukaIdOrderByNameAsc(talukaId);
        }
        return villageRepository.findByTaluka_DistrictIdOrderByNameAsc(districtId);
    }

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

            List<OfficeResponse> items = offices.stream()
                    .filter(this::officeInCoveredState)
                    .map(OfficeResponse::from)
                    .toList();
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

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

    private boolean officeInCoveredState(Office office) {
        State state = office.getState();
        if (state != null) {
            return coveredStateService.isCoveredStateEntity(state);
        }
        String stateLgdCode = office.getStateLgdCode();
        return stateLgdCode == null || coveredStateService.matchesCoveredStateLgdCode(stateLgdCode);
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

    private static String normalizeDivisionCode(String divisionCode) {
        if (divisionCode == null) {
            return null;
        }
        String trimmed = divisionCode.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
                null
        );
    }

    private static BoundaryMasterResponse toDivisionResponse(Division division) {
        State state = division.getState();
        return new BoundaryMasterResponse(
                division.getId(),
                division.getName(),
                division.getLocalName(),
                null,
                null,
                division.getDivisionCode(),
                state == null ? null : state.getId(),
                division.getId(),
                null,
                null,
                null,
                null
        );
    }

    private static BoundaryMasterResponse toDistrictResponse(District district) {
        State state = district.getState();
        return new BoundaryMasterResponse(
                district.getId(),
                district.getName(),
                district.getLocalName(),
                district.getLgdCode(),
                null,
                district.getDivisionCode(),
                state == null ? null : state.getId(),
                null,
                district.getId(),
                null,
                null,
                null
        );
    }

    private static BoundaryMasterResponse toTalukaResponse(Taluka taluka) {
        District district = taluka.getDistrict();
        State state = district == null ? null : district.getState();
        String districtLgdCode = taluka.getDistrictLgdCode();
        if (districtLgdCode == null && district != null) {
            districtLgdCode = district.getLgdCode();
        }
        return new BoundaryMasterResponse(
                taluka.getId(),
                taluka.getName(),
                taluka.getLocalName(),
                taluka.getLgdCode(),
                null,
                district == null ? null : district.getDivisionCode(),
                state == null ? null : state.getId(),
                null,
                district == null ? null : district.getId(),
                districtLgdCode,
                taluka.getId(),
                null
        );
    }

    private static BoundaryMasterResponse toVillageResponse(Village village) {
        Taluka taluka = village.getTaluka();
        District district = taluka == null ? null : taluka.getDistrict();
        State state = district == null ? null : district.getState();
        String talukaLgdCode = village.getTalukaLgdCode();
        if (talukaLgdCode == null && taluka != null) {
            talukaLgdCode = taluka.getLgdCode();
        }
        return new BoundaryMasterResponse(
                village.getId(),
                village.getName(),
                village.getLocalName(),
                village.getLgdCode(),
                null,
                district == null ? null : district.getDivisionCode(),
                state == null ? null : state.getId(),
                null,
                district == null ? null : district.getId(),
                null,
                taluka == null ? null : taluka.getId(),
                talukaLgdCode
        );
    }
}
