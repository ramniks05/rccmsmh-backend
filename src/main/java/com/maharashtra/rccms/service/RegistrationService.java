package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.RegistrationRequest;
import com.maharashtra.rccms.dto.RegistrationResponse;
import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.model.PartyInPersonRegistration;
import com.maharashtra.rccms.model.UserRole;
import com.maharashtra.rccms.model.master.District;
import com.maharashtra.rccms.model.master.State;
import com.maharashtra.rccms.repository.AdvocateRegistrationRepository;
import com.maharashtra.rccms.repository.EmployeeRepository;
import com.maharashtra.rccms.repository.PartyInPersonRegistrationRepository;
import com.maharashtra.rccms.util.AdvocateRegistrationSupport;
import com.maharashtra.rccms.util.EmployeeLoginSupport;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class RegistrationService {

    private static final String OFFICER_NOT_SELF_REGISTER =
            "Officer accounts are not self-registered; they will be created by an administrator.";

    private final AdvocateRegistrationRepository advocateRegistrationRepository;
    private final PartyInPersonRegistrationRepository partyInPersonRegistrationRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final LgdMasterLookupService lgdMasterLookupService;

    public RegistrationService(
            AdvocateRegistrationRepository advocateRegistrationRepository,
            PartyInPersonRegistrationRepository partyInPersonRegistrationRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            LgdMasterLookupService lgdMasterLookupService
    ) {
        this.advocateRegistrationRepository = advocateRegistrationRepository;
        this.partyInPersonRegistrationRepository = partyInPersonRegistrationRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.lgdMasterLookupService = lgdMasterLookupService;
    }

    public RegistrationResponse register(RegistrationRequest request) {
        validateRequest(request);
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (emailExistsAnywhere(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        return switch (request.getRole()) {
            case ADVOCATE -> registerAdvocate(request, normalizedEmail);
            case PARTY_IN_PERSON, PARTY_IN_PERSON_REPRESENTATIVE -> registerPartyInPerson(request, normalizedEmail);
            case OFFICER -> throw new IllegalArgumentException(OFFICER_NOT_SELF_REGISTER);
            case ADMIN -> throw new IllegalArgumentException("Admin is a fixed account and cannot register.");
        };
    }

    private void validateRequest(RegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required.");
        }

        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required.");
        }
        if (request.getRole() == UserRole.ADMIN) {
            throw new IllegalArgumentException("Admin is a fixed account and cannot register.");
        }
        if (request.getRole() == UserRole.OFFICER) {
            throw new IllegalArgumentException(OFFICER_NOT_SELF_REGISTER);
        }

        AdvocateRegistrationSupport.validateEmail(request.getEmail());
        AdvocateRegistrationSupport.validateMobile(request.getMobileNumber());
        validateText(request.getPassword(), "Password is required.");
        if (request.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        if (request.getRole() == UserRole.ADVOCATE) {
            validateAdvocateRegistration(request);
        } else {
            validatePartyInPersonRegistration(request);
            if (hasAdvocateOnlyFields(request)) {
                throw new IllegalArgumentException("Advocate fields are allowed only for advocate role.");
            }
        }
    }

    private void validatePartyInPersonRegistration(RegistrationRequest request) {
        validateText(request.getFullName(), "Full name is required.");
        if (hasStructuredPartyAddress(request)) {
            AdvocateRegistrationSupport.validatePinCode(request.getPinCode());
            validateText(request.getStateName(), "State is required.");
            validateText(request.getDistrictName(), "District is required.");
            validateText(request.getAddressLine1(), "Address line 1 is required.");
        } else {
            validateText(request.getAddress(), "Address is required.");
        }
    }

    private static boolean hasStructuredPartyAddress(RegistrationRequest request) {
        return AdvocateRegistrationSupport.hasText(request.getAddressLine1())
                || AdvocateRegistrationSupport.hasText(request.getPinCode());
    }

    private void validateAdvocateRegistration(RegistrationRequest request) {
        if (hasPartyOnlyFields(request)) {
            throw new IllegalArgumentException(
                    "Party address fields (pinCode, stateName, addressLine1, etc.) are only for party in person registration."
            );
        }
        boolean hasStructuredName = AdvocateRegistrationSupport.hasText(request.getFirstName())
                && AdvocateRegistrationSupport.hasText(request.getLastName());
        if (!hasStructuredName && !AdvocateRegistrationSupport.hasText(request.getFullName())) {
            throw new IllegalArgumentException("First name and last name are required for advocate registration.");
        }

        lgdMasterLookupService.requireStateByLgdCode(request.getBarEnrollmentState(), "barEnrollmentState");
        if (request.getBarEnrollmentYear() == null) {
            throw new IllegalArgumentException("Bar enrollment year is required.");
        }
        int year = request.getBarEnrollmentYear();
        int currentYear = Year.now().getValue();
        if (year < 1950 || year > currentYear) {
            throw new IllegalArgumentException("Bar enrollment year is invalid.");
        }

        String enrollment = resolveBarEnrollmentNumber(request);
        validateText(enrollment, "Bar enrollment number is required.");
        if (advocateRegistrationRepository.existsByBarEnrollmentNumberIgnoreCase(enrollment)) {
            throw new IllegalArgumentException("Bar enrollment number is already registered.");
        }

        lgdMasterLookupService.requireStateByLgdCode(request.getPlaceOfPracticeState(), "placeOfPracticeState");
        lgdMasterLookupService.requireDistrictByLgdCode(
                request.getPlaceOfPracticeDistrict(),
                request.getPlaceOfPracticeState(),
                "placeOfPracticeDistrict"
        );
        validateText(
                request.getBarEnrollmentCertificateStorageKey(),
                "Bar enrollment certificate upload is required."
        );
    }

    private RegistrationResponse registerAdvocate(RegistrationRequest request, String normalizedEmail) {
        AdvocateRegistration registration = new AdvocateRegistration();
        registration.setEmail(normalizedEmail);
        registration.setMobileNumber(request.getMobileNumber().trim());
        registration.setPasswordHash(passwordEncoder.encode(request.getPassword().trim()));

        if (AdvocateRegistrationSupport.hasText(request.getFirstName())) {
            registration.setFirstName(request.getFirstName().trim());
            registration.setMiddleName(AdvocateRegistrationSupport.trimToNull(request.getMiddleName()));
            registration.setLastName(request.getLastName().trim());
        } else {
            registration.setFullName(request.getFullName().trim());
        }

        String enrollment = resolveBarEnrollmentNumber(request);
        State barEnrollmentState = lgdMasterLookupService.requireStateByLgdCode(
                request.getBarEnrollmentState(),
                "barEnrollmentState"
        );
        State practiceState = lgdMasterLookupService.requireStateByLgdCode(
                request.getPlaceOfPracticeState(),
                "placeOfPracticeState"
        );
        District practiceDistrict = lgdMasterLookupService.requireDistrictByLgdCode(
                request.getPlaceOfPracticeDistrict(),
                request.getPlaceOfPracticeState(),
                "placeOfPracticeDistrict"
        );

        registration.setBarEnrollmentState(barEnrollmentState.getLgdCode());
        registration.setBarEnrollmentYear(request.getBarEnrollmentYear());
        registration.setBarEnrollmentNumber(enrollment);
        registration.setBarCouncilNumber(enrollment);
        registration.setEnrollmentNumber(enrollment);
        registration.setPlaceOfPracticeState(practiceState.getLgdCode());
        registration.setPlaceOfPracticeDistrict(practiceDistrict.getLgdCode());
        registration.setBarEnrollmentCertificateStorageKey(
                request.getBarEnrollmentCertificateStorageKey().trim()
        );
        registration.setBarEnrollmentCertificateFileName(
                AdvocateRegistrationSupport.trimToNull(request.getBarEnrollmentCertificateFileName())
        );
        registration.setLawFirmName(AdvocateRegistrationSupport.trimToNull(request.getLawFirmName()));
        // Residential address is captured later via PUT /api/advocates/me/profile (addressLine1, pinCode, etc.)
        registration.setAddress(null);
        registration.setProfileComplete(false);

        AdvocateRegistration saved = advocateRegistrationRepository.save(registration);
        return new RegistrationResponse(
                saved.getId(),
                UserRole.ADVOCATE,
                "Registration successful. Please complete your profile.",
                false
        );
    }

    private RegistrationResponse registerPartyInPerson(RegistrationRequest request, String normalizedEmail) {
        PartyInPersonRegistration registration = new PartyInPersonRegistration();
        registration.setRole(request.getRole());
        registration.setFullName(request.getFullName().trim());
        registration.setEmail(normalizedEmail);
        registration.setMobileNumber(request.getMobileNumber().trim());
        registration.setPasswordHash(passwordEncoder.encode(request.getPassword().trim()));

        if (hasStructuredPartyAddress(request)) {
            registration.setPinCode(request.getPinCode().trim());
            registration.setStateName(request.getStateName().trim());
            registration.setDistrictName(request.getDistrictName().trim());
            registration.setSubdistrictName(
                    AdvocateRegistrationSupport.trimToNull(request.getSubdistrictName())
            );
            registration.setVillage(AdvocateRegistrationSupport.trimToNull(request.getVillage()));
            registration.setAddressLine1(request.getAddressLine1().trim());
        } else {
            registration.setAddress(request.getAddress().trim());
        }

        PartyInPersonRegistration saved = partyInPersonRegistrationRepository.save(registration);
        return new RegistrationResponse(saved.getId(), saved.getRole(), "Registration successful.", true);
    }

    private static String resolveBarEnrollmentNumber(RegistrationRequest request) {
        if (AdvocateRegistrationSupport.hasText(request.getBarEnrollmentNumber())) {
            return request.getBarEnrollmentNumber().trim();
        }
        if (AdvocateRegistrationSupport.hasText(request.getEnrollmentNumber())) {
            return request.getEnrollmentNumber().trim();
        }
        if (AdvocateRegistrationSupport.hasText(request.getBarCouncilNumber())) {
            return request.getBarCouncilNumber().trim();
        }
        return null;
    }

    private static boolean hasAdvocateOnlyFields(RegistrationRequest request) {
        return AdvocateRegistrationSupport.hasText(request.getBarCouncilNumber())
                || AdvocateRegistrationSupport.hasText(request.getEnrollmentNumber())
                || AdvocateRegistrationSupport.hasText(request.getBarEnrollmentNumber())
                || AdvocateRegistrationSupport.hasText(request.getBarEnrollmentState())
                || request.getBarEnrollmentYear() != null
                || AdvocateRegistrationSupport.hasText(request.getPlaceOfPracticeState())
                || AdvocateRegistrationSupport.hasText(request.getPlaceOfPracticeDistrict())
                || AdvocateRegistrationSupport.hasText(request.getBarEnrollmentCertificateStorageKey())
                || AdvocateRegistrationSupport.hasText(request.getFirstName());
    }

    private static boolean hasPartyOnlyFields(RegistrationRequest request) {
        return hasStructuredPartyAddress(request)
                || AdvocateRegistrationSupport.hasText(request.getStateName())
                || AdvocateRegistrationSupport.hasText(request.getDistrictName())
                || AdvocateRegistrationSupport.hasText(request.getSubdistrictName())
                || AdvocateRegistrationSupport.hasText(request.getVillage());
    }

    private boolean emailExistsAnywhere(String email) {
        return advocateRegistrationRepository.existsByEmail(email)
                || partyInPersonRegistrationRepository.existsByEmail(email)
                || EmployeeLoginSupport.findByLoginId(employeeRepository, email).isPresent();
    }

    private void validateText(String value, String message) {
        if (!AdvocateRegistrationSupport.hasText(value)) {
            throw new IllegalArgumentException(message);
        }
    }
}
