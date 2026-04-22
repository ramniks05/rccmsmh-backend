package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.AuthLoginRequest;
import com.maharashtra.rccms.dto.AuthResponse;
import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.model.OfficerRegistration;
import com.maharashtra.rccms.model.PartyInPersonRegistration;
import com.maharashtra.rccms.model.UserRole;
import com.maharashtra.rccms.repository.AdvocateRegistrationRepository;
import com.maharashtra.rccms.repository.OfficerRegistrationRepository;
import com.maharashtra.rccms.repository.PartyInPersonRegistrationRepository;
import com.maharashtra.rccms.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private static final String INVALID_CREDENTIALS = "Invalid credentials.";

    private final AdvocateRegistrationRepository advocateRegistrationRepository;
    private final PartyInPersonRegistrationRepository partyInPersonRegistrationRepository;
    private final OfficerRegistrationRepository officerRegistrationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String adminUserId;
    private final String adminPassword;

    public AuthService(
            AdvocateRegistrationRepository advocateRegistrationRepository,
            PartyInPersonRegistrationRepository partyInPersonRegistrationRepository,
            OfficerRegistrationRepository officerRegistrationRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${rccms.admin.user-id}") String adminUserId,
            @Value("${rccms.admin.password}") String adminPassword
    ) {
        this.advocateRegistrationRepository = advocateRegistrationRepository;
        this.partyInPersonRegistrationRepository = partyInPersonRegistrationRepository;
        this.officerRegistrationRepository = officerRegistrationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.adminUserId = adminUserId;
        this.adminPassword = adminPassword;
    }

    public AuthResponse login(AuthLoginRequest request) {
        validateRequest(request);
        String loginId = request.getLoginId().trim();
        String password = request.getPassword().trim();
        UserRole role = request.getRole();

        return switch (role) {
            case ADMIN -> loginAdmin(loginId, password);
            case ADVOCATE -> loginAdvocate(loginId, password);
            case PARTY_IN_PERSON, PARTY_IN_PERSON_REPRESENTATIVE -> loginPartyInPerson(loginId, password, role);
            case OFFICER -> loginOfficer(loginId, password);
        };
    }

    private AuthResponse loginAdmin(String loginId, String password) {
        if (adminUserId.equals(loginId) && adminPassword.equals(password)) {
            return buildAuthResponse(adminUserId, "System Admin", UserRole.ADMIN);
        }
        throw new IllegalArgumentException(INVALID_CREDENTIALS);
    }

    private AuthResponse loginAdvocate(String loginId, String password) {
        String email = normalizeEmail(loginId);
        AdvocateRegistration user = advocateRegistrationRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_CREDENTIALS));
        assertPassword(password, user.getPasswordHash());
        return buildAuthResponse(user.getEmail(), user.getFullName(), UserRole.ADVOCATE);
    }

    private AuthResponse loginPartyInPerson(String loginId, String password, UserRole requestedRole) {
        String email = normalizeEmail(loginId);
        PartyInPersonRegistration user = partyInPersonRegistrationRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_CREDENTIALS));
        if (user.getRole() != requestedRole) {
            throw new IllegalArgumentException(INVALID_CREDENTIALS);
        }
        assertPassword(password, user.getPasswordHash());
        return buildAuthResponse(user.getEmail(), user.getFullName(), user.getRole());
    }

    private AuthResponse loginOfficer(String loginId, String password) {
        String email = normalizeEmail(loginId);
        OfficerRegistration user = officerRegistrationRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_CREDENTIALS));
        assertPassword(password, user.getPasswordHash());
        return buildAuthResponse(user.getEmail(), user.getFullName(), UserRole.OFFICER);
    }

    private void assertPassword(String rawPassword, String passwordHash) {
        if (!hasText(passwordHash) || !passwordEncoder.matches(rawPassword, passwordHash)) {
            throw new IllegalArgumentException(INVALID_CREDENTIALS);
        }
    }

    private AuthResponse buildAuthResponse(String loginId, String displayName, UserRole role) {
        String token = jwtService.generateToken(loginId, Map.of(
                "role", role.name(),
                "name", displayName
        ));
        return new AuthResponse(token, "Bearer", role.name(), displayName);
    }

    private void validateRequest(AuthLoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required.");
        }
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required.");
        }
        if (!hasText(request.getLoginId())) {
            throw new IllegalArgumentException("Login ID is required.");
        }
        if (!hasText(request.getPassword())) {
            throw new IllegalArgumentException("Password is required.");
        }
    }

    private static String normalizeEmail(String loginId) {
        return loginId.trim().toLowerCase();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
