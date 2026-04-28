package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.AdvocateLookupResponse;
import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.repository.AdvocateRegistrationRepository;
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

    public AdvocateLookupController(AdvocateRegistrationRepository advocateRegistrationRepository) {
        this.advocateRegistrationRepository = advocateRegistrationRepository;
    }

    /**
     * @param barCouncilNumber Bar council number (query param avoids path encoding issues).
     */
    @GetMapping("/by-bar-council")
    public ResponseEntity<?> getByBarCouncilNumber(@RequestParam("barCouncilNumber") String barCouncilNumber) {
        if (barCouncilNumber == null || barCouncilNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "barCouncilNumber is required"));
        }
        Optional<AdvocateRegistration> found = advocateRegistrationRepository
                .findFirstByBarCouncilNumberIgnoreCaseOrderByIdAsc(barCouncilNumber.trim());
        if (found.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "No advocate found for this bar council number"));
        }
        return ResponseEntity.ok(toResponse(found.get()));
    }

    private static AdvocateLookupResponse toResponse(AdvocateRegistration r) {
        return new AdvocateLookupResponse(
                r.getId(),
                r.getFullName(),
                r.getEmail(),
                r.getMobileNumber(),
                r.getAddress(),
                r.getBarCouncilNumber(),
                r.getEnrollmentNumber(),
                r.getLawFirmName(),
                r.getCreatedAt()
        );
    }
}
