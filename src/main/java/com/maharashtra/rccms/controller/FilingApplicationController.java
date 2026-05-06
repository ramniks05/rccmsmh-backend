package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.filing.ApplicationSavePayload;
import com.maharashtra.rccms.dto.filing.ApplicationSaveResponse;
import com.maharashtra.rccms.dto.filing.ApplicationActionRequest;
import com.maharashtra.rccms.dto.filing.ApplicationActionResponse;
import com.maharashtra.rccms.dto.filing.OfficerApplicationDetailResponse;
import com.maharashtra.rccms.dto.filing.OfficerCaseApprovalResponse;
import com.maharashtra.rccms.dto.filing.OfficerInboxItemResponse;
import com.maharashtra.rccms.service.FilingApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/filing-applications")
public class FilingApplicationController {

    private final FilingApplicationService filingApplicationService;

    public FilingApplicationController(FilingApplicationService filingApplicationService) {
        this.filingApplicationService = filingApplicationService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody ApplicationSavePayload body, Principal principal) {
        try {
            ApplicationSaveResponse result = filingApplicationService.save(body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/officer/inbox")
    public ResponseEntity<?> officerInbox(Principal principal) {
        try {
            List<OfficerInboxItemResponse> result = filingApplicationService.listOfficerInbox(principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/officer/{applicationId}")
    public ResponseEntity<?> officerApplicationDetail(@PathVariable("applicationId") Long applicationId, Principal principal) {
        try {
            OfficerApplicationDetailResponse result = filingApplicationService.getOfficerApplicationDetail(applicationId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/officer/{applicationId}/approve")
    public ResponseEntity<?> officerApproveApplication(@PathVariable("applicationId") Long applicationId, Principal principal) {
        try {
            OfficerCaseApprovalResponse result = filingApplicationService.approveApplication(applicationId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/officer/{applicationId}/forward-to-po")
    public ResponseEntity<?> forwardToPo(
            @PathVariable("applicationId") Long applicationId,
            @RequestBody ApplicationActionRequest body,
            Principal principal
    ) {
        try {
            ApplicationActionResponse result = filingApplicationService.forwardToPo(applicationId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/officer/{applicationId}/return-to-clerk")
    public ResponseEntity<?> returnToClerk(
            @PathVariable("applicationId") Long applicationId,
            @RequestBody ApplicationActionRequest body,
            Principal principal
    ) {
        try {
            ApplicationActionResponse result = filingApplicationService.returnToClerk(applicationId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/officer/{applicationId}/reject")
    public ResponseEntity<?> rejectApplication(
            @PathVariable("applicationId") Long applicationId,
            @RequestBody ApplicationActionRequest body,
            Principal principal
    ) {
        try {
            ApplicationActionResponse result = filingApplicationService.rejectApplication(applicationId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
