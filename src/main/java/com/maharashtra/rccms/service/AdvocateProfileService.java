package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.AdvocateProfileResponse;
import com.maharashtra.rccms.dto.AdvocateProfileUpdateRequest;
import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.model.Gender;
import com.maharashtra.rccms.model.master.District;
import com.maharashtra.rccms.model.master.State;
import com.maharashtra.rccms.repository.AdvocateRegistrationRepository;
import com.maharashtra.rccms.repository.DistrictRepository;
import com.maharashtra.rccms.util.AdvocateRegistrationSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Objects;

@Service
public class AdvocateProfileService {

    private final AdvocateRegistrationRepository advocateRegistrationRepository;
    private final DistrictRepository districtRepository;
    private final LgdMasterLookupService lgdMasterLookupService;
    private final CoveredStateService coveredStateService;

    public AdvocateProfileService(
            AdvocateRegistrationRepository advocateRegistrationRepository,
            DistrictRepository districtRepository,
            LgdMasterLookupService lgdMasterLookupService,
            CoveredStateService coveredStateService
    ) {
        this.advocateRegistrationRepository = advocateRegistrationRepository;
        this.districtRepository = districtRepository;
        this.lgdMasterLookupService = lgdMasterLookupService;
        this.coveredStateService = coveredStateService;
    }

    @Transactional(readOnly = true)
    public AdvocateProfileResponse getMyProfile(Principal principal) {
        AdvocateRegistration row = resolveCurrentAdvocate(principal);
        return toProfileResponse(row);
    }

    @Transactional
    public AdvocateProfileResponse updateMyProfile(AdvocateProfileUpdateRequest request, Principal principal) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required.");
        }
        AdvocateRegistration row = resolveCurrentAdvocate(principal);
        validateProfileUpdate(request, row);

        row.setMobileNumber(request.getMobileNumber().trim());

        String newEmail = request.getEmail().trim().toLowerCase();
        if (!Objects.equals(row.getEmail(), newEmail)
                && advocateRegistrationRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        row.setEmail(newEmail);

        row.setGender(parseGender(request.getGender()));
        row.setPinCode(request.getPinCode().trim());
        applyLocationFields(row, request);
        row.setVillage(request.getVillage().trim());
        row.setAddressLine1(request.getAddressLine1().trim());

        AdvocateRegistration saved = advocateRegistrationRepository.save(row);
        return toProfileResponse(saved);
    }

    public AdvocateProfileResponse toProfileResponse(AdvocateRegistration row) {
        AdvocateProfileResponse out = new AdvocateProfileResponse();
        out.setId(row.getId());
        out.setUserType("ADVOCATE");
        out.setFirstName(row.getFirstName());
        out.setMiddleName(row.getMiddleName());
        out.setLastName(row.getLastName());
        out.setFullName(row.getFullName());
        out.setEmail(row.getEmail());
        out.setMobileNumber(row.getMobileNumber());
        out.setBarEnrollmentState(row.getBarEnrollmentState());
        lgdMasterLookupService.findStateByLgdCode(row.getBarEnrollmentState())
                .ifPresent(s -> out.setBarEnrollmentStateName(s.getName()));
        out.setBarEnrollmentYear(row.getBarEnrollmentYear());
        out.setBarEnrollmentNumber(row.getBarEnrollmentNumber());
        out.setPlaceOfPracticeState(row.getPlaceOfPracticeState());
        lgdMasterLookupService.findStateByLgdCode(row.getPlaceOfPracticeState())
                .ifPresent(s -> out.setPlaceOfPracticeStateName(s.getName()));
        out.setPlaceOfPracticeDistrict(row.getPlaceOfPracticeDistrict());
        lgdMasterLookupService.findDistrictByLgdCode(row.getPlaceOfPracticeDistrict())
                .ifPresent(d -> out.setPlaceOfPracticeDistrictName(d.getName()));
        out.setBarEnrollmentCertificateStorageKey(row.getBarEnrollmentCertificateStorageKey());
        out.setBarEnrollmentCertificateFileName(row.getBarEnrollmentCertificateFileName());
        out.setBarEnrollmentCertificateUploaded(AdvocateRegistrationSupport.hasText(row.getBarEnrollmentCertificateStorageKey()));
        out.setGender(row.getGender() != null ? row.getGender().name() : null);
        out.setPinCode(row.getPinCode());
        out.setStateId(row.getStateId());
        out.setStateName(row.getStateName());
        out.setDistrictId(row.getDistrictId());
        out.setDistrictName(row.getDistrictName());
        out.setVillage(row.getVillage());
        out.setAddressLine1(row.getAddressLine1());
        out.setAddressLine2(row.getAddressLine2());
        out.setAddressLine3(row.getAddressLine3());
        out.setAddress(row.getAddress());
        out.setLawFirmName(row.getLawFirmName());
        out.setProfileComplete(row.isProfileComplete());
        out.setCreatedAt(row.getCreatedAt());
        out.setUpdatedAt(row.getUpdatedAt());
        return out;
    }

    private AdvocateRegistration resolveCurrentAdvocate(Principal principal) {
        Objects.requireNonNull(principal);
        String email = principal.getName().trim().toLowerCase();
        return advocateRegistrationRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Advocate profile not found."));
    }

    private void validateProfileUpdate(AdvocateProfileUpdateRequest request, AdvocateRegistration row) {
        validateText(row.getFirstName(), "First name is missing from registration.");
        validateText(row.getLastName(), "Last name is missing from registration.");
        assertReadOnlyName(request.getFirstName(), row.getFirstName(), "First name", true);
        assertReadOnlyName(request.getMiddleName(), row.getMiddleName(), "Middle name", false);
        assertReadOnlyName(request.getLastName(), row.getLastName(), "Last name", true);
        AdvocateRegistrationSupport.validateMobile(request.getMobileNumber());
        AdvocateRegistrationSupport.validateEmail(request.getEmail());
        validateText(request.getGender(), "Gender is required.");
        parseGender(request.getGender());
        AdvocateRegistrationSupport.validatePinCode(request.getPinCode());
        if (request.getStateId() == null && !AdvocateRegistrationSupport.hasText(request.getStateName())) {
            throw new IllegalArgumentException("State is required.");
        }
        if (request.getDistrictId() == null && !AdvocateRegistrationSupport.hasText(request.getDistrictName())) {
            throw new IllegalArgumentException("District is required.");
        }
        validateText(request.getVillage(), "Village is required.");
        validateText(request.getAddressLine1(), "Address line 1 is required.");
    }

    private void applyLocationFields(AdvocateRegistration row, AdvocateProfileUpdateRequest request) {
        Long stateId = request.getStateId();
        Long districtId = request.getDistrictId();

        if (districtId != null) {
            District district = districtRepository.findById(districtId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid districtId."));
            row.setDistrictId(district.getId());
            row.setDistrictName(district.getName());
            if (district.getState() != null) {
                row.setStateId(district.getState().getId());
                row.setStateName(district.getState().getName());
            }
        } else {
            row.setDistrictId(null);
            row.setDistrictName(requiredText(request.getDistrictName(), "districtName"));
        }

        row.setSubdistrictId(null);
        row.setSubdistrictName(null);

        if (stateId != null) {
            State state = coveredStateService.requireCoveredStateById(stateId);
            row.setStateId(state.getId());
            row.setStateName(state.getName());
        } else if (!AdvocateRegistrationSupport.hasText(row.getStateName())) {
            row.setStateId(null);
            row.setStateName(requiredText(request.getStateName(), "stateName"));
        }
    }

    private static Gender parseGender(String raw) {
        String value = AdvocateRegistrationSupport.trimToNull(raw);
        if (value == null) {
            throw new IllegalArgumentException("Gender is required.");
        }
        try {
            return Gender.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Gender must be MALE, FEMALE, or OTHER.");
        }
    }

    private static void assertReadOnlyName(String sent, String stored, String label, boolean required) {
        if (!required && !AdvocateRegistrationSupport.hasText(sent) && !AdvocateRegistrationSupport.hasText(stored)) {
            return;
        }
        if (!AdvocateRegistrationSupport.hasText(sent)) {
            throw new IllegalArgumentException(label + " is required.");
        }
        if (!sameNormalizedText(sent, stored)) {
            throw new IllegalArgumentException(label + " cannot be changed on profile update.");
        }
    }

    private static boolean sameNormalizedText(String a, String b) {
        String left = AdvocateRegistrationSupport.trimToNull(a);
        String right = AdvocateRegistrationSupport.trimToNull(b);
        return Objects.equals(left, right);
    }

    private static void validateText(String value, String message) {
        if (!AdvocateRegistrationSupport.hasText(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    private static String requiredText(String value, String fieldName) {
        String trimmed = AdvocateRegistrationSupport.trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }
}
