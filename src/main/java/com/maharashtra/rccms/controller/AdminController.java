package com.maharashtra.rccms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(Principal principal) {
        return ResponseEntity.ok(Map.of(
                "message", "Admin access granted.",
                "loginId", principal.getName()
        ));
    }
}
