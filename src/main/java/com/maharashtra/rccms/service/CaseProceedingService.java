package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.caseflow.CaseHearingResponse;
import com.maharashtra.rccms.dto.caseflow.CaseHearingRescheduleRequest;
import com.maharashtra.rccms.dto.caseflow.CaseHearingScheduleRequest;
import com.maharashtra.rccms.model.caseflow.HearingOutcome;
import com.maharashtra.rccms.dto.caseflow.CaseInboxItemResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerDashboardResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetHistoryResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerCauseListItemResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerRoznamaTableResponse;
import com.maharashtra.rccms.dto.caseflow.CaseRoznamaCompleteRequest;
import com.maharashtra.rccms.dto.caseflow.CaseRoznamaCompleteResponse;
import com.maharashtra.rccms.dto.caseflow.HearingAttendanceResponse;
import com.maharashtra.rccms.dto.caseflow.HearingAttendanceSaveRequest;
import com.maharashtra.rccms.dto.caseflow.RoznamaResponse;
import com.maharashtra.rccms.dto.caseflow.RoznamaTableEntryResponse;
import com.maharashtra.rccms.filing.RoznamaContentHelper;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetFinalizeRequest;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetSignRequest;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetUpsertRequest;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeResponse;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeServeToPartyRequest;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeServeToPartyResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerNoticeServeQueueItemResponse;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentDraftRequest;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentSignPublishRequest;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentResponse;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentWorkflowResponse;
import com.maharashtra.rccms.dto.caseflow.CaseWorkflowRevertRequest;
import com.maharashtra.rccms.dto.workflow.CaseWorkflowContextResponse;
import com.maharashtra.rccms.dto.workflow.NoticeTemplateResolvedResponse;
import com.maharashtra.rccms.model.Employee;
import com.maharashtra.rccms.model.EmployeePosting;
import com.maharashtra.rccms.dto.filing.OfficerApplicationDetailResponse;
import com.maharashtra.rccms.model.caseflow.CaseHearing;
import com.maharashtra.rccms.model.caseflow.CaseNotice;
import com.maharashtra.rccms.model.caseflow.CaseNoticeStatus;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheet;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheetStatus;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheetHistory;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflow;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflowStatus;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.repository.CaseHearingRepository;
import com.maharashtra.rccms.repository.CaseNoticeRepository;
import com.maharashtra.rccms.repository.CaseJudgmentWorkflowRepository;
import com.maharashtra.rccms.repository.CaseOrderSheetHistoryRepository;
import com.maharashtra.rccms.repository.CaseOrderSheetRepository;
import com.maharashtra.rccms.repository.CaseRegistryRepository;
import com.maharashtra.rccms.repository.EmployeePostingRepository;
import com.maharashtra.rccms.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.maharashtra.rccms.workflow.WorkflowAction;

@Service
@SuppressWarnings("null")
public class CaseProceedingService {

    private final CaseRegistryRepository caseRegistryRepository;
    private final CaseHearingRepository caseHearingRepository;
    private final CaseNoticeRepository caseNoticeRepository;
    private final CaseJudgmentWorkflowRepository caseJudgmentWorkflowRepository;
    private final CaseOrderSheetRepository caseOrderSheetRepository;
    private final CaseOrderSheetHistoryRepository caseOrderSheetHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeePostingRepository employeePostingRepository;
    private final FilingApplicationService filingApplicationService;
    private final WorkflowPolicyService workflowPolicyService;
    private final WorkflowContextService workflowContextService;
    private final NoticeTemplateService noticeTemplateService;
    private final JudgmentWorkflowHistoryService judgmentWorkflowHistoryService;
    private final CaseRegistryStatusSyncService caseRegistryStatusSyncService;
    private final CaseHearingAttendanceService caseHearingAttendanceService;

    public CaseProceedingService(
            CaseRegistryRepository caseRegistryRepository,
            CaseHearingRepository caseHearingRepository,
            CaseNoticeRepository caseNoticeRepository,
            CaseJudgmentWorkflowRepository caseJudgmentWorkflowRepository,
            CaseOrderSheetRepository caseOrderSheetRepository,
            CaseOrderSheetHistoryRepository caseOrderSheetHistoryRepository,
            EmployeeRepository employeeRepository,
            EmployeePostingRepository employeePostingRepository,
            FilingApplicationService filingApplicationService,
            WorkflowPolicyService workflowPolicyService,
            WorkflowContextService workflowContextService,
            NoticeTemplateService noticeTemplateService,
            JudgmentWorkflowHistoryService judgmentWorkflowHistoryService,
            CaseRegistryStatusSyncService caseRegistryStatusSyncService,
            CaseHearingAttendanceService caseHearingAttendanceService
    ) {
        this.caseRegistryRepository = caseRegistryRepository;
        this.caseHearingRepository = caseHearingRepository;
        this.caseNoticeRepository = caseNoticeRepository;
        this.caseJudgmentWorkflowRepository = caseJudgmentWorkflowRepository;
        this.caseOrderSheetRepository = caseOrderSheetRepository;
        this.caseOrderSheetHistoryRepository = caseOrderSheetHistoryRepository;
        this.employeeRepository = employeeRepository;
        this.employeePostingRepository = employeePostingRepository;
        this.filingApplicationService = filingApplicationService;
        this.workflowPolicyService = workflowPolicyService;
        this.workflowContextService = workflowContextService;
        this.noticeTemplateService = noticeTemplateService;
        this.judgmentWorkflowHistoryService = judgmentWorkflowHistoryService;
        this.caseRegistryStatusSyncService = caseRegistryStatusSyncService;
        this.caseHearingAttendanceService = caseHearingAttendanceService;
    }

    @Transactional(readOnly = true)
    public CaseWorkflowContextResponse getWorkflowContext(Long caseId, Long hearingId, Principal principal) {
        return workflowContextService.buildCaseContext(caseId, hearingId, principal);
    }

    @Transactional(readOnly = true)
    public NoticeTemplateResolvedResponse resolveNoticeTemplate(
            Long caseId,
            String noticeType,
            Long hearingId,
            String partyNamesBlock,
            Principal principal
    ) {
        String login = normalizeLogin(principal);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        LocalDate hearingDate = null;
        if (hearingId != null) {
            CaseHearing hearing = caseHearingRepository.findById(hearingId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid hearingId."));
            if (!Objects.equals(hearing.getCaseRegistry().getId(), caseId)) {
                throw new IllegalArgumentException("hearingId does not belong to case.");
            }
            hearingDate = hearing.getHearingDate();
        }
        return noticeTemplateService.resolveForCase(caseRow, noticeType, hearingDate, partyNamesBlock);
    }

    @Transactional(readOnly = true)
    public OfficerDashboardResponse getOfficerDashboard(Principal principal) {
        OfficerDashboardResponse out = new OfficerDashboardResponse();
        out.setPendingApplications(filingApplicationService.listOfficerInbox(principal));
        out.setActiveCases(listCaseInbox(principal, null));
        out.setTodayHearings(listTodayHearings(principal));
        return out;
    }

    @Transactional(readOnly = true)
    public List<CaseInboxItemResponse> listCaseInbox(Principal principal, String status) {
        String login = normalizeLogin(principal);
        Long officeId = resolveOfficerCurrentOfficeId(login);
        List<CaseRegistry> rows;
        String s = trimToNull(status);
        if (s == null) {
            rows = caseRegistryRepository.findByOfficeIdOrderByApprovedAtDescIdDesc(officeId);
        } else {
            rows = caseRegistryRepository.findByOfficeIdAndStatusIgnoreCaseOrderByApprovedAtDescIdDesc(officeId, s);
        }
        List<CaseInboxItemResponse> out = new ArrayList<>();
        List<Long> caseIds = new ArrayList<>();
        for (CaseRegistry row : rows) {
            if (s == null && "DISPOSED".equalsIgnoreCase(row.getStatus())) {
                continue;
            }
            caseIds.add(row.getId());
        }
        Map<Long, CaseOrderSheet> roznamaByCaseId = loadRoznamaByCaseIds(caseIds);
        Map<Long, CaseHearing> latestHearingByCaseId = loadLatestHearingByCaseIds(caseIds);
        for (CaseRegistry row : rows) {
            if (s == null && "DISPOSED".equalsIgnoreCase(row.getStatus())) {
                continue;
            }
            CaseHearing latestHearing = latestHearingByCaseId.get(row.getId());
            boolean noticeServed = caseRegistryStatusSyncService.isNoticeServedForCase(row.getId(), latestHearing);
            if (noticeServed) {
                caseRegistryStatusSyncService.syncNoticeServedIfNeeded(
                        row.getId(),
                        latestHearing != null ? latestHearing.getId() : null
                );
            }
            out.add(toCaseInboxItem(
                    row,
                    roznamaByCaseId.get(row.getId()),
                    latestHearing,
                    noticeServed
            ));
        }
        return out;
    }

    /**
     * All hearings in the officer's office where a hearing date is assigned and notice is not yet served
     * (Send notice to party menu). Not filtered by calendar date — use cause-list for a single day.
     */
    @Transactional(readOnly = true)
    public List<OfficerNoticeServeQueueItemResponse> listPendingNoticeServe(Principal principal) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        Long officeId = resolveOfficerCurrentOfficeId(login);
        List<CaseHearing> hearings = caseHearingRepository.findPendingNoticeServeByOfficeId(officeId);

        List<OfficerNoticeServeQueueItemResponse> out = new ArrayList<>();
        int rowNo = 1;
        for (CaseHearing hearing : hearings) {
            CaseRegistry caseRow = hearing.getCaseRegistry();
            if (caseRow == null) {
                continue;
            }
            CaseNotice activeNotice = findActiveNoticeForHearing(hearing.getId());

            OfficerNoticeServeQueueItemResponse item = new OfficerNoticeServeQueueItemResponse();
            item.setRowNo(rowNo++);
            item.setQueueDate(hearing.getHearingDate());
            item.setCaseId(caseRow.getId());
            item.setCaseNo(caseRow.getCaseNo());
            item.setCaseStatus(caseRow.getStatus());
            item.setFilingApplicationId(caseRow.getFilingApplicationId());
            item.setCaseCategoryName(caseRow.getCaseCategory() != null ? caseRow.getCaseCategory().getName() : null);
            item.setHearingId(hearing.getId());
            item.setHearingNo(hearing.getHearingNo());
            item.setHearingDate(hearing.getHearingDate());
            item.setHearingStatus(hearing.getStatus());
            if (activeNotice != null) {
                item.setNoticeId(activeNotice.getId());
                item.setNoticeStatus(activeNotice.getStatus() != null ? activeNotice.getStatus().name() : null);
            }
            item.setAllowedActions(workflowPolicyService.noticeAllowed(caseRow, posting, hearing, activeNotice));
            out.add(item);
        }
        return out;
    }

    @Transactional(readOnly = true)
    public OfficerRoznamaTableResponse listRoznamaTable(Principal principal, LocalDate date) {
        String login = normalizeLogin(principal);
        Long officeId = resolveOfficerCurrentOfficeId(login);
        LocalDate hearingDate = date != null ? date : LocalDate.now();
        List<CaseHearing> hearings = caseHearingRepository.findByCaseRegistryOfficeIdAndHearingDateOrderByCaseRegistryIdAscHearingNoAsc(
                officeId,
                hearingDate
        );
        List<Long> caseIds = new ArrayList<>();
        for (CaseHearing hearing : hearings) {
            if (hearing.getCaseRegistry() != null && hearing.getCaseRegistry().getId() != null) {
                caseIds.add(hearing.getCaseRegistry().getId());
            }
        }
        Map<Long, CaseOrderSheet> roznamaByCaseId = loadRoznamaByCaseIds(caseIds);
        Map<Long, List<CaseOrderSheetHistory>> historyByCaseId = loadRoznamaHistoryByCaseIds(caseIds);

        List<OfficerCauseListItemResponse> rows = new ArrayList<>();
        int rowNo = 1;
        for (CaseHearing hearing : hearings) {
            CaseRegistry caseRow = hearing.getCaseRegistry();
            if (caseRow == null) {
                continue;
            }
            OfficerCauseListItemResponse item = new OfficerCauseListItemResponse();
            item.setRowNo(rowNo++);
            item.setCauseDate(hearingDate);
            item.setCaseId(caseRow.getId());
            item.setCaseNo(caseRow.getCaseNo());
            item.setCaseStatus(caseRow.getStatus());
            item.setFilingApplicationId(caseRow.getFilingApplicationId());
            item.setCaseCategoryName(caseRow.getCaseCategory() != null ? caseRow.getCaseCategory().getName() : null);
            item.setHearingId(hearing.getId());
            item.setHearingNo(hearing.getHearingNo());
            item.setHearingDate(hearing.getHearingDate());
            item.setHearingStatus(hearing.getStatus());
            boolean noticeServed = Boolean.TRUE.equals(hearing.getNoticeServed());
            item.setNoticeServed(noticeServed);
            item.setProceedingAllowed(noticeServed);
            CaseOrderSheet sheet = roznamaByCaseId.get(caseRow.getId());
            List<CaseOrderSheetHistory> histories = historyByCaseId.getOrDefault(caseRow.getId(), Collections.emptyList());
            applyRoznamaFieldsForHearing(item, sheet, histories, hearing);
            rows.add(item);
        }

        OfficerRoznamaTableResponse table = new OfficerRoznamaTableResponse();
        table.setHearingDate(hearingDate);
        table.setTotalRows(rows.size());
        table.setRows(rows);
        return table;
    }

    @Transactional(readOnly = true)
    public OfficerApplicationDetailResponse getOfficerCaseDetail(Long caseId, Principal principal) {
        return filingApplicationService.getOfficerCaseDetail(caseId, principal);
    }

    @Transactional(readOnly = true)
    public List<CaseNoticeResponse> listCaseNotices(Long caseId, Principal principal) {
        String login = normalizeLogin(principal);
        resolveOfficerCase(caseId, login);
        List<CaseNoticeResponse> out = new ArrayList<>();
        for (CaseNotice row : caseNoticeRepository.findByCaseRegistryIdOrderByIdDesc(caseId)) {
            out.add(toCaseNoticeResponse(row));
        }
        return out;
    }

    /**
     * One-shot serve: saves notice content and selected parties, then internally finalizes,
     * digitally signs, and serves (PO_FINALIZED → PO_SIGNED → SERVED, hearing.noticeServed = true).
     */
    @Transactional
    public CaseNoticeServeToPartyResponse serveNoticeToParty(
            Long caseId,
            CaseNoticeServeToPartyRequest request,
            Principal principal
    ) {
        if (request == null || request.getHearingId() == null) {
            throw new IllegalArgumentException("hearingId is required.");
        }
        if (request.getSelectedParties() == null || request.getSelectedParties().isEmpty()) {
            throw new IllegalArgumentException("selectedParties is required (at least one party).");
        }
        String content = trimToNull(request.getDraftContent());
        if (content == null) {
            content = trimToNull(request.getFinalContent());
        }
        if (content == null) {
            throw new IllegalArgumentException("draftContent (notice body) is required.");
        }

        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseHearing hearing = caseHearingRepository.findById(request.getHearingId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid hearingId."));
        if (!Objects.equals(hearing.getCaseRegistry().getId(), caseId)) {
            throw new IllegalArgumentException("hearingId does not belong to case.");
        }
        if (hearing.getHearingDate() == null) {
            throw new IllegalArgumentException("Hearing date must be assigned before serving notice to parties.");
        }
        if (Boolean.TRUE.equals(hearing.getNoticeServed())) {
            throw new IllegalArgumentException("Notice is already served for this hearing.");
        }

        String noticeType = trimToNull(request.getNoticeType()) != null
                ? request.getNoticeType().trim()
                : "HEARING_NOTICE";
        CaseNotice row = resolveOrCreateActiveNotice(caseRow, hearing, login, noticeType);
        requireCaseAction(caseRow, posting, hearing, row,
                caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null),
                caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null),
                WorkflowAction.SERVE_NOTICE_TO_PARTY);

        if (row.getStatus() == CaseNoticeStatus.SERVED) {
            throw new IllegalArgumentException("Notice already served for this hearing.");
        }

        row.setDraftContent(content);
        row.setSelectedPartiesJson(toJsonArray(request.getSelectedParties()));
        if (trimToNull(row.getNoticeType()) == null) {
            row.setNoticeType(noticeType);
        }

        boolean noticeFinalized = false;
        boolean noticeSigned = false;

        if (row.getStatus() == CaseNoticeStatus.PO_DRAFT
                || row.getStatus() == CaseNoticeStatus.CLERK_DRAFT
                || row.getStatus() == CaseNoticeStatus.PO_SCRUTINY) {
            row.setFinalContent(content);
            row.setPoFinalizedByLoginId(login);
            row.setStatus(CaseNoticeStatus.PO_FINALIZED);
            noticeFinalized = true;
        } else if (row.getStatus() == CaseNoticeStatus.PO_FINALIZED) {
            noticeFinalized = true;
            if (trimToNull(row.getFinalContent()) == null) {
                row.setFinalContent(content);
                row.setPoFinalizedByLoginId(login);
            }
        }

        if (row.getStatus() == CaseNoticeStatus.PO_FINALIZED) {
            String sigRef = trimToNull(request.getDigitalSignatureRef());
            if (sigRef == null) {
                sigRef = "PO-SERVE-" + caseId + "-" + hearing.getId() + "-" + System.currentTimeMillis();
            }
            row.setDigitalSignatureRef(sigRef);
            row.setPoSignedByLoginId(login);
            row.setStatus(CaseNoticeStatus.PO_SIGNED);
            noticeSigned = true;
        } else if (row.getStatus() == CaseNoticeStatus.PO_SIGNED) {
            noticeSigned = true;
            if (trimToNull(row.getDigitalSignatureRef()) == null) {
                String sigRef = trimToNull(request.getDigitalSignatureRef());
                if (sigRef == null) {
                    sigRef = "PO-SERVE-" + caseId + "-" + hearing.getId() + "-" + System.currentTimeMillis();
                }
                row.setDigitalSignatureRef(sigRef);
                row.setPoSignedByLoginId(login);
            }
        }

        Instant servedAt = Instant.now();
        row.setStatus(CaseNoticeStatus.SERVED);
        row.setServedAt(servedAt);
        row.setServedByLoginId(login);
        row = caseNoticeRepository.save(row);

        hearing.setNoticeServed(true);
        caseHearingRepository.save(hearing);
        caseRow.setStatus("NOTICE_SERVED");
        caseRegistryRepository.save(caseRow);

        CaseNoticeServeToPartyResponse out = new CaseNoticeServeToPartyResponse();
        out.setCaseId(caseId);
        out.setHearingId(hearing.getId());
        out.setNoticeId(row.getId());
        out.setStatus(row.getStatus().name());
        out.setSelectedParties(parseJsonArray(row.getSelectedPartiesJson()));
        out.setDigitalSignatureRef(row.getDigitalSignatureRef());
        out.setServedAt(servedAt);
        out.setNoticeFinalized(noticeFinalized || trimToNull(row.getFinalContent()) != null);
        out.setNoticeSigned(noticeSigned);
        out.setNoticeServed(true);
        out.setMessage(
                "Notice served to selected parties for hearing #"
                        + hearing.getHearingNo()
                        + " (template applied, digitally signed)."
        );
        return out;
    }

    @Transactional(readOnly = true)
    public CaseJudgmentWorkflowResponse getJudgmentWorkflow(Long caseId, Principal principal) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null);
        return enrichJudgmentResponse(caseRow, row, posting);
    }

    @Transactional
    public CaseJudgmentWorkflowResponse saveJudgmentDraft(Long caseId, CaseJudgmentDraftRequest request, Principal principal) {
        String summary = request != null ? request.resolveSummary() : null;
        if (trimToNull(summary) == null) {
            throw new IllegalArgumentException(
                    "summary is required (use summary, draftSummary, judgmentSummary, or content)."
            );
        }
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElseGet(CaseJudgmentWorkflow::new);
        if (row.getCaseRegistry() == null) {
            row.setCaseRegistry(caseRow);
        }
        if (row.getStatus() == CaseJudgmentWorkflowStatus.PUBLISHED) {
            throw new IllegalArgumentException("Case judgment is already published.");
        }

        CaseJudgmentWorkflowStatus from = row.getStatus();
        boolean po = workflowPolicyService.isPo(posting);
        boolean poThenClerk = "PO_THEN_CLERK".equalsIgnoreCase(
                workflowPolicyService.definitionFor(caseRow).getJudgment().getMode());
        if (po) {
            boolean newRow = row.getId() == null;
            boolean poMayEditClerkDraft = poThenClerk && from == CaseJudgmentWorkflowStatus.CLERK_DRAFT;
            if (from != null
                    && from != CaseJudgmentWorkflowStatus.PO_DRAFT
                    && from != CaseJudgmentWorkflowStatus.PO_SCRUTINY
                    && !poMayEditClerkDraft) {
                throw new IllegalArgumentException(
                        "PO can edit judgment draft only before sending to clerk, while clerk is drafting, or while reviewing clerk draft (PO_SCRUTINY)."
                );
            }
            boolean poDraftPhase = newRow || from == null || from == CaseJudgmentWorkflowStatus.PO_DRAFT;
            WorkflowAction draftAction = poDraftPhase
                    ? WorkflowAction.PO_DRAFT_JUDGMENT
                    : WorkflowAction.UPDATE_PO_JUDGMENT;
            requireCaseAction(caseRow, posting, null, null,
                    caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null),
                    row,
                    draftAction);
            row.setDraftSummary(summary.trim());
            row.setDraftedByLoginId(login);
            if (poDraftPhase) {
                row.setStatus(CaseJudgmentWorkflowStatus.PO_DRAFT);
            }
            row = caseJudgmentWorkflowRepository.save(row);
            recordJudgmentHistory(caseRow, row, from, row.getStatus(), draftAction, summary, null, "PRESIDING_OFFICER", login);
            return enrichJudgmentResponse(caseRow, row, posting);
        }

        assertClerk(posting);
        if (row.getStatus() != CaseJudgmentWorkflowStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException(
                    "Judgment can be edited by clerk only after PO sends the draft (workflowStatus must be CLERK_DRAFT)."
            );
        }
        requireCaseAction(caseRow, posting, null, null,
                caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null),
                row,
                WorkflowAction.CLERK_UPDATE_JUDGMENT);
        row.setDraftSummary(summary.trim());
        row.setDraftedByLoginId(login);
        row = caseJudgmentWorkflowRepository.save(row);
        recordJudgmentHistory(caseRow, row, from, row.getStatus(), WorkflowAction.CLERK_UPDATE_JUDGMENT, summary, null, "CLERK", login);
        return enrichJudgmentResponse(caseRow, row, posting);
    }

    @Transactional
    public CaseJudgmentWorkflowResponse sendJudgmentToClerk(Long caseId, CaseWorkflowRevertRequest request, Principal principal) {
        String remarks = trimToNull(request != null ? request.getRemarks() : null);
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Judgment draft not found. Save PO draft first."));
        requireCaseAction(caseRow, posting, null, null,
                caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null),
                row,
                WorkflowAction.SEND_JUDGMENT_TO_CLERK);
        if (row.getStatus() != CaseJudgmentWorkflowStatus.PO_DRAFT) {
            throw new IllegalArgumentException("Only PO draft judgment can be sent to clerk.");
        }
        CaseJudgmentWorkflowStatus from = row.getStatus();
        row.setStatus(CaseJudgmentWorkflowStatus.CLERK_DRAFT);
        row = caseJudgmentWorkflowRepository.save(row);
        recordJudgmentHistory(caseRow, row, from, row.getStatus(), WorkflowAction.SEND_JUDGMENT_TO_CLERK,
                row.getDraftSummary(), remarks, "PRESIDING_OFFICER", login);
        return enrichJudgmentResponse(caseRow, row, posting);
    }

    @Transactional
    public CaseJudgmentWorkflowResponse submitJudgmentToPo(Long caseId, Principal principal) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertClerk(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Judgment draft not found."));
        requireCaseAction(caseRow, posting, null, null,
                caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null),
                row,
                WorkflowAction.SUBMIT_JUDGMENT_TO_PO);
        if (row.getStatus() != CaseJudgmentWorkflowStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException(
                    "Only clerk draft judgment can be submitted to PO scrutiny (workflowStatus must be CLERK_DRAFT)."
            );
        }
        if (trimToNull(row.getDraftSummary()) == null) {
            throw new IllegalArgumentException(
                    "Judgment draft summary is required before submitting to PO review."
            );
        }
        CaseJudgmentWorkflowStatus from = row.getStatus();
        row.setStatus(CaseJudgmentWorkflowStatus.PO_SCRUTINY);
        row = caseJudgmentWorkflowRepository.save(row);
        recordJudgmentHistory(caseRow, row, from, row.getStatus(), WorkflowAction.SUBMIT_JUDGMENT_TO_PO,
                row.getDraftSummary(), null, "CLERK", login);
        return enrichJudgmentResponse(caseRow, row, posting);
    }

    @Transactional
    public CaseJudgmentWorkflowResponse finalizeJudgmentDraft(Long caseId, CaseJudgmentDraftRequest request, Principal principal) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Judgment draft not found."));
        requireCaseAction(caseRow, posting, null, null,
                caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null),
                row,
                WorkflowAction.FINALIZE_JUDGMENT);
        if (row.getStatus() != CaseJudgmentWorkflowStatus.PO_SCRUTINY) {
            throw new IllegalArgumentException("Only judgment under PO scrutiny can be finalized by PO.");
        }
        CaseJudgmentWorkflowStatus from = row.getStatus();
        String summary = request != null ? request.resolveSummary() : null;
        if (trimToNull(summary) == null) {
            summary = trimToNull(row.getDraftSummary());
        }
        if (trimToNull(summary) == null) {
            throw new IllegalArgumentException("Judgment summary is missing; cannot finalize.");
        }
        row.setFinalSummary(summary.trim());
        row.setFinalizedByLoginId(login);
        row.setStatus(CaseJudgmentWorkflowStatus.PO_FINALIZED);
        row = caseJudgmentWorkflowRepository.save(row);
        recordJudgmentHistory(caseRow, row, from, row.getStatus(), WorkflowAction.FINALIZE_JUDGMENT,
                summary, null, "PRESIDING_OFFICER", login);
        return enrichJudgmentResponse(caseRow, row, posting);
    }

    @Transactional
    public CaseJudgmentWorkflowResponse revertJudgmentToClerk(Long caseId, CaseWorkflowRevertRequest request, Principal principal) {
        String remarks = requiredText(request != null ? request.getRemarks() : null, "remarks");
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        if ("DISPOSED".equalsIgnoreCase(caseRow.getStatus())) {
            throw new IllegalArgumentException("Published/disposed judgment cannot be reverted.");
        }
        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Judgment workflow not found."));
        requireCaseAction(caseRow, posting, null, null,
                caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null),
                row,
                WorkflowAction.REVERT_JUDGMENT_TO_CLERK);
        if (row.getStatus() == CaseJudgmentWorkflowStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException("Judgment is still in clerk draft. Submit to PO scrutiny before revert.");
        }
        if (row.getStatus() == CaseJudgmentWorkflowStatus.PUBLISHED) {
            throw new IllegalArgumentException("Published judgment cannot be reverted.");
        }
        String latest = trimToNull(row.getFinalSummary());
        if (latest != null) {
            row.setDraftSummary(latest);
        }
        row.setFinalSummary(null);
        row.setFinalizedByLoginId(null);
        CaseJudgmentWorkflowStatus from = row.getStatus();
        row.setStatus(CaseJudgmentWorkflowStatus.CLERK_DRAFT);
        row = caseJudgmentWorkflowRepository.save(row);
        recordJudgmentHistory(caseRow, row, from, row.getStatus(), WorkflowAction.REVERT_JUDGMENT_TO_CLERK,
                row.getDraftSummary(), remarks, "PRESIDING_OFFICER", login);
        return enrichJudgmentResponse(caseRow, row, posting);
    }

    @Transactional(readOnly = true)
    public List<com.maharashtra.rccms.dto.workflow.JudgmentWorkflowHistoryResponse> listJudgmentHistory(
            Long caseId,
            Principal principal
    ) {
        String login = normalizeLogin(principal);
        resolveOfficerCase(caseId, login);
        return judgmentWorkflowHistoryService.listForCase(caseId);
    }

    @Transactional
    public CaseJudgmentResponse publishJudgment(Long caseId, Principal principal) {
        return publishJudgmentInternal(caseId, null, principal, WorkflowAction.PUBLISH_JUDGMENT);
    }

    /**
     * PO signs and publishes judgment (disposes case). From PO_SCRUTINY auto-finalizes first.
     */
    @Transactional
    public CaseJudgmentResponse signAndPublishJudgment(
            Long caseId,
            CaseJudgmentSignPublishRequest request,
            Principal principal
    ) {
        if (request == null || trimToNull(request.getDigitalSignatureRef()) == null) {
            throw new IllegalArgumentException("digitalSignatureRef is required.");
        }
        return publishJudgmentInternal(
                caseId,
                request,
                principal,
                WorkflowAction.SIGN_AND_PUBLISH_JUDGMENT
        );
    }

    private CaseJudgmentResponse publishJudgmentInternal(
            Long caseId,
            CaseJudgmentSignPublishRequest request,
            Principal principal,
            WorkflowAction action
    ) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Judgment workflow not found."));
        requireCaseAction(caseRow, posting, null, null,
                caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null),
                row,
                action);

        if (row.getStatus() == CaseJudgmentWorkflowStatus.PO_SCRUTINY) {
            if (action != WorkflowAction.SIGN_AND_PUBLISH_JUDGMENT) {
                throw new IllegalArgumentException("Only sign-and-publish is allowed from PO scrutiny without separate finalize.");
            }
            String summary = request != null ? request.resolveSummary() : null;
            if (trimToNull(summary) == null) {
                summary = trimToNull(row.getDraftSummary());
            }
            if (trimToNull(summary) == null) {
                throw new IllegalArgumentException("Judgment summary is missing; cannot sign and publish.");
            }
            CaseJudgmentWorkflowStatus fromScrutiny = row.getStatus();
            row.setFinalSummary(summary.trim());
            row.setFinalizedByLoginId(login);
            row.setStatus(CaseJudgmentWorkflowStatus.PO_FINALIZED);
            row = caseJudgmentWorkflowRepository.save(row);
            recordJudgmentHistory(caseRow, row, fromScrutiny, row.getStatus(), WorkflowAction.FINALIZE_JUDGMENT,
                    summary, request != null ? request.getRemarks() : null, "PRESIDING_OFFICER", login);
        } else if (row.getStatus() != CaseJudgmentWorkflowStatus.PO_FINALIZED) {
            throw new IllegalArgumentException(
                    "Judgment must be under PO scrutiny or PO finalized before publish/sign."
            );
        }

        String summary = request != null ? request.resolveSummary() : null;
        if (trimToNull(summary) == null) {
            summary = trimToNull(row.getFinalSummary());
        }
        if (trimToNull(summary) == null) {
            summary = trimToNull(row.getDraftSummary());
        }
        if (trimToNull(summary) == null) {
            throw new IllegalArgumentException("Final judgment summary is missing.");
        }
        summary = summary.trim();

        String signatureRef = request != null ? trimToNull(request.getDigitalSignatureRef()) : null;
        if (action == WorkflowAction.SIGN_AND_PUBLISH_JUDGMENT && signatureRef == null) {
            throw new IllegalArgumentException("digitalSignatureRef is required.");
        }
        if (signatureRef == null) {
            signatureRef = "PO-JUDGMENT-" + caseId + "-" + System.currentTimeMillis();
        }

        CaseJudgmentWorkflowStatus from = row.getStatus();
        row.setFinalSummary(summary);
        row.setPublishedSummary(summary);
        row.setPublishedByLoginId(login);
        row.setPublishedAt(Instant.now());
        row.setDigitalSignatureRef(signatureRef);
        row.setStatus(CaseJudgmentWorkflowStatus.PUBLISHED);
        caseJudgmentWorkflowRepository.save(row);
        recordJudgmentHistory(caseRow, row, from, row.getStatus(), action,
                summary, request != null ? request.getRemarks() : null, "PRESIDING_OFFICER", login);

        caseRow.setStatus("DISPOSED");
        caseRow.setDisposedAt(Instant.now());
        caseRow.setDisposedByOfficerLoginId(login);
        caseRow.setJudgmentSummary(summary);
        caseRegistryRepository.save(caseRow);

        CaseJudgmentResponse out = new CaseJudgmentResponse();
        out.setCaseId(caseRow.getId());
        out.setCaseNo(caseRow.getCaseNo());
        out.setStatus(caseRow.getStatus());
        out.setWorkflowStatus(CaseJudgmentWorkflowStatus.PUBLISHED.name());
        out.setJudgmentSummary(summary);
        out.setDigitalSignatureRef(signatureRef);
        out.setDisposedAt(caseRow.getDisposedAt());
        out.setMessage(action == WorkflowAction.SIGN_AND_PUBLISH_JUDGMENT
                ? "Judgment signed, published, and case disposed."
                : "Judgment published and case disposed.");
        return out;
    }

    @Transactional
    public CaseHearingResponse scheduleHearing(Long caseId, CaseHearingScheduleRequest request, Principal principal) {
        if (request == null || request.getHearingDate() == null) {
            throw new IllegalArgumentException("hearingDate is required.");
        }
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);
        requireCaseAction(caseRow, posting, null, null,
                caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null),
                caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null),
                WorkflowAction.SCHEDULE_HEARING);

        CaseHearing row = createNextScheduledHearing(
                caseRow,
                request.getHearingDate(),
                Boolean.TRUE.equals(request.getNoticeGenerate()),
                trimToNull(request.getRemarks()),
                login
        );
        if (!"HEARING_SCHEDULED".equalsIgnoreCase(caseRow.getStatus())) {
            caseRow.setStatus("HEARING_SCHEDULED");
            caseRegistryRepository.save(caseRow);
        }
        return toHearingResponse(row);
    }

    /**
     * After roznamma sign with outcome ADJOURN, schedule the next hearing (if nextHearingDate was not sent on sign).
     */
    @Transactional
    public CaseHearingResponse rescheduleHearingAfterAdjourn(
            Long caseId,
            Long hearingId,
            CaseHearingRescheduleRequest request,
            Principal principal
    ) {
        if (request == null || request.getNextHearingDate() == null) {
            throw new IllegalArgumentException("nextHearingDate is required.");
        }
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseHearing completedHearing = caseHearingRepository.findById(hearingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid hearingId."));
        if (!Objects.equals(completedHearing.getCaseRegistry().getId(), caseId)) {
            throw new IllegalArgumentException("hearingId does not belong to case.");
        }
        if (!"COMPLETED".equalsIgnoreCase(trimToNull(completedHearing.getStatus()))) {
            throw new IllegalArgumentException("Only a completed hearing can be adjourned to a new date.");
        }
        if (!hasSignedAdjournRoznammaForHearing(caseId, hearingId)) {
            throw new IllegalArgumentException(
                    "Roznamma must be signed with outcome ADJOURN for this hearing before scheduling the next date."
            );
        }

        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null);
        requireCaseAction(caseRow, posting, completedHearing, null, sheet,
                caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null),
                WorkflowAction.RESCHEDULE_HEARING);

        CaseHearing next = createNextScheduledHearing(
                caseRow,
                request.getNextHearingDate(),
                request.getNoticeGenerate() == null || Boolean.TRUE.equals(request.getNoticeGenerate()),
                trimToNull(request.getRemarks()),
                login
        );
        caseRow.setStatus("HEARING_SCHEDULED");
        caseRegistryRepository.save(caseRow);
        return toHearingResponse(next);
    }

    @Transactional(readOnly = true)
    public List<CaseHearingResponse> listCaseHearings(Long caseId, Principal principal) {
        String login = normalizeLogin(principal);
        resolveOfficerCase(caseId, login);
        List<CaseHearingResponse> out = new ArrayList<>();
        for (CaseHearing row : caseHearingRepository.findByCaseRegistryIdOrderByHearingNoAsc(caseId)) {
            out.add(toHearingResponse(row));
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<CaseHearingResponse> listTodayHearings(Principal principal) {
        String login = normalizeLogin(principal);
        Long officeId = resolveOfficerCurrentOfficeId(login);
        List<CaseHearingResponse> out = new ArrayList<>();
        for (CaseHearing row : caseHearingRepository.findByCaseRegistryOfficeIdAndHearingDateOrderByCaseRegistryIdAscHearingNoAsc(
                officeId,
                LocalDate.now()
        )) {
            out.add(toHearingResponse(row));
        }
        return out;
    }

    @Transactional
    private CaseOrderSheetResponse upsertOrderSheet(Long caseId, CaseOrderSheetUpsertRequest request, Principal principal) {
        return upsertOrderSheet(caseId, request, principal, false);
    }

    @Transactional
    private CaseOrderSheetResponse upsertOrderSheet(
            Long caseId,
            CaseOrderSheetUpsertRequest request,
            Principal principal,
            boolean oneShotComplete
    ) {
        if (request == null || trimToNull(request.getContent()) == null) {
            throw new IllegalArgumentException("content is required.");
        }
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        LocalDate targetHearingDate = request.getHearingDate() != null ? request.getHearingDate() : LocalDate.now();
        CaseHearing hearing = resolveHearingForRoznama(
                caseRow,
                request.getHearingId(),
                request.getHearingDate(),
                targetHearingDate
        );
        assertNoticeServedForProceeding(hearing);
        CaseOrderSheet existingSheet = caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null);
        WorkflowAction roznamaAction = existingSheet != null && isProceedingDraftStatus(existingSheet.getStatus())
                ? WorkflowAction.UPDATE_ROZNAMA
                : WorkflowAction.DRAFT_ROZNAMA;
        if (!oneShotComplete) {
            requireCaseAction(caseRow, posting, hearing, null, existingSheet,
                    caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null),
                    roznamaAction);
        }

        CaseOrderSheet sheet = resolveOrCreateOrderSheet(caseRow);
        prepareSheetForHearingDraft(sheet, hearing, login);
        if (sheet.getStatus() != null && !isProceedingDraftStatus(sheet.getStatus())) {
            throw new IllegalArgumentException("Proceeding can be edited only in PO draft stage.");
        }
        List<CaseHearing> hearings = caseHearingRepository.findByCaseRegistryIdOrderByHearingNoAsc(caseId);
        List<CaseOrderSheetHistory> histories =
                caseOrderSheetHistoryRepository.findByCaseRegistryIdOrderByCreatedAtDesc(caseId);
        List<RoznamaTableEntryResponse> existingRows =
                RoznamaContentHelper.buildTableRows(hearings, hearing, sheet, histories);
        String mergedContent = RoznamaContentHelper.mergeSaveContent(
                request.getContent().trim(),
                existingRows,
                hearing
        );
        sheet.setDraftContent(mergedContent);
        sheet.setFinalContent(null);
        sheet.setDigitalSignatureRef(null);
        sheet.setStatus(CaseOrderSheetStatus.PO_DRAFT);
        sheet.setDraftedByLoginId(login);
        sheet.setPoFinalizedByLoginId(null);
        sheet.setPoSignedByLoginId(null);
        sheet.setCurrentHearing(hearing);
        sheet.setContent(mergedContent);
        sheet.setUpdatedByLoginId(login);
        sheet = caseOrderSheetRepository.save(sheet);

        CaseOrderSheetHistory hist = new CaseOrderSheetHistory();
        hist.setCaseRegistry(caseRow);
        hist.setCaseHearing(hearing);
        hist.setContent(sheet.getDraftContent());
        hist.setRemarks(withStage("PO_DRAFT", request.getRemarks()));
        hist.setCreatedByLoginId(login);
        caseOrderSheetHistoryRepository.save(hist);

        return toOrderSheetResponse(sheet);
    }

    @Transactional
    private CaseOrderSheetResponse finalizeOrderSheet(Long caseId, CaseOrderSheetFinalizeRequest request, Principal principal) {
        return finalizeOrderSheet(caseId, request, principal, false);
    }

    @Transactional
    private CaseOrderSheetResponse finalizeOrderSheet(
            Long caseId,
            CaseOrderSheetFinalizeRequest request,
            Principal principal,
            boolean oneShotComplete
    ) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);
        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Order sheet draft not found."));
        if (!oneShotComplete) {
            requireCaseAction(caseRow, posting, sheet.getCurrentHearing(), null, sheet,
                    caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null),
                    WorkflowAction.FINALIZE_ROZNAMA);
        }
        if (!isProceedingDraftStatus(sheet.getStatus()) && sheet.getStatus() != CaseOrderSheetStatus.PO_SCRUTINY) {
            throw new IllegalArgumentException("Only PO draft proceeding can be finalized.");
        }
        String content = request != null ? trimToNull(request.getFinalContent()) : null;
        if (content == null) {
            content = trimToNull(sheet.getDraftContent());
        }
        if (content == null) {
            content = trimToNull(sheet.getContent());
        }
        if (content == null) {
            throw new IllegalArgumentException("Roznama draft content is missing; cannot finalize.");
        }
        sheet.setFinalContent(content);
        sheet.setContent(sheet.getFinalContent());
        HearingOutcome finalizeOutcome = parseHearingOutcome(
                request != null ? request.getHearingOutcome() : null,
                false
        );
        if (finalizeOutcome != null) {
            sheet.setHearingOutcome(finalizeOutcome);
        }
        sheet.setStatus(CaseOrderSheetStatus.PO_FINALIZED);
        sheet.setPoFinalizedByLoginId(login);
        sheet.setUpdatedByLoginId(login);
        sheet = caseOrderSheetRepository.save(sheet);

        CaseOrderSheetHistory hist = new CaseOrderSheetHistory();
        hist.setCaseRegistry(caseRow);
        hist.setCaseHearing(sheet.getCurrentHearing());
        hist.setContent(sheet.getFinalContent());
        hist.setRemarks(withStage("PO_FINALIZED", request != null ? request.getRemarks() : null));
        hist.setCreatedByLoginId(login);
        caseOrderSheetHistoryRepository.save(hist);
        return toOrderSheetResponse(sheet);
    }

    @Transactional
    private CaseOrderSheetResponse signOrderSheet(Long caseId, CaseOrderSheetSignRequest request, Principal principal) {
        return signOrderSheet(caseId, request, principal, false);
    }

    @Transactional
    private CaseOrderSheetResponse signOrderSheet(
            Long caseId,
            CaseOrderSheetSignRequest request,
            Principal principal,
            boolean oneShotComplete
    ) {
        if (request == null || trimToNull(request.getDigitalSignatureRef()) == null) {
            throw new IllegalArgumentException("digitalSignatureRef is required.");
        }
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);
        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Order sheet not found."));
        if (!oneShotComplete) {
            requireCaseAction(caseRow, posting, sheet.getCurrentHearing(), null, sheet,
                    caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null),
                    WorkflowAction.SIGN_ROZNAMA);
        }
        if (sheet.getStatus() == CaseOrderSheetStatus.PO_SCRUTINY
                || isProceedingDraftStatus(sheet.getStatus())) {
            String finalText = trimToNull(sheet.getFinalContent());
            if (finalText == null) {
                finalText = trimToNull(sheet.getDraftContent());
            }
            if (finalText == null) {
                finalText = trimToNull(sheet.getContent());
            }
            if (finalText == null) {
                throw new IllegalArgumentException("Proceeding content is required before sign.");
            }
            sheet.setFinalContent(finalText);
            sheet.setContent(finalText);
            sheet.setStatus(CaseOrderSheetStatus.PO_FINALIZED);
            sheet.setPoFinalizedByLoginId(login);
        }
        if (sheet.getStatus() != CaseOrderSheetStatus.PO_FINALIZED) {
            throw new IllegalArgumentException("Only PO finalized proceeding can be signed.");
        }
        HearingOutcome outcome = parseHearingOutcome(request.getHearingOutcome(), true);
        sheet.setHearingOutcome(outcome);
        sheet.setDigitalSignatureRef(request.getDigitalSignatureRef().trim());
        sheet.setStatus(CaseOrderSheetStatus.PO_SIGNED);
        sheet.setPoSignedByLoginId(login);
        sheet.setUpdatedByLoginId(login);

        CaseHearing nextHearing = applyHearingOutcomeAfterRoznammaSign(
                caseRow,
                sheet,
                outcome,
                request.getNextHearingDate(),
                login
        );
        sheet = caseOrderSheetRepository.save(sheet);

        CaseOrderSheetHistory hist = new CaseOrderSheetHistory();
        hist.setCaseRegistry(caseRow);
        hist.setCaseHearing(sheet.getCurrentHearing());
        hist.setContent(sheet.getFinalContent() != null ? sheet.getFinalContent() : sheet.getContent());
        hist.setRemarks(withStage("PO_SIGNED", request.getRemarks()));
        hist.setCreatedByLoginId(login);
        caseOrderSheetHistoryRepository.save(hist);

        CaseOrderSheetResponse response = toOrderSheetResponse(sheet);
        response.setCaseStatus(caseRow.getStatus());
        if (nextHearing != null) {
            response.setMessage(
                    "Roznamma signed. Next hearing #" + nextHearing.getHearingNo()
                            + " scheduled on " + nextHearing.getHearingDate()
                            + ". Serve notice to parties."
            );
        } else if (outcome == HearingOutcome.FINAL) {
            response.setMessage("Roznamma signed. This was the final hearing — proceed to judgment.");
        } else {
            response.setMessage(
                    "Roznamma signed with adjournment. Schedule next hearing date using reschedule API."
            );
        }
        return response;
    }

    @Transactional(readOnly = true)
    public RoznamaResponse getRoznama(Long caseId, Long hearingId, LocalDate hearingDate, Principal principal) {
        String login = normalizeLogin(principal);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        LocalDate defaultDate = hearingDate != null ? hearingDate : LocalDate.now();
        CaseHearing hearing = resolveHearingForRoznama(caseRow, hearingId, hearingDate, defaultDate);
        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null);
        List<CaseOrderSheetHistory> histories = caseOrderSheetHistoryRepository.findByCaseRegistryIdOrderByCreatedAtDesc(caseId);
        RoznamaHearingView view = resolveRoznamaViewForHearing(sheet, histories, hearing);
        RoznamaResponse out;
        if (view.linkedToHearing && sheet != null) {
            out = toRoznamaResponse(toOrderSheetResponse(sheet));
            out.setHearingId(hearing.getId());
        } else {
            out = new RoznamaResponse();
            out.setCaseId(caseRow.getId());
            out.setCaseNo(caseRow.getCaseNo());
            out.setHearingId(hearing.getId());
            out.setId(view.roznamaId);
            out.setStatus(view.roznamaStatus);
            out.setDraftContent(view.draftContent);
            out.setFinalContent(view.finalContent);
            out.setContent(view.finalContent != null ? view.finalContent : view.draftContent);
            out.setUpdatedAt(view.updatedAt);
        }
        enrichRoznamaTable(out, caseId, hearing, sheet, histories);
        caseHearingAttendanceService.enrichRoznamaResponse(out, caseRow, hearing);
        return out;
    }

    @Transactional(readOnly = true)
    public HearingAttendanceResponse getHearingAttendance(Long caseId, Long hearingId, Principal principal) {
        if (hearingId == null) {
            throw new IllegalArgumentException("hearingId is required.");
        }
        String login = normalizeLogin(principal);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        CaseHearing hearing = caseHearingRepository.findById(hearingId)
                .orElseThrow(() -> new IllegalArgumentException("Hearing not found."));
        if (!Objects.equals(hearing.getCaseRegistry().getId(), caseRow.getId())) {
            throw new IllegalArgumentException("Hearing does not belong to this case.");
        }
        assertNoticeServedForProceeding(hearing);
        return caseHearingAttendanceService.getAttendance(caseRow, hearing);
    }

    @Transactional
    public HearingAttendanceResponse saveHearingAttendance(
            Long caseId,
            Long hearingId,
            HearingAttendanceSaveRequest request,
            Principal principal
    ) {
        if (hearingId == null) {
            throw new IllegalArgumentException("hearingId is required.");
        }
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);
        CaseHearing hearing = caseHearingRepository.findById(hearingId)
                .orElseThrow(() -> new IllegalArgumentException("Hearing not found."));
        if (!Objects.equals(hearing.getCaseRegistry().getId(), caseRow.getId())) {
            throw new IllegalArgumentException("Hearing does not belong to this case.");
        }
        assertNoticeServedForProceeding(hearing);
        return caseHearingAttendanceService.saveAttendance(caseRow, hearing, request, login);
    }

    /**
     * One-shot: save roznamma content, finalize, sign, and apply hearing outcome (ADJOURN or FINAL).
     */
    @Transactional
    public CaseRoznamaCompleteResponse completeRoznama(
            Long caseId,
            CaseRoznamaCompleteRequest request,
            Principal principal
    ) {
        if (request == null || request.getHearingId() == null) {
            throw new IllegalArgumentException("hearingId is required.");
        }
        if (trimToNull(request.getContent()) == null) {
            throw new IllegalArgumentException("content is required.");
        }
        HearingOutcome outcome = parseHearingOutcome(request.getHearingOutcome(), true);

        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseHearing hearing = caseHearingRepository.findById(request.getHearingId())
                .orElseThrow(() -> new IllegalArgumentException("Hearing not found."));
        if (!Objects.equals(hearing.getCaseRegistry().getId(), caseRow.getId())) {
            throw new IllegalArgumentException("Hearing does not belong to this case.");
        }
        caseHearingAttendanceService.saveAttendanceIfProvided(
                caseRow, hearing, request.getAttendance(), login
        );
        caseHearingAttendanceService.assertAttendanceCompleteIfRequired(caseRow, hearing);

        CaseOrderSheet existingSheet = caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null);
        requireCaseAction(caseRow, posting, hearing, null, existingSheet,
                caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null),
                WorkflowAction.COMPLETE_ROZNAMA);

        CaseOrderSheetUpsertRequest draftRequest = new CaseOrderSheetUpsertRequest();
        draftRequest.setHearingId(request.getHearingId());
        draftRequest.setHearingDate(request.getHearingDate());
        draftRequest.setContent(request.getContent().trim());
        draftRequest.setRemarks(request.getRemarks());
        upsertOrderSheet(caseId, draftRequest, principal, true);

        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Roznamma not found. Save draft first."));

        if (sheet.getStatus() == CaseOrderSheetStatus.PO_SIGNED
                && sheet.getHearingOutcome() != null
                && sheet.getCurrentHearing() != null
                && Objects.equals(sheet.getCurrentHearing().getId(), request.getHearingId())) {
            throw new IllegalArgumentException("Roznamma is already signed for this hearing.");
        }

        boolean roznamaFinalized = false;
        boolean roznamaSigned = false;

        if (isProceedingDraftStatus(sheet.getStatus()) || sheet.getStatus() == CaseOrderSheetStatus.PO_SCRUTINY) {
            String contentForFinalize = firstNonBlankContent(
                    sheet.getDraftContent(),
                    sheet.getContent(),
                    request.getContent()
            );
            CaseOrderSheetFinalizeRequest finalizeRequest = new CaseOrderSheetFinalizeRequest();
            finalizeRequest.setFinalContent(contentForFinalize);
            finalizeRequest.setHearingOutcome(request.getHearingOutcome());
            finalizeRequest.setRemarks(request.getRemarks());
            finalizeOrderSheet(caseId, finalizeRequest, principal, true);
            roznamaFinalized = true;
            sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId)
                    .orElseThrow(() -> new IllegalArgumentException("Roznamma not found after finalize."));
        } else if (sheet.getStatus() == CaseOrderSheetStatus.PO_FINALIZED) {
            roznamaFinalized = true;
        }

        if (sheet.getStatus() != CaseOrderSheetStatus.PO_SIGNED) {
            CaseOrderSheetSignRequest signRequest = new CaseOrderSheetSignRequest();
            String sigRef = trimToNull(request.getDigitalSignatureRef());
            if (sigRef == null) {
                sigRef = "PO-ROZNAMA-" + caseId + "-" + request.getHearingId() + "-" + System.currentTimeMillis();
            }
            signRequest.setDigitalSignatureRef(sigRef);
            signRequest.setHearingOutcome(request.getHearingOutcome());
            signRequest.setNextHearingDate(request.getNextHearingDate());
            signRequest.setRemarks(request.getRemarks());
            CaseOrderSheetResponse signed = signOrderSheet(caseId, signRequest, principal, true);
            roznamaSigned = true;
            return buildRoznamaCompleteResponse(caseId, request.getHearingId(), signed, outcome, roznamaFinalized, roznamaSigned);
        }

        CaseOrderSheetResponse existing = toOrderSheetResponse(sheet);
        existing.setCaseStatus(caseRow.getStatus());
        existing.setMessage("Roznamma was already signed for this hearing.");
        return buildRoznamaCompleteResponse(caseId, request.getHearingId(), existing, outcome, roznamaFinalized, true);
    }

    private CaseRoznamaCompleteResponse buildRoznamaCompleteResponse(
            Long caseId,
            Long hearingId,
            CaseOrderSheetResponse sheet,
            HearingOutcome outcome,
            boolean roznamaFinalized,
            boolean roznamaSigned
    ) {
        CaseRoznamaCompleteResponse out = new CaseRoznamaCompleteResponse();
        out.setCaseId(caseId);
        out.setRoznamaId(sheet.getId());
        out.setHearingId(hearingId != null ? hearingId : sheet.getCurrentHearingId());
        out.setStatus(sheet.getStatus());
        out.setHearingOutcome(outcome.name());
        out.setCaseStatus(sheet.getCaseStatus());
        out.setMessage(sheet.getMessage());
        out.setRoznamaFinalized(roznamaFinalized);
        out.setRoznamaSigned(roznamaSigned);
        out.setDigitalSignatureRef(sheet.getDigitalSignatureRef());
        out.setUpdatedAt(sheet.getUpdatedAt());

        caseHearingRepository.findById(out.getHearingId()).ifPresent(hearing -> {
            out.setFinalHearing(Boolean.TRUE.equals(hearing.getFinalHearing()));
        });
        if (outcome == HearingOutcome.ADJOURN && "HEARING_SCHEDULED".equalsIgnoreCase(sheet.getCaseStatus())) {
            caseHearingRepository.findFirstByCaseRegistryIdOrderByHearingNoDesc(caseId).ifPresent(next -> {
                if (!Objects.equals(next.getId(), out.getHearingId())) {
                    out.setNextHearingId(next.getId());
                    out.setNextHearingDate(next.getHearingDate());
                }
            });
        }
        if (out.getMessage() == null) {
            if (outcome == HearingOutcome.FINAL) {
                out.setMessage("Roznamma finalized, signed. Proceed to judgment.");
            } else if (out.getNextHearingId() != null) {
                out.setMessage("Roznamma finalized, signed. Next hearing scheduled — serve notice to parties.");
            } else {
                out.setMessage("Roznamma finalized, signed with adjournment. Schedule next hearing date if not set.");
            }
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<CaseOrderSheetHistoryResponse> getRoznamaHistory(Long caseId, Long hearingId, Principal principal) {
        String login = normalizeLogin(principal);
        resolveOfficerCase(caseId, login);
        List<CaseOrderSheetHistoryResponse> out = new ArrayList<>();
        for (CaseOrderSheetHistory row : caseOrderSheetHistoryRepository.findByCaseRegistryIdOrderByCreatedAtDesc(caseId)) {
            if (hearingId != null) {
                Long rowHearingId = row.getCaseHearing() != null ? row.getCaseHearing().getId() : null;
                if (!Objects.equals(hearingId, rowHearingId)) {
                    continue;
                }
            }
            out.add(toOrderSheetHistoryResponse(row));
        }
        return out;
    }

    private void assertNotDisposed(CaseRegistry caseRow) {
        if ("DISPOSED".equalsIgnoreCase(caseRow.getStatus())) {
            throw new IllegalArgumentException("Case already disposed.");
        }
    }

    private CaseRegistry resolveOfficerCase(Long caseId, String login) {
        if (caseId == null) {
            throw new IllegalArgumentException("caseId is required.");
        }
        Long officeId = resolveOfficerCurrentOfficeId(login);
        return caseRegistryRepository.findByIdAndOfficeId(caseId, officeId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found for officer office."));
    }

    private EmployeePosting resolveOfficerPosting(String login) {
        Employee employee = resolveOfficerEmployee(login);
        return employeePostingRepository.findFirstByEmployeeIdAndToDateIsNull(employee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Current posting not found for officer."));
    }

    private static void assertPo(EmployeePosting posting) {
        Long designationId = posting.getDesignation() != null ? posting.getDesignation().getId() : null;
        if (!Objects.equals(designationId, 1L)) {
            throw new IllegalArgumentException("Only Presiding Officer can perform this action.");
        }
    }

    private static void assertClerk(EmployeePosting posting) {
        Long designationId = posting.getDesignation() != null ? posting.getDesignation().getId() : null;
        if (Objects.equals(designationId, 1L)) {
            throw new IllegalArgumentException("Only clerk can perform this action.");
        }
    }

    private Long resolveOfficerCurrentOfficeId(String login) {
        EmployeePosting posting = resolveOfficerPosting(login);
        Long officeId = posting.getOffice() != null ? posting.getOffice().getId() : null;
        if (officeId == null) {
            throw new IllegalArgumentException("Officer current posting office is missing.");
        }
        return officeId;
    }

    private Employee resolveOfficerEmployee(String login) {
        if (login.endsWith("@officer.local")) {
            String employeeCode = login.substring(0, login.length() - "@officer.local".length()).trim();
            if (!employeeCode.isEmpty()) {
                Optional<Employee> byCode = employeeRepository.findFirstByEmployeeCodeIgnoreCase(employeeCode);
                if (byCode.isPresent()) {
                    return byCode.get();
                }
            }
        }
        return employeeRepository.findFirstByEmailIgnoreCase(login)
                .orElseThrow(() -> new IllegalArgumentException("Officer employee profile not found."));
    }

    private static String normalizeLogin(Principal principal) {
        Objects.requireNonNull(principal);
        return principal.getName().trim().toLowerCase(Locale.ROOT);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private static String firstNonBlankContent(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            String trimmed = trimToNull(value);
            if (trimmed != null) {
                return trimmed;
            }
        }
        return null;
    }

    private static String requiredText(String value, String field) {
        String t = trimToNull(value);
        if (t == null) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return t;
    }

    private void requireCaseAction(
            CaseRegistry caseRow,
            EmployeePosting posting,
            CaseHearing hearing,
            CaseNotice notice,
            CaseOrderSheet sheet,
            CaseJudgmentWorkflow judgment,
            WorkflowAction action
    ) {
        java.util.Set<WorkflowAction> allowed = new java.util.LinkedHashSet<>();
        for (String code : workflowPolicyService.caseAllowedActions(caseRow, posting, hearing, notice, sheet, judgment)) {
            allowed.add(WorkflowAction.valueOf(code));
        }
        workflowPolicyService.requireAction(action, allowed);
    }

    private void recordJudgmentHistory(
            CaseRegistry caseRow,
            CaseJudgmentWorkflow workflow,
            CaseJudgmentWorkflowStatus from,
            CaseJudgmentWorkflowStatus to,
            WorkflowAction action,
            String summary,
            String remarks,
            String role,
            String login
    ) {
        if (!workflowPolicyService.definitionFor(caseRow).getJudgment().isAuditTrailRequired()) {
            return;
        }
        judgmentWorkflowHistoryService.record(caseRow, workflow, from, to, action, summary, remarks, role, login);
    }

    private static String withStage(String stage, String remarks) {
        String r = trimToNull(remarks);
        return r == null ? stage : stage + " | " + r;
    }

    private CaseOrderSheet resolveOrCreateOrderSheet(CaseRegistry caseRow) {
        return caseOrderSheetRepository.findByCaseRegistryId(caseRow.getId()).orElseGet(() -> {
            CaseOrderSheet sheet = new CaseOrderSheet();
            sheet.setCaseRegistry(caseRow);
            return sheet;
        });
    }

    private static void assertNoticeServedForProceeding(CaseHearing hearing) {
        if (!Boolean.TRUE.equals(hearing.getNoticeServed())) {
            throw new IllegalArgumentException(
                    "Proceeding cannot start until notice is served for this hearing (hearing #"
                            + hearing.getHearingNo() + " on " + hearing.getHearingDate() + ")."
            );
        }
    }

    private CaseHearing createNextScheduledHearing(
            CaseRegistry caseRow,
            LocalDate hearingDate,
            boolean noticeGenerate,
            String remarks,
            String login
    ) {
        Integer nextHearingNo = caseHearingRepository.findFirstByCaseRegistryIdOrderByHearingNoDesc(caseRow.getId())
                .map(CaseHearing::getHearingNo)
                .map(x -> x + 1)
                .orElse(1);
        CaseHearing row = new CaseHearing();
        row.setCaseRegistry(caseRow);
        row.setHearingNo(nextHearingNo);
        row.setHearingDate(hearingDate);
        row.setStatus("SCHEDULED");
        row.setNoticeGenerated(noticeGenerate);
        row.setNoticeServed(false);
        row.setFinalHearing(false);
        row.setRemarks(remarks);
        row.setCreatedByLoginId(login);
        return caseHearingRepository.save(row);
    }

    private CaseHearing applyHearingOutcomeAfterRoznammaSign(
            CaseRegistry caseRow,
            CaseOrderSheet sheet,
            HearingOutcome outcome,
            LocalDate nextHearingDate,
            String login
    ) {
        CaseHearing hearing = sheet.getCurrentHearing();
        if (hearing == null) {
            throw new IllegalArgumentException("Roznamma must be linked to a hearing.");
        }
        hearing.setStatus("COMPLETED");
        CaseHearing nextHearing = null;
        if (outcome == HearingOutcome.FINAL) {
            hearing.setFinalHearing(true);
            caseHearingRepository.save(hearing);
            caseRow.setStatus("READY_FOR_JUDGMENT");
            caseRegistryRepository.save(caseRow);
        } else {
            hearing.setFinalHearing(false);
            caseHearingRepository.save(hearing);
            if (nextHearingDate != null) {
                nextHearing = createNextScheduledHearing(
                        caseRow,
                        nextHearingDate,
                        true,
                        "Adjourned from hearing #" + hearing.getHearingNo(),
                        login
                );
                caseRow.setStatus("HEARING_SCHEDULED");
                caseRegistryRepository.save(caseRow);
            } else {
                caseRow.setStatus("ADJOURNED");
                caseRegistryRepository.save(caseRow);
            }
        }
        return nextHearing;
    }

    private boolean hasSignedAdjournRoznammaForHearing(Long caseId, Long hearingId) {
        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null);
        if (sheet != null
                && sheet.getStatus() == CaseOrderSheetStatus.PO_SIGNED
                && sheet.getHearingOutcome() == HearingOutcome.ADJOURN
                && sheet.getCurrentHearing() != null
                && Objects.equals(sheet.getCurrentHearing().getId(), hearingId)) {
            return true;
        }
        for (CaseOrderSheetHistory row : caseOrderSheetHistoryRepository.findByCaseRegistryIdOrderByCreatedAtDesc(caseId)) {
            if (row.getCaseHearing() == null || !Objects.equals(row.getCaseHearing().getId(), hearingId)) {
                continue;
            }
            if ("PO_SIGNED".equals(parseHistoryStatus(row.getRemarks()))) {
                return sheet == null || sheet.getHearingOutcome() == HearingOutcome.ADJOURN;
            }
        }
        return false;
    }

    private static HearingOutcome parseHearingOutcome(String raw, boolean required) {
        String value = trimToNull(raw);
        if (value == null) {
            if (required) {
                throw new IllegalArgumentException("hearingOutcome is required (ADJOURN or FINAL).");
            }
            return null;
        }
        try {
            return HearingOutcome.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("hearingOutcome must be ADJOURN or FINAL.");
        }
    }

    private static CaseHearingResponse toHearingResponse(CaseHearing row) {
        CaseHearingResponse out = new CaseHearingResponse();
        out.setHearingId(row.getId());
        out.setCaseId(row.getCaseRegistry() != null ? row.getCaseRegistry().getId() : null);
        out.setCaseNo(row.getCaseRegistry() != null ? row.getCaseRegistry().getCaseNo() : null);
        out.setHearingNo(row.getHearingNo());
        out.setHearingDate(row.getHearingDate());
        out.setStatus(row.getStatus());
        out.setNoticeGenerated(row.getNoticeGenerated());
        boolean noticeServed = Boolean.TRUE.equals(row.getNoticeServed());
        out.setNoticeServed(noticeServed);
        out.setProceedingAllowed(noticeServed);
        out.setFinalHearing(Boolean.TRUE.equals(row.getFinalHearing()));
        out.setRemarks(row.getRemarks());
        out.setCreatedAt(row.getCreatedAt());
        out.setUpdatedAt(row.getUpdatedAt());
        return out;
    }

    private Map<Long, CaseOrderSheet> loadRoznamaByCaseIds(List<Long> caseIds) {
        if (caseIds == null || caseIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, CaseOrderSheet> out = new HashMap<>();
        for (CaseOrderSheet sheet : caseOrderSheetRepository.findByCaseRegistryIdIn(caseIds)) {
            if (sheet.getCaseRegistry() != null && sheet.getCaseRegistry().getId() != null) {
                out.put(sheet.getCaseRegistry().getId(), sheet);
            }
        }
        return out;
    }

    private Map<Long, List<CaseOrderSheetHistory>> loadRoznamaHistoryByCaseIds(List<Long> caseIds) {
        if (caseIds == null || caseIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, List<CaseOrderSheetHistory>> out = new HashMap<>();
        for (CaseOrderSheetHistory row : caseOrderSheetHistoryRepository.findByCaseRegistryIdInOrderByCreatedAtDesc(caseIds)) {
            if (row.getCaseRegistry() == null || row.getCaseRegistry().getId() == null) {
                continue;
            }
            out.computeIfAbsent(row.getCaseRegistry().getId(), ignored -> new ArrayList<>()).add(row);
        }
        return out;
    }

    private CaseHearing resolveHearingForRoznama(
            CaseRegistry caseRow,
            Long hearingId,
            LocalDate hearingDate,
            LocalDate defaultDate
    ) {
        Long caseId = caseRow.getId();
        if (hearingId != null) {
            CaseHearing hearing = caseHearingRepository.findById(hearingId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid hearingId."));
            if (!Objects.equals(hearing.getCaseRegistry().getId(), caseId)) {
                throw new IllegalArgumentException("hearingId does not belong to case.");
            }
            return hearing;
        }
        LocalDate targetDate = hearingDate != null ? hearingDate : defaultDate;
        List<CaseHearing> onDate = caseHearingRepository.findByCaseRegistryIdAndHearingDateOrderByHearingNoAsc(caseId, targetDate);
        if (onDate.isEmpty()) {
            throw new IllegalArgumentException(
                    "No hearing found for case on " + targetDate + ". Schedule a hearing or pass hearingId."
            );
        }
        if (onDate.size() > 1) {
            throw new IllegalArgumentException(
                    "Multiple hearings on " + targetDate + " for this case. Pass hearingId."
            );
        }
        return onDate.get(0);
    }

    private void prepareSheetForHearingDraft(CaseOrderSheet sheet, CaseHearing hearing, String login) {
        assertNoticeServedForProceeding(hearing);
        boolean sameHearing = sheet.getCurrentHearing() != null
                && Objects.equals(sheet.getCurrentHearing().getId(), hearing.getId());
        if (sameHearing) {
            return;
        }
        if (sheet.getStatus() == CaseOrderSheetStatus.PO_SIGNED) {
            Long caseId = sheet.getCaseRegistry() != null ? sheet.getCaseRegistry().getId() : null;
            String draftJson = "";
            if (caseId != null) {
                List<CaseHearing> hearings =
                        caseHearingRepository.findByCaseRegistryIdOrderByHearingNoAsc(caseId);
                List<CaseOrderSheetHistory> histories =
                        caseOrderSheetHistoryRepository.findByCaseRegistryIdOrderByCreatedAtDesc(caseId);
                List<RoznamaTableEntryResponse> table =
                        RoznamaContentHelper.buildTableRows(hearings, hearing, sheet, histories);
                draftJson = RoznamaContentHelper.toContentJson(table);
            }
            sheet.setStatus(CaseOrderSheetStatus.PO_DRAFT);
            sheet.setDraftContent(draftJson);
            sheet.setFinalContent(null);
            sheet.setDigitalSignatureRef(null);
            sheet.setPoFinalizedByLoginId(null);
            sheet.setPoSignedByLoginId(null);
            sheet.setContent(draftJson);
            sheet.setHearingOutcome(null);
            sheet.setCurrentHearing(hearing);
            sheet.setUpdatedByLoginId(login);
            return;
        }
        if (sheet.getStatus() != null && !isProceedingDraftStatus(sheet.getStatus())) {
            throw new IllegalArgumentException(
                    "Proceeding for another hearing is in progress. Complete, sign, or revert it before starting this hearing date."
            );
        }
        sheet.setCurrentHearing(hearing);
    }

    private static void applyRoznamaFieldsForHearing(
            OfficerCauseListItemResponse item,
            CaseOrderSheet sheet,
            List<CaseOrderSheetHistory> histories,
            CaseHearing hearing
    ) {
        RoznamaHearingView view = resolveRoznamaViewForHearing(sheet, histories, hearing);
        item.setRoznamaId(view.roznamaId);
        item.setRoznamaStatus(view.roznamaStatus);
        item.setProceedingStage(view.proceedingStage);
        item.setDraftContent(view.draftContent);
        item.setFinalContent(view.finalContent);
        item.setRoznamaUpdatedAt(view.updatedAt);
        item.setRoznamaLinkedToHearing(view.linkedToHearing);
        item.setCanEdit(view.canEdit);
    }

    private static RoznamaHearingView resolveRoznamaViewForHearing(
            CaseOrderSheet sheet,
            List<CaseOrderSheetHistory> histories,
            CaseHearing hearing
    ) {
        RoznamaHearingView view = new RoznamaHearingView();
        boolean noticeServed = Boolean.TRUE.equals(hearing.getNoticeServed());
        view.proceedingStage = noticeServed ? "NOTICE_SERVED" : "AWAITING_NOTICE_SERVE";
        view.canEdit = noticeServed && !hasInProgressRoznamaOnOtherHearing(sheet, hearing);

        if (sheet != null && sheet.getCurrentHearing() != null
                && Objects.equals(sheet.getCurrentHearing().getId(), hearing.getId())) {
            view.linkedToHearing = true;
            view.roznamaId = sheet.getId();
            view.roznamaStatus = sheet.getStatus() != null ? sheet.getStatus().name() : null;
            view.proceedingStage = toRoznamaProceedingStage(sheet.getStatus());
            view.draftContent = sheet.getDraftContent();
            view.finalContent = sheet.getFinalContent();
            view.updatedAt = sheet.getUpdatedAt();
            view.canEdit = noticeServed && isProceedingDraftStatus(sheet.getStatus());
            return view;
        }

        if (histories != null) {
            for (CaseOrderSheetHistory row : histories) {
                if (row.getCaseHearing() == null || !Objects.equals(row.getCaseHearing().getId(), hearing.getId())) {
                    continue;
                }
                String historyStatus = parseHistoryStatus(row.getRemarks());
                if ("PO_SIGNED".equals(historyStatus)) {
                    view.linkedToHearing = true;
                    view.roznamaId = sheet != null ? sheet.getId() : null;
                    view.roznamaStatus = "PO_SIGNED";
                    view.proceedingStage = "ROZNAMA_PO_SIGNED";
                    view.finalContent = row.getContent();
                    view.draftContent = row.getContent();
                    view.updatedAt = row.getCreatedAt();
                    view.canEdit = false;
                    return view;
                }
            }
        }
        return view;
    }

    private static boolean hasInProgressRoznamaOnOtherHearing(CaseOrderSheet sheet, CaseHearing hearing) {
        if (sheet == null || sheet.getCurrentHearing() == null) {
            return false;
        }
        if (Objects.equals(sheet.getCurrentHearing().getId(), hearing.getId())) {
            return false;
        }
        return sheet.getStatus() != CaseOrderSheetStatus.PO_SIGNED;
    }

    private static final class RoznamaHearingView {
        private Long roznamaId;
        private String roznamaStatus;
        private String proceedingStage;
        private String draftContent;
        private String finalContent;
        private Instant updatedAt;
        private boolean linkedToHearing;
        private boolean canEdit;
    }

    private static String toRoznamaProceedingStage(CaseOrderSheetStatus status) {
        if (status == null) {
            return "ROZNAMA_NOT_STARTED";
        }
        return switch (status) {
            case CLERK_DRAFT -> "ROZNAMA_CLERK_DRAFT";
            case PO_DRAFT -> "ROZNAMA_PO_DRAFT";
            case PO_SCRUTINY -> "ROZNAMA_PO_SCRUTINY";
            case PO_FINALIZED -> "ROZNAMA_PO_FINALIZED";
            case PO_SIGNED -> "ROZNAMA_PO_SIGNED";
        };
    }

    private static boolean isProceedingDraftStatus(CaseOrderSheetStatus status) {
        return status == CaseOrderSheetStatus.PO_DRAFT || status == CaseOrderSheetStatus.CLERK_DRAFT;
    }

    private static String parseHistoryStatus(String remarks) {
        String r = trimToNull(remarks);
        if (r == null) {
            return null;
        }
        int sep = r.indexOf(" | ");
        return sep >= 0 ? r.substring(0, sep) : r;
    }

    private void enrichRoznamaTable(
            RoznamaResponse out,
            Long caseId,
            CaseHearing hearing,
            CaseOrderSheet sheet,
            List<CaseOrderSheetHistory> histories
    ) {
        List<CaseHearing> hearings = caseHearingRepository.findByCaseRegistryIdOrderByHearingNoAsc(caseId);
        List<RoznamaTableEntryResponse> rows =
                RoznamaContentHelper.buildTableRows(hearings, hearing, sheet, histories);
        out.setTableRows(rows);
        String tableJson = RoznamaContentHelper.toContentJson(rows);
        out.setContent(tableJson);
        rows.stream()
                .filter(row -> !row.isReadOnly())
                .findFirst()
                .ifPresentOrElse(
                        editable -> out.setDraftContent(editable.getContent()),
                        () -> {
                            if (out.getFinalContent() != null && !out.getFinalContent().isBlank()) {
                                out.setContent(out.getFinalContent());
                            } else if (out.getDraftContent() != null && !out.getDraftContent().isBlank()) {
                                out.setContent(out.getDraftContent());
                            }
                        }
                );
    }

    private static RoznamaResponse toRoznamaResponse(CaseOrderSheetResponse sheet) {
        RoznamaResponse out = new RoznamaResponse();
        out.setId(sheet.getId());
        out.setCaseId(sheet.getCaseId());
        out.setCaseNo(sheet.getCaseNo());
        out.setHearingId(sheet.getCurrentHearingId());
        out.setContent(sheet.getContent());
        out.setDraftContent(sheet.getDraftContent());
        out.setFinalContent(sheet.getFinalContent());
        out.setStatus(sheet.getStatus());
        out.setHearingOutcome(sheet.getHearingOutcome());
        out.setCaseStatus(sheet.getCaseStatus());
        out.setMessage(sheet.getMessage());
        out.setDigitalSignatureRef(sheet.getDigitalSignatureRef());
        out.setUpdatedAt(sheet.getUpdatedAt());
        out.setUpdatedByLoginId(sheet.getUpdatedByLoginId());
        return out;
    }

    private static CaseOrderSheetResponse toOrderSheetResponse(CaseOrderSheet sheet) {
        CaseOrderSheetResponse out = new CaseOrderSheetResponse();
        out.setId(sheet.getId());
        out.setCaseId(sheet.getCaseRegistry() != null ? sheet.getCaseRegistry().getId() : null);
        out.setCaseNo(sheet.getCaseRegistry() != null ? sheet.getCaseRegistry().getCaseNo() : null);
        out.setContent(sheet.getContent());
        out.setDraftContent(sheet.getDraftContent());
        out.setFinalContent(sheet.getFinalContent());
        out.setStatus(sheet.getStatus() != null ? sheet.getStatus().name() : null);
        out.setHearingOutcome(sheet.getHearingOutcome() != null ? sheet.getHearingOutcome().name() : null);
        out.setCurrentHearingId(sheet.getCurrentHearing() != null ? sheet.getCurrentHearing().getId() : null);
        out.setDigitalSignatureRef(sheet.getDigitalSignatureRef());
        out.setUpdatedAt(sheet.getUpdatedAt());
        out.setUpdatedByLoginId(sheet.getUpdatedByLoginId());
        return out;
    }

    private static CaseOrderSheetHistoryResponse toOrderSheetHistoryResponse(CaseOrderSheetHistory row) {
        CaseOrderSheetHistoryResponse out = new CaseOrderSheetHistoryResponse();
        out.setHistoryId(row.getId());
        if (row.getCaseHearing() != null) {
            out.setHearingId(row.getCaseHearing().getId());
            out.setHearingNo(row.getCaseHearing().getHearingNo());
            out.setHearingDate(row.getCaseHearing().getHearingDate());
        }
        out.setContent(row.getContent());
        out.setStatus(parseHistoryStatus(row.getRemarks()));
        out.setRemarks(row.getRemarks());
        out.setCreatedAt(row.getCreatedAt());
        out.setCreatedByLoginId(row.getCreatedByLoginId());
        return out;
    }

    private CaseNotice findActiveNoticeForHearing(Long hearingId) {
        if (hearingId == null) {
            return null;
        }
        for (CaseNotice row : caseNoticeRepository.findByHearingIdOrderByIdDesc(hearingId)) {
            if (row.getStatus() != CaseNoticeStatus.SERVED) {
                return row;
            }
        }
        return null;
    }

    private Map<Long, CaseHearing> loadLatestHearingByCaseIds(List<Long> caseIds) {
        Map<Long, CaseHearing> out = new HashMap<>();
        if (caseIds.isEmpty()) {
            return out;
        }
        for (CaseHearing hearing : caseHearingRepository.findByCaseRegistry_IdInOrderByCaseRegistry_IdAscHearingNoDesc(caseIds)) {
            if (hearing.getCaseRegistry() == null || hearing.getCaseRegistry().getId() == null) {
                continue;
            }
            Long caseId = hearing.getCaseRegistry().getId();
            out.putIfAbsent(caseId, hearing);
        }
        return out;
    }

    private static String resolveCaseProceedingStage(
            CaseRegistry caseRow,
            CaseOrderSheet roznama,
            CaseHearing latestHearing,
            boolean noticeServedForLatestHearing
    ) {
        if (latestHearing != null && latestHearing.getHearingDate() != null
                && !Boolean.TRUE.equals(latestHearing.getNoticeServed())
                && !"COMPLETED".equalsIgnoreCase(latestHearing.getStatus())) {
            return "AWAITING_NOTICE_SERVE";
        }
        if (roznama != null && roznama.getStatus() != null && latestHearing != null) {
            boolean sheetForLatest = roznama.getCurrentHearing() != null
                    && Objects.equals(roznama.getCurrentHearing().getId(), latestHearing.getId());
            CaseOrderSheetStatus roznamaStatus = roznama.getStatus();
            if (sheetForLatest) {
                if (roznamaStatus != CaseOrderSheetStatus.PO_SIGNED) {
                    return toRoznamaProceedingStage(roznamaStatus);
                }
                return "ROZNAMA_PO_SIGNED";
            }
        }
        if (noticeServedForLatestHearing) {
            return "NOTICE_SERVED";
        }
        if (latestHearing != null && latestHearing.getHearingDate() != null) {
            return "AWAITING_NOTICE_SERVE";
        }
        String caseStatus = caseRow != null ? trimToNull(caseRow.getStatus()) : null;
        if (caseStatus != null && "READY_FOR_JUDGMENT".equalsIgnoreCase(caseStatus)) {
            return "READY_FOR_JUDGMENT";
        }
        if (caseStatus != null && "HEARING_SCHEDULED".equalsIgnoreCase(caseStatus)) {
            return "AWAITING_NOTICE_SERVE";
        }
        return "ROZNAMA_NOT_STARTED";
    }

    private CaseNotice resolveOrCreateActiveNotice(
            CaseRegistry caseRow,
            CaseHearing hearing,
            String login,
            String noticeType
    ) {
        CaseNotice existing = findActiveNoticeForHearing(hearing.getId());
        if (existing != null) {
            return existing;
        }
        CaseNotice row = new CaseNotice();
        row.setCaseRegistry(caseRow);
        row.setHearing(hearing);
        row.setStatus(CaseNoticeStatus.PO_DRAFT);
        row.setClerkDraftedByLoginId(login);
        row.setNoticeType(noticeType);
        // Persisted once in serveNoticeToParty after draftContent is set (NOT NULL column).
        return row;
    }

    private static CaseNoticeResponse toCaseNoticeResponse(CaseNotice row) {
        CaseNoticeResponse out = new CaseNoticeResponse();
        out.setNoticeId(row.getId());
        out.setCaseId(row.getCaseRegistry() != null ? row.getCaseRegistry().getId() : null);
        out.setHearingId(row.getHearing() != null ? row.getHearing().getId() : null);
        out.setNoticeType(row.getNoticeType());
        out.setStatus(row.getStatus() != null ? row.getStatus().name() : null);
        out.setDraftContent(row.getDraftContent());
        out.setFinalContent(row.getFinalContent());
        out.setDigitalSignatureRef(row.getDigitalSignatureRef());
        out.setSelectedParties(parseJsonArray(row.getSelectedPartiesJson()));
        out.setServedAt(row.getServedAt());
        out.setCreatedAt(row.getCreatedAt());
        out.setUpdatedAt(row.getUpdatedAt());
        return out;
    }

    private CaseJudgmentWorkflowResponse enrichJudgmentResponse(
            CaseRegistry caseRow,
            CaseJudgmentWorkflow row,
            EmployeePosting posting
    ) {
        CaseJudgmentWorkflowResponse out = new CaseJudgmentWorkflowResponse();
        out.setCaseId(caseRow.getId());
        out.setCaseNo(caseRow.getCaseNo());
        out.setCaseStatus(caseRow.getStatus());
        if (row != null) {
            out.setWorkflowStatus(row.getStatus() != null ? row.getStatus().name() : null);
            out.setDraftSummary(row.getDraftSummary());
            out.setFinalSummary(row.getFinalSummary());
            out.setPublishedSummary(row.getPublishedSummary());
            out.setDigitalSignatureRef(row.getDigitalSignatureRef());
            out.setPublishedAt(row.getPublishedAt());
            out.setUpdatedAt(row.getUpdatedAt());
        }
        List<String> allowed = workflowPolicyService.judgmentAllowed(caseRow, posting, row);
        out.setAllowedActions(allowed);
        out.setEditable(workflowPolicyService.judgmentEditable(caseRow, posting, row));
        out.setSubmittable(workflowPolicyService.judgmentSubmittable(caseRow, posting, row));
        out.setActorRole(workflowPolicyService.resolveOfficerActorRole(posting));
        return out;
    }

    private static String toJsonArray(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        List<String> cleaned = values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .collect(Collectors.toList());
        if (cleaned.isEmpty()) {
            return "[]";
        }
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(cleaned);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to serialize selectedParties.");
        }
    }

    private static List<String> parseJsonArray(String raw) {
        if (trimToNull(raw) == null) {
            return Collections.emptyList();
        }
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(raw, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                    });
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private static CaseInboxItemResponse toCaseInboxItem(
            CaseRegistry row,
            CaseOrderSheet roznama,
            CaseHearing latestHearing,
            boolean noticeServed
    ) {
        CaseInboxItemResponse out = new CaseInboxItemResponse();
        out.setCaseId(row.getId());
        out.setCaseNo(row.getCaseNo());
        String caseStatus = row.getStatus();
        if (latestHearing != null
                && Boolean.TRUE.equals(latestHearing.getNoticeServed())
                && caseStatus != null
                && !"DISPOSED".equalsIgnoreCase(caseStatus)
                && !"NOTICE_SERVED".equalsIgnoreCase(caseStatus)
                && !"HEARING_SCHEDULED".equalsIgnoreCase(caseStatus)
                && !"READY_FOR_JUDGMENT".equalsIgnoreCase(caseStatus)) {
            caseStatus = "NOTICE_SERVED";
        }
        if (latestHearing != null
                && latestHearing.getHearingDate() != null
                && !Boolean.TRUE.equals(latestHearing.getNoticeServed())
                && !"COMPLETED".equalsIgnoreCase(latestHearing.getStatus())
                && "NOTICE_SERVED".equalsIgnoreCase(caseStatus)) {
            caseStatus = "HEARING_SCHEDULED";
        }
        out.setStatus(caseStatus);
        out.setFilingApplicationId(row.getFilingApplicationId());
        out.setCaseCategoryId(row.getCaseCategory() != null ? row.getCaseCategory().getId() : null);
        out.setCaseCategoryName(row.getCaseCategory() != null ? row.getCaseCategory().getName() : null);
        out.setOfficeId(row.getOffice() != null ? row.getOffice().getId() : null);
        out.setOfficeName(row.getOffice() != null ? row.getOffice().getName() : null);
        out.setApprovedAt(row.getApprovedAt());
        out.setDisposedAt(row.getDisposedAt());
        if (roznama != null) {
            out.setRoznamaId(roznama.getId());
        }
        out.setProceedingStage(resolveCaseProceedingStage(row, roznama, latestHearing, noticeServed));
        return out;
    }
}
