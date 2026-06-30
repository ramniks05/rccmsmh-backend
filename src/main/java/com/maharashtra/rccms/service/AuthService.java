package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.AuthLoginRequest;
import com.maharashtra.rccms.dto.AuthResponse;
import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.model.Employee;
import com.maharashtra.rccms.model.EmployeePosting;
import com.maharashtra.rccms.model.PartyInPersonRegistration;
import com.maharashtra.rccms.model.UserRole;
import com.maharashtra.rccms.repository.AdvocateRegistrationRepository;
import com.maharashtra.rccms.repository.EmployeePostingRepository;
import com.maharashtra.rccms.repository.EmployeeRepository;
import com.maharashtra.rccms.repository.PartyInPersonRegistrationRepository;
import com.maharashtra.rccms.security.JwtService;
import com.maharashtra.rccms.util.EmployeeLoginSupport;
import com.maharashtra.rccms.util.PresidingOfficerDesignationSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final String INVALID_CREDENTIALS = "Invalid credentials.";

    private final AdvocateRegistrationRepository advocateRegistrationRepository;
    private final PartyInPersonRegistrationRepository partyInPersonRegistrationRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeePostingRepository employeePostingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String adminUserId;
    private final String adminPassword;

    public AuthService(
            AdvocateRegistrationRepository advocateRegistrationRepository,
            PartyInPersonRegistrationRepository partyInPersonRegistrationRepository,
            EmployeeRepository employeeRepository,
            EmployeePostingRepository employeePostingRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${rccms.admin.user-id}") String adminUserId,
            @Value("${rccms.admin.password}") String adminPassword
    ) {
        this.advocateRegistrationRepository = advocateRegistrationRepository;
        this.partyInPersonRegistrationRepository = partyInPersonRegistrationRepository;
        this.employeeRepository = employeeRepository;
        this.employeePostingRepository = employeePostingRepository;
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
        return buildAdvocateAuthResponse(user);
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
        Employee employee = EmployeeLoginSupport.findByLoginId(employeeRepository, loginId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_CREDENTIALS));
        assertPassword(password, employee.getPasswordHash());
        if (Boolean.FALSE.equals(employee.getIsActive())) {
            throw new IllegalArgumentException(INVALID_CREDENTIALS);
        }
        String officerLoginId = EmployeeLoginSupport.buildLoginId(employee.getEmail(), employee.getEmployeeCode());
        EmployeePosting posting = resolveOfficerCurrentPosting(employee);
        Long designationId = posting != null && posting.getDesignation() != null ? posting.getDesignation().getId() : null;
        String designationName = posting != null && posting.getDesignation() != null ? posting.getDesignation().getName() : null;
        Long officeId = posting != null && posting.getOffice() != null ? posting.getOffice().getId() : null;
        String officeName = posting != null && posting.getOffice() != null ? posting.getOffice().getName() : null;
        String officeCode = posting != null && posting.getOffice() != null ? posting.getOffice().getOfficeCode() : null;
        boolean presidingOfficer = PresidingOfficerDesignationSupport.isPresidingOfficer(posting);
        String officerActorRole = presidingOfficer ? "PRESIDING_OFFICER" : "CLERK";
        return buildAuthResponse(officerLoginId, employee.getFullName(), UserRole.OFFICER, designationId, designationName,
                officeId, officeName, officeCode, null, presidingOfficer, officerActorRole);
    }

    private void assertPassword(String rawPassword, String passwordHash) {
        if (!hasText(passwordHash) || !passwordEncoder.matches(rawPassword, passwordHash)) {
            throw new IllegalArgumentException(INVALID_CREDENTIALS);
        }
    }

    private AuthResponse buildAdvocateAuthResponse(AdvocateRegistration user) {
        String loginId = user.getEmail();
        String displayName = user.getFullName();
        String barEnrollmentNumber = trimToNull(user.getBarEnrollmentNumber());

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", UserRole.ADVOCATE.name());
        claims.put("name", displayName);
        if (barEnrollmentNumber != null) {
            claims.put("barEnrollmentNumber", barEnrollmentNumber);
        }

        String token = jwtService.generateToken(loginId, claims);
        return new AuthResponse(token, "Bearer", UserRole.ADVOCATE.name(), displayName,
                null, null, null, null, null, barEnrollmentNumber);
    }

    private AuthResponse buildAuthResponse(String loginId, String displayName, UserRole role) {
        return buildAuthResponse(loginId, displayName, role, null, null, null, null, null);
    }

    private AuthResponse buildAuthResponse(
            String loginId,
            String displayName,
            UserRole role,
            Long designationId,
            String designationName,
            Long officeId,
            String officeName,
            String officeCode
    ) {
        return buildAuthResponse(loginId, displayName, role, designationId, designationName, officeId, officeName, officeCode,
                null, null, null);
    }

    private AuthResponse buildAuthResponse(
            String loginId,
            String displayName,
            UserRole role,
            Long designationId,
            String designationName,
            Long officeId,
            String officeName,
            String officeCode,
            String barEnrollmentNumber,
            Boolean presidingOfficer,
            String officerActorRole
    ) {
        String token = jwtService.generateToken(loginId, Map.of(
                "role", role.name(),
                "name", displayName
        ));
        return new AuthResponse(token, "Bearer", role.name(), displayName, designationId, designationName,
                officeId, officeName, officeCode, barEnrollmentNumber, presidingOfficer, officerActorRole);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private EmployeePosting resolveOfficerCurrentPosting(Employee employee) {
        if (employee.getId() == null) {
            return null;
        }
        return employeePostingRepository.findFirstByEmployeeIdAndToDateIsNull(employee.getId())
                .orElse(null);
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
