package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.AdvocateLookupResponse;
import com.maharashtra.rccms.dto.AdvocateProfileResponse;
import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.repository.AdvocateRegistrationRepository;
import com.maharashtra.rccms.service.AdvocateProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * Read-only advocate lookup for any authenticated user (not public).
 * Security: {@code anyRequest().authenticated()} in {@link com.maharashtra.rccms.config.SecurityConfig}.
 */
@RestController
@RequestMapping("/api/advocates")
public class AdvocateLookupController {

    private final AdvocateRegistrationRepository advocateRegistrationRepository;
    private final AdvocateProfileService advocateProfileService;

    public AdvocateLookupController(
            AdvocateRegistrationRepository advocateRegistrationRepository,
            AdvocateProfileService advocateProfileService
    ) {
        this.advocateRegistrationRepository = advocateRegistrationRepository;
        this.advocateProfileService = advocateProfileService;
    }

    /**
     * @param barCouncilNumber Bar council number (query param avoids path encoding issues).
     */
    @GetMapping("/by-bar-council")
    public ResponseEntity<?> getByBarCouncilNumber(@RequestParam("barCouncilNumber") String barCouncilNumber) {
        if (barCouncilNumber == null || barCouncilNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "barCouncilNumber is required"));
        }
        String key = barCouncilNumber.trim();
        Optional<AdvocateRegistration> found = advocateRegistrationRepository
                .findFirstByBarEnrollmentNumberIgnoreCaseOrderByIdAsc(key);
        if (found.isEmpty()) {
            found = advocateRegistrationRepository.findFirstByBarCouncilNumberIgnoreCaseOrderByIdAsc(key);
        }
        if (found.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "No advocate found for this bar council number"));
        }
        return ResponseEntity.ok(toResponse(found.get()));
    }

    private AdvocateLookupResponse toResponse(AdvocateRegistration r) {
        AdvocateProfileResponse profile = advocateProfileService.toProfileResponse(r);
        String enrollment = profile.getBarEnrollmentNumber();
        return new AdvocateLookupResponse(
                profile.getId(),
                profile.getFullName(),
                profile.getFirstName(),
                profile.getMiddleName(),
                profile.getLastName(),
                profile.getEmail(),
                profile.getMobileNumber(),
                profile.getAddress(),
                profile.getBarEnrollmentState(),
                profile.getBarEnrollmentStateName(),
                profile.getBarEnrollmentYear(),
                enrollment,
                enrollment,
                enrollment,
                profile.getPlaceOfPracticeState(),
                profile.getPlaceOfPracticeStateName(),
                profile.getPlaceOfPracticeDistrict(),
                profile.getPlaceOfPracticeDistrictName(),
                profile.getLawFirmName(),
                profile.isProfileComplete(),
                profile.getCreatedAt()
        );
    }
}
