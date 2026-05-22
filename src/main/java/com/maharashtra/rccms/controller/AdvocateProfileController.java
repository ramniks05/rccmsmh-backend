package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.AdvocateProfileResponse;
import com.maharashtra.rccms.dto.AdvocateProfileUpdateRequest;
import com.maharashtra.rccms.service.AdvocateProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/advocates/me")
public class AdvocateProfileController {

    private final AdvocateProfileService advocateProfileService;

    public AdvocateProfileController(AdvocateProfileService advocateProfileService) {
        this.advocateProfileService = advocateProfileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        try {
            AdvocateProfileResponse result = advocateProfileService.getMyProfile(principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody AdvocateProfileUpdateRequest body, Principal principal) {
        try {
            AdvocateProfileResponse result = advocateProfileService.updateMyProfile(body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
