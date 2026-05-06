package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.caseflow.CaseHearingResponse;
import com.maharashtra.rccms.dto.caseflow.CaseHearingScheduleRequest;
import com.maharashtra.rccms.dto.caseflow.CaseInboxItemResponse;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentRequest;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerDashboardResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetHistoryResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetUpsertRequest;
import com.maharashtra.rccms.service.CaseProceedingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cases/officer")
public class CaseProceedingController {

    private final CaseProceedingService caseProceedingService;

    public CaseProceedingController(CaseProceedingService caseProceedingService) {
        this.caseProceedingService = caseProceedingService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(Principal principal) {
        try {
            OfficerDashboardResponse result = caseProceedingService.getOfficerDashboard(principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> listCaseInbox(
            @RequestParam(name = "status", required = false) String status,
            Principal principal
    ) {
        try {
            List<CaseInboxItemResponse> result = caseProceedingService.listCaseInbox(principal, status);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/hearings")
    public ResponseEntity<?> scheduleHearing(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseHearingScheduleRequest body,
            Principal principal
    ) {
        try {
            CaseHearingResponse result = caseProceedingService.scheduleHearing(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}/hearings")
    public ResponseEntity<?> listCaseHearings(@PathVariable("caseId") Long caseId, Principal principal) {
        try {
            List<CaseHearingResponse> result = caseProceedingService.listCaseHearings(caseId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/hearings/today")
    public ResponseEntity<?> listTodayHearings(Principal principal) {
        try {
            List<CaseHearingResponse> result = caseProceedingService.listTodayHearings(principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{caseId}/ordersheet")
    public ResponseEntity<?> upsertOrderSheet(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseOrderSheetUpsertRequest body,
            Principal principal
    ) {
        try {
            CaseOrderSheetResponse result = caseProceedingService.upsertOrderSheet(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}/ordersheet")
    public ResponseEntity<?> getOrderSheet(@PathVariable("caseId") Long caseId, Principal principal) {
        try {
            CaseOrderSheetResponse result = caseProceedingService.getOrderSheet(caseId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}/ordersheet/history")
    public ResponseEntity<?> getOrderSheetHistory(@PathVariable("caseId") Long caseId, Principal principal) {
        try {
            List<CaseOrderSheetHistoryResponse> result = caseProceedingService.getOrderSheetHistory(caseId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/judgment")
    public ResponseEntity<?> passFinalJudgment(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseJudgmentRequest body,
            Principal principal
    ) {
        try {
            CaseJudgmentResponse result = caseProceedingService.passFinalJudgment(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
