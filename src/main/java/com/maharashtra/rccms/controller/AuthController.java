package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.AdvocateProfileResponse;
import com.maharashtra.rccms.dto.AuthLoginRequest;
import com.maharashtra.rccms.dto.AuthResponse;
import com.maharashtra.rccms.service.AdvocateProfileService;
import com.maharashtra.rccms.service.AuthService;
import com.maharashtra.rccms.repository.AdvocateRegistrationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AdvocateProfileService advocateProfileService;
    private final AdvocateRegistrationRepository advocateRegistrationRepository;

    public AuthController(
            AuthService authService,
            AdvocateProfileService advocateProfileService,
            AdvocateRegistrationRepository advocateRegistrationRepository
    ) {
        this.authService = authService;
        this.advocateProfileService = advocateProfileService;
        this.advocateRegistrationRepository = advocateRegistrationRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        String loginId = principal.getName();
        if (advocateRegistrationRepository.findByEmail(loginId.trim().toLowerCase()).isPresent()) {
            try {
                AdvocateProfileResponse profile = advocateProfileService.getMyProfile(principal);
                return ResponseEntity.ok(profile);
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
            }
        }
        return ResponseEntity.ok(Map.of("loginId", loginId));
    }
}
