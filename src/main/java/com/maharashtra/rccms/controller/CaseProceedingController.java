package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.caseflow.CaseHearingResponse;
import com.maharashtra.rccms.dto.caseflow.CaseHearingScheduleRequest;
import com.maharashtra.rccms.dto.caseflow.CaseInboxItemResponse;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentRequest;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerDashboardResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetHistoryResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerCauseListItemResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerRoznamaTableResponse;
import com.maharashtra.rccms.dto.caseflow.RoznamaHearingContextRequest;
import com.maharashtra.rccms.dto.caseflow.RoznamaResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetFinalizeRequest;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetSignRequest;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetUpsertRequest;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeDraftRequest;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeFinalizeRequest;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeResponse;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeSignRequest;
import com.maharashtra.rccms.dto.caseflow.CaseWorkflowActionResponse;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentDraftRequest;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentWorkflowResponse;
import com.maharashtra.rccms.dto.caseflow.CaseWorkflowRevertRequest;
import com.maharashtra.rccms.dto.filing.OfficerApplicationDetailResponse;
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

    @GetMapping("/cause-list")
    public ResponseEntity<?> listCauseList(
            @RequestParam(name = "date", required = false) String date,
            Principal principal
    ) {
        try {
            LocalDate causeDate = date != null && !date.isBlank() ? LocalDate.parse(date.trim()) : null;
            List<OfficerCauseListItemResponse> result = caseProceedingService.listCauseList(principal, causeDate);
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

    @PostMapping("/{caseId}/notices/draft")
    public ResponseEntity<?> draftNotice(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseNoticeDraftRequest body,
            Principal principal
    ) {
        try {
            CaseWorkflowActionResponse result = caseProceedingService.draftNotice(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/notices")
    public ResponseEntity<?> draftNoticeCompat(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseNoticeDraftRequest body,
            Principal principal
    ) {
        try {
            CaseWorkflowActionResponse result = caseProceedingService.draftNotice(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/notices/save-draft")
    public ResponseEntity<?> draftNoticeSaveDraftAlias(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseNoticeDraftRequest body,
            Principal principal
    ) {
        try {
            CaseWorkflowActionResponse result = caseProceedingService.draftNotice(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/notices/{noticeId}/submit-to-po")
    public ResponseEntity<?> submitNoticeToPo(
            @PathVariable("caseId") Long caseId,
            @PathVariable("noticeId") Long noticeId,
            Principal principal
    ) {
        try {
            CaseWorkflowActionResponse result = caseProceedingService.submitNoticeToPo(caseId, noticeId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/notices/{noticeId}/finalize")
    public ResponseEntity<?> finalizeNotice(
            @PathVariable("caseId") Long caseId,
            @PathVariable("noticeId") Long noticeId,
            @RequestBody(required = false) CaseNoticeFinalizeRequest body,
            Principal principal
    ) {
        try {
            CaseWorkflowActionResponse result = caseProceedingService.finalizeNotice(caseId, noticeId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/notices/{noticeId}/sign")
    public ResponseEntity<?> signNotice(
            @PathVariable("caseId") Long caseId,
            @PathVariable("noticeId") Long noticeId,
            @RequestBody CaseNoticeSignRequest body,
            Principal principal
    ) {
        try {
            CaseWorkflowActionResponse result = caseProceedingService.signNotice(caseId, noticeId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/notices/{noticeId}/revert-to-clerk")
    public ResponseEntity<?> revertNoticeToClerk(
            @PathVariable("caseId") Long caseId,
            @PathVariable("noticeId") Long noticeId,
            @RequestBody CaseWorkflowRevertRequest body,
            Principal principal
    ) {
        try {
            CaseWorkflowActionResponse result = caseProceedingService.revertNoticeToClerk(caseId, noticeId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/notices/{noticeId}/serve")
    public ResponseEntity<?> serveNotice(
            @PathVariable("caseId") Long caseId,
            @PathVariable("noticeId") Long noticeId,
            Principal principal
    ) {
        try {
            CaseWorkflowActionResponse result = caseProceedingService.serveNotice(caseId, noticeId, principal);
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

    @PostMapping("/{caseId}/ordersheet/submit-to-po")
    public ResponseEntity<?> submitOrderSheetToPo(@PathVariable("caseId") Long caseId, Principal principal) {
        try {
            CaseOrderSheetResponse result = caseProceedingService.submitOrderSheetToPo(caseId, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/ordersheet/finalize")
    public ResponseEntity<?> finalizeOrderSheet(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseOrderSheetFinalizeRequest body,
            Principal principal
    ) {
        try {
            CaseOrderSheetResponse result = caseProceedingService.finalizeOrderSheet(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/ordersheet/sign")
    public ResponseEntity<?> signOrderSheet(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseOrderSheetSignRequest body,
            Principal principal
    ) {
        try {
            CaseOrderSheetResponse result = caseProceedingService.signOrderSheet(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/ordersheet/revert-to-clerk")
    public ResponseEntity<?> revertOrderSheetToClerk(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseWorkflowRevertRequest body,
            Principal principal
    ) {
        try {
            CaseOrderSheetResponse result = caseProceedingService.revertOrderSheetToClerk(caseId, body, principal);
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

    @PutMapping("/{caseId}/roznama")
    @PostMapping("/{caseId}/roznama/draft")
    public ResponseEntity<?> draftRoznama(
            @PathVariable("caseId") Long caseId,
            @RequestBody CaseOrderSheetUpsertRequest body,
            Principal principal
    ) {
        try {
            RoznamaResponse result = caseProceedingService.draftRoznama(caseId, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/roznama/submit-to-po")
    public ResponseEntity<?> submitRoznamaToPo(
            @PathVariable("caseId") Long caseId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            @RequestParam(name = "hearingDate", required = false) String hearingDate,
            @RequestBody(required = false) RoznamaHearingContextRequest body,
            Principal principal
    ) {
        try {
            RoznamaResponse result = caseProceedingService.submitRoznamaToPo(
                    caseId,
                    null,
                    mergeHearingId(hearingId, body),
                    mergeHearingDate(hearingDate, body),
                    principal
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/roznama/finalize")
    public ResponseEntity<?> finalizeRoznama(
            @PathVariable("caseId") Long caseId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            @RequestParam(name = "hearingDate", required = false) String hearingDate,
            @RequestBody(required = false) CaseOrderSheetFinalizeRequest body,
            Principal principal
    ) {
        try {
            LocalDate parsedDate = hearingDate != null && !hearingDate.isBlank() ? LocalDate.parse(hearingDate.trim()) : null;
            RoznamaResponse result = caseProceedingService.finalizeRoznama(
                    caseId,
                    null,
                    hearingId,
                    parsedDate,
                    body,
                    principal
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/roznama/sign")
    public ResponseEntity<?> signRoznama(
            @PathVariable("caseId") Long caseId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            @RequestParam(name = "hearingDate", required = false) String hearingDate,
            @RequestBody CaseOrderSheetSignRequest body,
            Principal principal
    ) {
        try {
            LocalDate parsedDate = hearingDate != null && !hearingDate.isBlank() ? LocalDate.parse(hearingDate.trim()) : null;
            RoznamaResponse result = caseProceedingService.signRoznama(
                    caseId,
                    null,
                    hearingId,
                    parsedDate,
                    body,
                    principal
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/roznama/revert-to-clerk")
    public ResponseEntity<?> revertRoznamaToClerk(
            @PathVariable("caseId") Long caseId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            @RequestParam(name = "hearingDate", required = false) String hearingDate,
            @RequestBody CaseWorkflowRevertRequest body,
            Principal principal
    ) {
        try {
            LocalDate parsedDate = hearingDate != null && !hearingDate.isBlank() ? LocalDate.parse(hearingDate.trim()) : null;
            RoznamaResponse result = caseProceedingService.revertRoznamaToClerk(
                    caseId,
                    null,
                    hearingId,
                    parsedDate,
                    body,
                    principal
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/roznama/{roznamaId}/finalize")
    public ResponseEntity<?> finalizeRoznamaById(
            @PathVariable("caseId") Long caseId,
            @PathVariable("roznamaId") Long roznamaId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            @RequestParam(name = "hearingDate", required = false) String hearingDate,
            @RequestBody(required = false) CaseOrderSheetFinalizeRequest body,
            Principal principal
    ) {
        try {
            LocalDate parsedDate = hearingDate != null && !hearingDate.isBlank() ? LocalDate.parse(hearingDate.trim()) : null;
            RoznamaResponse result = caseProceedingService.finalizeRoznama(
                    caseId,
                    roznamaId,
                    hearingId,
                    parsedDate,
                    body,
                    principal
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/roznama/{roznamaId}/submit-to-po")
    public ResponseEntity<?> submitRoznamaToPoById(
            @PathVariable("caseId") Long caseId,
            @PathVariable("roznamaId") Long roznamaId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            @RequestParam(name = "hearingDate", required = false) String hearingDate,
            Principal principal
    ) {
        try {
            LocalDate parsedDate = hearingDate != null && !hearingDate.isBlank() ? LocalDate.parse(hearingDate.trim()) : null;
            RoznamaResponse result = caseProceedingService.submitRoznamaToPo(caseId, roznamaId, hearingId, parsedDate, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/roznama/{roznamaId}/sign")
    public ResponseEntity<?> signRoznamaById(
            @PathVariable("caseId") Long caseId,
            @PathVariable("roznamaId") Long roznamaId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            @RequestParam(name = "hearingDate", required = false) String hearingDate,
            @RequestBody CaseOrderSheetSignRequest body,
            Principal principal
    ) {
        try {
            LocalDate parsedDate = hearingDate != null && !hearingDate.isBlank() ? LocalDate.parse(hearingDate.trim()) : null;
            RoznamaResponse result = caseProceedingService.signRoznama(caseId, roznamaId, hearingId, parsedDate, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{caseId}/roznama/{roznamaId}/revert-to-clerk")
    public ResponseEntity<?> revertRoznamaToClerkById(
            @PathVariable("caseId") Long caseId,
            @PathVariable("roznamaId") Long roznamaId,
            @RequestParam(name = "hearingId", required = false) Long hearingId,
            @RequestParam(name = "hearingDate", required = false) String hearingDate,
            @RequestBody CaseWorkflowRevertRequest body,
            Principal principal
    ) {
        try {
            LocalDate parsedDate = hearingDate != null && !hearingDate.isBlank() ? LocalDate.parse(hearingDate.trim()) : null;
            RoznamaResponse result = caseProceedingService.revertRoznamaToClerk(caseId, roznamaId, hearingId, parsedDate, body, principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private static Long mergeHearingId(Long queryHearingId, RoznamaHearingContextRequest body) {
        if (queryHearingId != null) {
            return queryHearingId;
        }
        return body != null ? body.getHearingId() : null;
    }

    private static LocalDate mergeHearingDate(String queryHearingDate, RoznamaHearingContextRequest body) {
        if (queryHearingDate != null && !queryHearingDate.isBlank()) {
            return LocalDate.parse(queryHearingDate.trim());
        }
        return body != null ? body.getHearingDate() : null;
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
