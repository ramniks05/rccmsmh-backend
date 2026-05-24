package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.caseflow.CaseHearingResponse;
import com.maharashtra.rccms.dto.caseflow.CaseHearingRescheduleRequest;
import com.maharashtra.rccms.dto.caseflow.CaseHearingScheduleRequest;
import com.maharashtra.rccms.dto.caseflow.CaseInboxItemResponse;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentDraftRequest;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentWorkflowResponse;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeResponse;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeServeToPartyRequest;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeServeToPartyResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetHistoryResponse;
import com.maharashtra.rccms.dto.caseflow.CaseRoznamaCompleteRequest;
import com.maharashtra.rccms.dto.caseflow.CaseRoznamaCompleteResponse;
import com.maharashtra.rccms.dto.caseflow.HearingAttendanceSaveRequest;
import com.maharashtra.rccms.dto.caseflow.CaseWorkflowRevertRequest;
import com.maharashtra.rccms.dto.caseflow.OfficerDashboardResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerNoticeServeQueueItemResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerRoznamaTableResponse;
import com.maharashtra.rccms.dto.caseflow.RoznamaResponse;
import com.maharashtra.rccms.dto.filing.OfficerApplicationDetailResponse;
import com.maharashtra.rccms.dto.workflow.CaseWorkflowContextResponse;
import com.maharashtra.rccms.dto.workflow.JudgmentWorkflowHistoryResponse;
import com.maharashtra.rccms.dto.workflow.NoticeTemplateResolvedResponse;
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
import java.time.LocalDate;
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

    @GetMapping("/notices/pending-serve")
    public ResponseEntity<?> listPendingNoticeServe(Principal principal) {
        try {
            List<OfficerNoticeServeQueueItemResponse> result = caseProceedingService.listPendingNoticeServe(principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/roznama/table")
    public ResponseEntity<?> listRoznamaTable(
            @RequestParam(name = "date", required = false) String date,
            Principal principal
    ) {
        try {
            LocalDate hearingDate = date != null && !date.isBlank() ? LocalDate.parse(date.trim()) : null;
            OfficerRoznamaTableResponse result = caseProceedingService.listRoznamaTable(principal, hearingDate);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}/workflow-context")
    public ResponseEntity<?> getWorkflowContext(
            @PathVariable("caseId") Long caseId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            Principal principal
    ) {
        try {
            CaseWorkflowContextResponse result = caseProceedingService.getWorkflowContext(caseId, hearingId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}/notices/template")
    public ResponseEntity<?> resolveNoticeTemplate(
            @PathVariable("caseId") Long caseId,
            @RequestParam(name = "noticeType", required = false) String noticeType,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            @RequestParam(name = "partyNames", required = false) String partyNames,
            Principal principal
    ) {
        try {
            NoticeTemplateResolvedResponse result = caseProceedingService.resolveNoticeTemplate(
                    caseId, noticeType, hearingId, partyNames, principal
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<?> getOfficerCaseDetail(@PathVariable("caseId") Long caseId, Principal principal) {
        try {
            OfficerApplicationDetailResponse result = caseProceedingService.getOfficerCaseDetail(caseId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}/notices")
    public ResponseEntity<?> listCaseNotices(@PathVariable("caseId") Long caseId, Principal principal) {
        try {
            List<CaseNoticeResponse> result = caseProceedingService.listCaseNotices(caseId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/notices/serve")
    public ResponseEntity<?> serveNoticeToParty(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseNoticeServeToPartyRequest body,
            Principal principal
    ) {
        try {
            CaseNoticeServeToPartyResponse result = caseProceedingService.serveNoticeToParty(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}/judgment/workflow")
    public ResponseEntity<?> getJudgmentWorkflow(@PathVariable("caseId") Long caseId, Principal principal) {
        try {
            CaseJudgmentWorkflowResponse result = caseProceedingService.getJudgmentWorkflow(caseId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{caseId}/judgment/draft")
    @PostMapping("/{caseId}/judgment/draft")
    public ResponseEntity<?> saveJudgmentDraft(
            @PathVariable("caseId") Long caseId,
            @RequestBody(required = false) CaseJudgmentDraftRequest body,
            Principal principal
    ) {
        try {
            CaseJudgmentWorkflowResponse result = caseProceedingService.saveJudgmentDraft(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/judgment/send-to-clerk")
    public ResponseEntity<?> sendJudgmentToClerk(
            @PathVariable("caseId") Long caseId,
            @RequestBody(required = false) CaseWorkflowRevertRequest body,
            Principal principal
    ) {
        try {
            CaseJudgmentWorkflowResponse result = caseProceedingService.sendJudgmentToClerk(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}/judgment/history")
    public ResponseEntity<?> listJudgmentHistory(@PathVariable("caseId") Long caseId, Principal principal) {
        try {
            List<JudgmentWorkflowHistoryResponse> result = caseProceedingService.listJudgmentHistory(caseId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/judgment/submit-to-po")
    public ResponseEntity<?> submitJudgmentToPo(@PathVariable("caseId") Long caseId, Principal principal) {
        try {
            CaseJudgmentWorkflowResponse result = caseProceedingService.submitJudgmentToPo(caseId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/judgment/finalize")
    public ResponseEntity<?> finalizeJudgmentDraft(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseJudgmentDraftRequest body,
            Principal principal
    ) {
        try {
            CaseJudgmentWorkflowResponse result = caseProceedingService.finalizeJudgmentDraft(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/judgment/revert-to-clerk")
    public ResponseEntity<?> revertJudgmentToClerk(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseWorkflowRevertRequest body,
            Principal principal
    ) {
        try {
            CaseJudgmentWorkflowResponse result = caseProceedingService.revertJudgmentToClerk(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/judgment/publish")
    public ResponseEntity<?> publishJudgment(@PathVariable("caseId") Long caseId, Principal principal) {
        try {
            return ResponseEntity.ok(caseProceedingService.publishJudgment(caseId, principal));
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

    @PostMapping("/{caseId}/hearings/{hearingId}/reschedule")
    public ResponseEntity<?> rescheduleHearing(
            @PathVariable("caseId") Long caseId,
            @PathVariable("hearingId") Long hearingId,
            @RequestBody CaseHearingRescheduleRequest body,
            Principal principal
    ) {
        try {
            CaseHearingResponse result = caseProceedingService.rescheduleHearingAfterAdjourn(
                    caseId, hearingId, body, principal
            );
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

    @GetMapping("/{caseId}/hearings/{hearingId}/attendance")
    public ResponseEntity<?> getHearingAttendance(
            @PathVariable("caseId") Long caseId,
            @PathVariable("hearingId") Long hearingId,
            Principal principal
    ) {
        try {
            return ResponseEntity.ok(caseProceedingService.getHearingAttendance(caseId, hearingId, principal));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{caseId}/hearings/{hearingId}/attendance")
    public ResponseEntity<?> saveHearingAttendance(
            @PathVariable("caseId") Long caseId,
            @PathVariable("hearingId") Long hearingId,
            @RequestBody HearingAttendanceSaveRequest body,
            Principal principal
    ) {
        try {
            return ResponseEntity.ok(caseProceedingService.saveHearingAttendance(caseId, hearingId, body, principal));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}/roznama")
    public ResponseEntity<?> getRoznama(
            @PathVariable("caseId") Long caseId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            @RequestParam(name = "hearingDate", required = false) String hearingDate,
            Principal principal
    ) {
        try {
            LocalDate parsedDate = hearingDate != null && !hearingDate.isBlank() ? LocalDate.parse(hearingDate.trim()) : null;
            RoznamaResponse result = caseProceedingService.getRoznama(caseId, hearingId, parsedDate, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/roznama")
    public ResponseEntity<?> saveAndSignRoznama(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseRoznamaCompleteRequest body,
            Principal principal
    ) {
        try {
            CaseRoznamaCompleteResponse result = caseProceedingService.completeRoznama(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{caseId}/roznama/history")
    public ResponseEntity<?> getRoznamaHistory(
            @PathVariable("caseId") Long caseId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            Principal principal
    ) {
        try {
            List<CaseOrderSheetHistoryResponse> result = caseProceedingService.getRoznamaHistory(caseId, hearingId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
