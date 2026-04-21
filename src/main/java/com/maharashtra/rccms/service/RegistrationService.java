package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.RegistrationRequest;
import com.maharashtra.rccms.dto.RegistrationResponse;
import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.model.PartyInPersonRegistration;
import com.maharashtra.rccms.model.UserRole;
import com.maharashtra.rccms.repository.AdvocateRegistrationRepository;
import com.maharashtra.rccms.repository.OfficerRegistrationRepository;
import com.maharashtra.rccms.repository.PartyInPersonRegistrationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private static final String OFFICER_NOT_SELF_REGISTER =
            "Officer accounts are not self-registered; they will be created by an administrator.";

    private final AdvocateRegistrationRepository advocateRegistrationRepository;
    private final PartyInPersonRegistrationRepository partyInPersonRegistrationRepository;
    private final OfficerRegistrationRepository officerRegistrationRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            AdvocateRegistrationRepository advocateRegistrationRepository,
            PartyInPersonRegistrationRepository partyInPersonRegistrationRepository,
            OfficerRegistrationRepository officerRegistrationRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.advocateRegistrationRepository = advocateRegistrationRepository;
        this.partyInPersonRegistrationRepository = partyInPersonRegistrationRepository;
        this.officerRegistrationRepository = officerRegistrationRepository;
        this.passwordEncoder = passwordEncoder;
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

        validateText(request.getFullName(), "Full name is required.");
        validateText(request.getEmail(), "Email is required.");
        validateText(request.getMobileNumber(), "Mobile number is required.");
        validateText(request.getAddress(), "Address is required.");
        validateText(request.getPassword(), "Password is required.");
        if (request.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        if (request.getRole() == UserRole.ADVOCATE) {
            validateText(request.getBarCouncilNumber(), "Bar council number is required for advocate.");
            validateText(request.getEnrollmentNumber(), "Enrollment number is required for advocate.");
        } else {
            if (hasText(request.getBarCouncilNumber())
                    || hasText(request.getEnrollmentNumber())
                    || hasText(request.getLawFirmName())) {
                throw new IllegalArgumentException("Advocate fields are allowed only for advocate role.");
            }
        }
    }

    private RegistrationResponse registerAdvocate(RegistrationRequest request, String normalizedEmail) {
        AdvocateRegistration registration = new AdvocateRegistration();
        registration.setFullName(request.getFullName().trim());
        registration.setEmail(normalizedEmail);
        registration.setMobileNumber(request.getMobileNumber().trim());
        registration.setAddress(request.getAddress().trim());
        registration.setBarCouncilNumber(request.getBarCouncilNumber().trim());
        registration.setEnrollmentNumber(request.getEnrollmentNumber().trim());
        registration.setLawFirmName(trimToNull(request.getLawFirmName()));
        registration.setPasswordHash(passwordEncoder.encode(request.getPassword().trim()));

        AdvocateRegistration saved = advocateRegistrationRepository.save(registration);
        return new RegistrationResponse(saved.getId(), UserRole.ADVOCATE, "Registration successful.");
    }

    private RegistrationResponse registerPartyInPerson(RegistrationRequest request, String normalizedEmail) {
        PartyInPersonRegistration registration = new PartyInPersonRegistration();
        registration.setRole(request.getRole());
        registration.setFullName(request.getFullName().trim());
        registration.setEmail(normalizedEmail);
        registration.setMobileNumber(request.getMobileNumber().trim());
        registration.setAddress(request.getAddress().trim());
        registration.setPasswordHash(passwordEncoder.encode(request.getPassword().trim()));

        PartyInPersonRegistration saved = partyInPersonRegistrationRepository.save(registration);
        return new RegistrationResponse(saved.getId(), saved.getRole(), "Registration successful.");
    }

    private boolean emailExistsAnywhere(String email) {
        return advocateRegistrationRepository.existsByEmail(email)
                || partyInPersonRegistrationRepository.existsByEmail(email)
                || officerRegistrationRepository.existsByEmail(email);
    }

    private void validateText(String value, String message) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }
}
