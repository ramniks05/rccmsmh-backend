package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.OfficeResponse;
import com.maharashtra.rccms.model.master.BoundaryLevel;
import com.maharashtra.rccms.model.master.District;
import com.maharashtra.rccms.model.master.Division;
import com.maharashtra.rccms.model.master.Office;
import com.maharashtra.rccms.model.master.OfficeType;
import com.maharashtra.rccms.model.master.State;
import com.maharashtra.rccms.model.master.Taluka;
import com.maharashtra.rccms.repository.OfficeRepository;
import com.maharashtra.rccms.repository.OfficeTypeRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class OfficeLookupService {

    private final OfficeRepository officeRepository;
    private final OfficeTypeRepository officeTypeRepository;
    private final CoveredStateService coveredStateService;

    public OfficeLookupService(
            OfficeRepository officeRepository,
            OfficeTypeRepository officeTypeRepository,
            CoveredStateService coveredStateService
    ) {
        this.officeRepository = officeRepository;
        this.officeTypeRepository = officeTypeRepository;
        this.coveredStateService = coveredStateService;
    }

    public List<OfficeResponse> listOfficeResponses(
            Long departmentId,
            Long officeTypeId,
            String boundaryLevel,
            Long stateId,
            Long divisionId,
            String divisionCode,
            Long districtId,
            Long talukaId
    ) {
        return listOffices(departmentId, officeTypeId, boundaryLevel, stateId, divisionId, divisionCode, districtId, talukaId)
                .stream()
                .map(OfficeResponse::from)
                .toList();
    }

    public List<Office> listOffices(
            Long departmentId,
            Long officeTypeId,
            String boundaryLevel,
            Long stateId,
            Long divisionId,
            String divisionCode,
            Long districtId,
            Long talukaId
    ) {
        String normalizedDivisionCode = trimToNull(divisionCode);
        String normalizedBoundaryLevel = resolveBoundaryLevel(officeTypeId, boundaryLevel);

        return loadBaseOffices(departmentId, officeTypeId, normalizedBoundaryLevel).stream()
                .filter(this::officeInCoveredState)
                .filter(o -> matchesStateId(o, stateId))
                .filter(o -> matchesDivisionId(o, divisionId))
                .filter(o -> matchesDivisionCode(o, normalizedDivisionCode))
                .filter(o -> matchesDistrictId(o, districtId))
                .filter(o -> matchesTalukaId(o, talukaId))
                .sorted(Comparator.comparing(Office::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private List<Office> loadBaseOffices(Long departmentId, Long officeTypeId, String boundaryLevel) {
        if (officeTypeId != null && departmentId != null) {
            return officeRepository.findByDepartmentIdAndOfficeTypeIdOrderByNameAsc(departmentId, officeTypeId);
        }
        if (officeTypeId != null) {
            return officeRepository.findByOfficeTypeIdOrderByNameAsc(officeTypeId);
        }
        if (departmentId != null && boundaryLevel != null) {
            return officeRepository.findByDepartmentIdAndOfficeType_BoundaryLevelOrderByNameAsc(
                    departmentId, boundaryLevel);
        }
        if (departmentId != null) {
            return officeRepository.findByDepartmentIdOrderByNameAsc(departmentId);
        }
        if (boundaryLevel != null) {
            return officeRepository.findByOfficeType_BoundaryLevelOrderByNameAsc(boundaryLevel);
        }
        return officeRepository.findAll();
    }

    private String resolveBoundaryLevel(Long officeTypeId, String boundaryLevel) {
        if (officeTypeId != null) {
            OfficeType officeType = officeTypeRepository.findById(officeTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid officeTypeId"));
            if (officeType.getBoundaryLevel() == null || officeType.getBoundaryLevel().isBlank()) {
                throw new IllegalArgumentException("Selected office type has no boundaryLevel configured");
            }
            return BoundaryLevel.normalize(officeType.getBoundaryLevel());
        }
        if (boundaryLevel == null || boundaryLevel.isBlank()) {
            return null;
        }
        return BoundaryLevel.normalize(boundaryLevel);
    }

    private boolean officeInCoveredState(Office office) {
        State state = office.getState();
        if (state != null) {
            return coveredStateService.isCoveredStateEntity(state);
        }
        String stateLgdCode = office.getStateLgdCode();
        return stateLgdCode == null || coveredStateService.matchesCoveredStateLgdCode(stateLgdCode);
    }

    private static boolean matchesStateId(Office office, Long stateId) {
        if (stateId == null) {
            return true;
        }
        State state = office.getState();
        return state != null && stateId.equals(state.getId());
    }

    private static boolean matchesDivisionId(Office office, Long divisionId) {
        if (divisionId == null) {
            return true;
        }
        Division division = office.getDivision();
        return division != null && divisionId.equals(division.getId());
    }

    private static boolean matchesDivisionCode(Office office, String divisionCode) {
        if (divisionCode == null) {
            return true;
        }
        Division division = office.getDivision();
        if (division != null && divisionCode.equals(division.getDivisionCode())) {
            return true;
        }
        District district = office.getDistrict();
        return district != null && divisionCode.equals(district.getDivisionCode());
    }

    private static boolean matchesDistrictId(Office office, Long districtId) {
        if (districtId == null) {
            return true;
        }
        District district = office.getDistrict();
        return district != null && districtId.equals(district.getId());
    }

    private static boolean matchesTalukaId(Office office, Long talukaId) {
        if (talukaId == null) {
            return true;
        }
        Taluka taluka = office.getTaluka();
        return taluka != null && talukaId.equals(taluka.getId());
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
