package com.maharashtra.rccms.service;

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

    public CaseProceedingService(
            CaseRegistryRepository caseRegistryRepository,
            CaseHearingRepository caseHearingRepository,
            CaseNoticeRepository caseNoticeRepository,
            CaseJudgmentWorkflowRepository caseJudgmentWorkflowRepository,
            CaseOrderSheetRepository caseOrderSheetRepository,
            CaseOrderSheetHistoryRepository caseOrderSheetHistoryRepository,
            EmployeeRepository employeeRepository,
            EmployeePostingRepository employeePostingRepository,
            FilingApplicationService filingApplicationService
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
        for (CaseRegistry row : rows) {
            if (s == null && "DISPOSED".equalsIgnoreCase(row.getStatus())) {
                continue;
            }
            out.add(toCaseInboxItem(row, roznamaByCaseId.get(row.getId())));
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<OfficerCauseListItemResponse> listCauseList(Principal principal, LocalDate date) {
        return listRoznamaTable(principal, date).getRows();
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

    @Transactional
    public CaseWorkflowActionResponse draftNotice(Long caseId, CaseNoticeDraftRequest request, Principal principal) {
        if (request == null || trimToNull(request.getDraftContent()) == null) {
            throw new IllegalArgumentException("draftContent is required.");
        }
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertClerk(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseNotice row = new CaseNotice();
        row.setCaseRegistry(caseRow);
        if (request.getHearingId() != null) {
            CaseHearing hearing = caseHearingRepository.findById(request.getHearingId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid hearingId."));
            if (!Objects.equals(hearing.getCaseRegistry().getId(), caseId)) {
                throw new IllegalArgumentException("hearingId does not belong to case.");
            }
            row.setHearing(hearing);
        }
        row.setNoticeType(trimToNull(request.getNoticeType()) != null ? request.getNoticeType().trim() : "HEARING_NOTICE");
        row.setDraftContent(request.getDraftContent().trim());
        row.setSelectedPartiesJson(toJsonArray(request.getSelectedParties()));
        row.setStatus(CaseNoticeStatus.CLERK_DRAFT);
        row.setClerkDraftedByLoginId(login);
        row = caseNoticeRepository.save(row);

        return buildWorkflowAction(caseId, row.getId(), row.getStatus().name(), "Notice draft saved by clerk.");
    }

    @Transactional
    public CaseWorkflowActionResponse submitNoticeToPo(Long caseId, Long noticeId, Principal principal) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertClerk(posting);
        resolveOfficerCase(caseId, login);

        CaseNotice row = caseNoticeRepository.findByIdAndCaseRegistryId(noticeId, caseId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found for case."));
        if (row.getStatus() != CaseNoticeStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException("Only clerk draft notice can be submitted to PO scrutiny.");
        }
        row.setStatus(CaseNoticeStatus.PO_SCRUTINY);
        row = caseNoticeRepository.save(row);
        return buildWorkflowAction(caseId, row.getId(), row.getStatus().name(), "Notice submitted to PO for scrutiny.");
    }

    @Transactional
    public CaseWorkflowActionResponse finalizeNotice(Long caseId, Long noticeId, CaseNoticeFinalizeRequest request, Principal principal) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        resolveOfficerCase(caseId, login);
        CaseNotice row = caseNoticeRepository.findByIdAndCaseRegistryId(noticeId, caseId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found for case."));
        if (row.getStatus() != CaseNoticeStatus.PO_SCRUTINY) {
            throw new IllegalArgumentException("Only notice under PO scrutiny can be finalized.");
        }
        String content = request != null ? trimToNull(request.getFinalContent()) : null;
        if (content == null) {
            content = trimToNull(row.getDraftContent());
        }
        if (content == null) {
            throw new IllegalArgumentException("Notice draft content is missing; cannot finalize.");
        }
        row.setFinalContent(content);
        row.setPoFinalizedByLoginId(login);
        row.setStatus(CaseNoticeStatus.PO_FINALIZED);
        row = caseNoticeRepository.save(row);
        return buildWorkflowAction(caseId, row.getId(), row.getStatus().name(), "Notice finalized by PO.");
    }

    @Transactional
    public CaseWorkflowActionResponse signNotice(Long caseId, Long noticeId, CaseNoticeSignRequest request, Principal principal) {
        if (request == null || trimToNull(request.getDigitalSignatureRef()) == null) {
            throw new IllegalArgumentException("digitalSignatureRef is required.");
        }
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        resolveOfficerCase(caseId, login);
        CaseNotice row = caseNoticeRepository.findByIdAndCaseRegistryId(noticeId, caseId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found for case."));
        if (row.getStatus() != CaseNoticeStatus.PO_FINALIZED) {
            throw new IllegalArgumentException("Only finalized notice can be digitally signed.");
        }
        row.setDigitalSignatureRef(request.getDigitalSignatureRef().trim());
        row.setPoSignedByLoginId(login);
        row.setStatus(CaseNoticeStatus.PO_SIGNED);
        row = caseNoticeRepository.save(row);
        return buildWorkflowAction(caseId, row.getId(), row.getStatus().name(), "Notice digitally signed by PO.");
    }

    @Transactional
    public CaseWorkflowActionResponse revertNoticeToClerk(Long caseId, Long noticeId, CaseWorkflowRevertRequest request, Principal principal) {
        String remarks = requiredText(request != null ? request.getRemarks() : null, "remarks");
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        resolveOfficerCase(caseId, login);

        CaseNotice row = caseNoticeRepository.findByIdAndCaseRegistryId(noticeId, caseId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found for case."));
        if (row.getStatus() == CaseNoticeStatus.SERVED) {
            throw new IllegalArgumentException("Served notice cannot be reverted.");
        }
        if (row.getStatus() == CaseNoticeStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException("Notice is still in clerk draft. Submit to PO scrutiny before revert.");
        }
        if (trimToNull(row.getFinalContent()) != null) {
            row.setDraftContent(row.getFinalContent().trim());
        }
        row.setFinalContent(null);
        row.setDigitalSignatureRef(null);
        row.setPoSignedByLoginId(null);
        row.setPoFinalizedByLoginId(null);
        row.setStatus(CaseNoticeStatus.CLERK_DRAFT);
        row = caseNoticeRepository.save(row);
        return buildWorkflowAction(caseId, row.getId(), row.getStatus().name(), "Notice reverted to clerk: " + remarks);
    }

    @Transactional
    public CaseWorkflowActionResponse serveNotice(Long caseId, Long noticeId, Principal principal) {
        String login = normalizeLogin(principal);
        resolveOfficerCase(caseId, login);
        CaseNotice row = caseNoticeRepository.findByIdAndCaseRegistryId(noticeId, caseId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found for case."));
        if (row.getStatus() != CaseNoticeStatus.PO_SIGNED) {
            throw new IllegalArgumentException("Only signed notice can be served.");
        }
        row.setStatus(CaseNoticeStatus.SERVED);
        row.setServedAt(Instant.now());
        row.setServedByLoginId(login);
        row = caseNoticeRepository.save(row);
        return buildWorkflowAction(caseId, row.getId(), row.getStatus().name(), "Notice served to selected parties.");
    }

    @Transactional(readOnly = true)
    public CaseJudgmentWorkflowResponse getJudgmentWorkflow(Long caseId, Principal principal) {
        String login = normalizeLogin(principal);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null);
        return toJudgmentWorkflowResponse(caseRow, row);
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
        resolveOfficerPosting(login);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElseGet(CaseJudgmentWorkflow::new);
        if (row.getCaseRegistry() == null) {
            row.setCaseRegistry(caseRow);
        }
        if (row.getStatus() == CaseJudgmentWorkflowStatus.PUBLISHED) {
            throw new IllegalArgumentException("Case judgment is already published.");
        }
        if (row.getStatus() != null && row.getStatus() != CaseJudgmentWorkflowStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException(
                    "Judgment draft can be edited only in clerk draft stage. Revert from PO or wait for PO action."
            );
        }
        row.setDraftSummary(summary.trim());
        row.setDraftedByLoginId(login);
        row.setStatus(CaseJudgmentWorkflowStatus.CLERK_DRAFT);
        row = caseJudgmentWorkflowRepository.save(row);
        return toJudgmentWorkflowResponse(caseRow, row);
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
        if (row.getStatus() != CaseJudgmentWorkflowStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException("Only clerk draft judgment can be submitted to PO scrutiny.");
        }
        row.setStatus(CaseJudgmentWorkflowStatus.PO_SCRUTINY);
        row = caseJudgmentWorkflowRepository.save(row);
        return toJudgmentWorkflowResponse(caseRow, row);
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
        if (row.getStatus() != CaseJudgmentWorkflowStatus.PO_SCRUTINY) {
            throw new IllegalArgumentException("Only judgment under PO scrutiny can be finalized by PO.");
        }
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
        return toJudgmentWorkflowResponse(caseRow, row);
    }

    @Transactional
    public CaseJudgmentWorkflowResponse revertJudgmentToClerk(Long caseId, CaseWorkflowRevertRequest request, Principal principal) {
        requiredText(request != null ? request.getRemarks() : null, "remarks");
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        if ("DISPOSED".equalsIgnoreCase(caseRow.getStatus())) {
            throw new IllegalArgumentException("Published/disposed judgment cannot be reverted.");
        }
        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Judgment workflow not found."));
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
        row.setStatus(CaseJudgmentWorkflowStatus.CLERK_DRAFT);
        row = caseJudgmentWorkflowRepository.save(row);
        return toJudgmentWorkflowResponse(caseRow, row);
    }

    @Transactional
    public CaseJudgmentResponse publishJudgment(Long caseId, Principal principal) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);

        CaseJudgmentWorkflow row = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Judgment workflow not found."));
        if (row.getStatus() != CaseJudgmentWorkflowStatus.PO_FINALIZED) {
            throw new IllegalArgumentException("Only PO finalized judgment can be published.");
        }
        String summary = trimToNull(row.getFinalSummary());
        if (summary == null) {
            throw new IllegalArgumentException("Final judgment summary is missing.");
        }

        row.setPublishedSummary(summary);
        row.setPublishedByLoginId(login);
        row.setPublishedAt(Instant.now());
        row.setStatus(CaseJudgmentWorkflowStatus.PUBLISHED);
        caseJudgmentWorkflowRepository.save(row);

        caseRow.setStatus("DISPOSED");
        caseRow.setDisposedAt(Instant.now());
        caseRow.setDisposedByOfficerLoginId(login);
        caseRow.setJudgmentSummary(summary);
        caseRegistryRepository.save(caseRow);

        CaseJudgmentResponse out = new CaseJudgmentResponse();
        out.setCaseId(caseRow.getId());
        out.setCaseNo(caseRow.getCaseNo());
        out.setStatus(caseRow.getStatus());
        out.setDisposedAt(caseRow.getDisposedAt());
        out.setMessage("Judgment published and case disposed.");
        return out;
    }

    @Transactional
    public CaseHearingResponse scheduleHearing(Long caseId, CaseHearingScheduleRequest request, Principal principal) {
        if (request == null || request.getHearingDate() == null) {
            throw new IllegalArgumentException("hearingDate is required.");
        }
        String login = normalizeLogin(principal);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        Integer nextHearingNo = caseHearingRepository.findFirstByCaseRegistryIdOrderByHearingNoDesc(caseId)
                .map(CaseHearing::getHearingNo)
                .map(x -> x + 1)
                .orElse(1);

        CaseHearing row = new CaseHearing();
        row.setCaseRegistry(caseRow);
        row.setHearingNo(nextHearingNo);
        row.setHearingDate(request.getHearingDate());
        row.setStatus("SCHEDULED");
        row.setNoticeGenerated(Boolean.TRUE.equals(request.getNoticeGenerate()));
        row.setRemarks(trimToNull(request.getRemarks()));
        row.setCreatedByLoginId(login);
        row = caseHearingRepository.save(row);

        if (!"HEARING_SCHEDULED".equalsIgnoreCase(caseRow.getStatus())) {
            caseRow.setStatus("HEARING_SCHEDULED");
            caseRegistryRepository.save(caseRow);
        }
        return toHearingResponse(row);
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
    public CaseOrderSheetResponse upsertOrderSheet(Long caseId, CaseOrderSheetUpsertRequest request, Principal principal) {
        if (request == null || trimToNull(request.getContent()) == null) {
            throw new IllegalArgumentException("content is required.");
        }
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        LocalDate targetHearingDate = request.getHearingDate() != null ? request.getHearingDate() : LocalDate.now();
        CaseHearing hearing = resolveHearingForRoznama(
                caseRow,
                request.getHearingId(),
                request.getHearingDate(),
                targetHearingDate
        );

        CaseOrderSheet sheet = resolveOrCreateOrderSheet(caseRow);
        prepareSheetForHearingDraft(sheet, hearing, login);
        if (sheet.getStatus() != null && sheet.getStatus() != CaseOrderSheetStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException("Roznama can be edited only in draft stage.");
        }
        sheet.setDraftContent(request.getContent().trim());
        sheet.setFinalContent(null);
        sheet.setDigitalSignatureRef(null);
        sheet.setStatus(CaseOrderSheetStatus.CLERK_DRAFT);
        sheet.setDraftedByLoginId(login);
        sheet.setPoFinalizedByLoginId(null);
        sheet.setPoSignedByLoginId(null);
        sheet.setCurrentHearing(hearing);
        sheet.setContent(sheet.getDraftContent());
        sheet.setUpdatedByLoginId(login);
        sheet = caseOrderSheetRepository.save(sheet);

        CaseOrderSheetHistory hist = new CaseOrderSheetHistory();
        hist.setCaseRegistry(caseRow);
        hist.setCaseHearing(hearing);
        hist.setContent(sheet.getDraftContent());
        hist.setRemarks(withStage(isPo(posting) ? "PO_DRAFT" : "CLERK_DRAFT", request.getRemarks()));
        hist.setCreatedByLoginId(login);
        caseOrderSheetHistoryRepository.save(hist);

        return toOrderSheetResponse(sheet);
    }

    @Transactional
    public CaseOrderSheetResponse submitOrderSheetToPo(Long caseId, Principal principal) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertClerk(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Order sheet draft not found."));
        if (sheet.getStatus() != CaseOrderSheetStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException("Only clerk draft order sheet can be submitted to PO scrutiny.");
        }
        sheet.setStatus(CaseOrderSheetStatus.PO_SCRUTINY);
        sheet.setUpdatedByLoginId(login);
        sheet = caseOrderSheetRepository.save(sheet);

        CaseOrderSheetHistory hist = new CaseOrderSheetHistory();
        hist.setCaseRegistry(caseRow);
        hist.setCaseHearing(sheet.getCurrentHearing());
        hist.setContent(sheet.getDraftContent() != null ? sheet.getDraftContent() : sheet.getContent());
        hist.setRemarks(withStage("PO_SCRUTINY", null));
        hist.setCreatedByLoginId(login);
        caseOrderSheetHistoryRepository.save(hist);
        return toOrderSheetResponse(sheet);
    }

    @Transactional
    public CaseOrderSheetResponse finalizeOrderSheet(Long caseId, CaseOrderSheetFinalizeRequest request, Principal principal) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);
        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Order sheet draft not found."));
        if (sheet.getStatus() != CaseOrderSheetStatus.PO_SCRUTINY) {
            throw new IllegalArgumentException("Only order sheet under PO scrutiny can be finalized.");
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
    public CaseOrderSheetResponse signOrderSheet(Long caseId, CaseOrderSheetSignRequest request, Principal principal) {
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
        if (sheet.getStatus() == CaseOrderSheetStatus.PO_SCRUTINY) {
            String finalText = trimToNull(sheet.getFinalContent());
            if (finalText == null) {
                finalText = trimToNull(sheet.getDraftContent());
            }
            if (finalText == null) {
                finalText = trimToNull(sheet.getContent());
            }
            if (finalText == null) {
                throw new IllegalArgumentException("Roznama content is required before sign.");
            }
            sheet.setFinalContent(finalText);
            sheet.setContent(finalText);
            sheet.setStatus(CaseOrderSheetStatus.PO_FINALIZED);
            sheet.setPoFinalizedByLoginId(login);
        }
        if (sheet.getStatus() != CaseOrderSheetStatus.PO_FINALIZED) {
            throw new IllegalArgumentException("Only PO finalized roznama can be signed.");
        }
        sheet.setDigitalSignatureRef(request.getDigitalSignatureRef().trim());
        sheet.setStatus(CaseOrderSheetStatus.PO_SIGNED);
        sheet.setPoSignedByLoginId(login);
        sheet.setUpdatedByLoginId(login);
        sheet = caseOrderSheetRepository.save(sheet);

        if (sheet.getCurrentHearing() != null && !"COMPLETED".equalsIgnoreCase(sheet.getCurrentHearing().getStatus())) {
            sheet.getCurrentHearing().setStatus("COMPLETED");
            caseHearingRepository.save(sheet.getCurrentHearing());
        }

        CaseOrderSheetHistory hist = new CaseOrderSheetHistory();
        hist.setCaseRegistry(caseRow);
        hist.setCaseHearing(sheet.getCurrentHearing());
        hist.setContent(sheet.getFinalContent() != null ? sheet.getFinalContent() : sheet.getContent());
        hist.setRemarks(withStage("PO_SIGNED", request.getRemarks()));
        hist.setCreatedByLoginId(login);
        caseOrderSheetHistoryRepository.save(hist);
        return toOrderSheetResponse(sheet);
    }

    @Transactional
    public CaseOrderSheetResponse revertOrderSheetToClerk(Long caseId, CaseWorkflowRevertRequest request, Principal principal) {
        String remarks = requiredText(request != null ? request.getRemarks() : null, "remarks");
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        assertPo(posting);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);
        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Order sheet not found."));
        if (sheet.getStatus() == CaseOrderSheetStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException("Order sheet is still in clerk draft. Submit to PO scrutiny before revert.");
        }
        if (trimToNull(sheet.getFinalContent()) != null) {
            sheet.setDraftContent(sheet.getFinalContent().trim());
        } else if (trimToNull(sheet.getContent()) != null) {
            sheet.setDraftContent(sheet.getContent().trim());
        }
        sheet.setFinalContent(null);
        sheet.setDigitalSignatureRef(null);
        sheet.setPoFinalizedByLoginId(null);
        sheet.setPoSignedByLoginId(null);
        sheet.setStatus(CaseOrderSheetStatus.CLERK_DRAFT);
        sheet.setContent(sheet.getDraftContent());
        sheet.setUpdatedByLoginId(login);
        sheet = caseOrderSheetRepository.save(sheet);

        CaseOrderSheetHistory hist = new CaseOrderSheetHistory();
        hist.setCaseRegistry(caseRow);
        hist.setCaseHearing(sheet.getCurrentHearing());
        hist.setContent(sheet.getDraftContent() != null ? sheet.getDraftContent() : "");
        hist.setRemarks(withStage("PO_REVERTED_TO_CLERK", remarks));
        hist.setCreatedByLoginId(login);
        caseOrderSheetHistoryRepository.save(hist);
        return toOrderSheetResponse(sheet);
    }

    @Transactional(readOnly = true)
    public CaseOrderSheetResponse getOrderSheet(Long caseId, Principal principal) {
        String login = normalizeLogin(principal);
        resolveOfficerCase(caseId, login);
        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Order sheet not found."));
        return toOrderSheetResponse(sheet);
    }

    @Transactional(readOnly = true)
    public List<CaseOrderSheetHistoryResponse> getOrderSheetHistory(Long caseId, Principal principal) {
        return getRoznamaHistory(caseId, null, principal);
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
        if (view.linkedToHearing && sheet != null) {
            RoznamaResponse out = toRoznamaResponse(toOrderSheetResponse(sheet));
            out.setHearingId(hearing.getId());
            return out;
        }
        RoznamaResponse out = new RoznamaResponse();
        out.setCaseId(caseRow.getId());
        out.setCaseNo(caseRow.getCaseNo());
        out.setHearingId(hearing.getId());
        out.setId(view.roznamaId);
        out.setStatus(view.roznamaStatus);
        out.setDraftContent(view.draftContent);
        out.setFinalContent(view.finalContent);
        out.setContent(view.finalContent != null ? view.finalContent : view.draftContent);
        out.setUpdatedAt(view.updatedAt);
        return out;
    }

    @Transactional
    public RoznamaResponse draftRoznama(Long caseId, CaseOrderSheetUpsertRequest request, Principal principal) {
        return toRoznamaResponse(upsertOrderSheet(caseId, request, principal));
    }

    @Transactional
    public RoznamaResponse finalizeRoznama(
            Long caseId,
            Long roznamaId,
            Long hearingId,
            LocalDate hearingDate,
            CaseOrderSheetFinalizeRequest request,
            Principal principal
    ) {
        String login = normalizeLogin(principal);
        if (roznamaId != null) {
            resolveRoznama(caseId, roznamaId, login);
        }
        ensureRoznamaHearingContext(caseId, hearingId, hearingDate, login);
        return toRoznamaResponse(finalizeOrderSheet(caseId, request, principal));
    }

    @Transactional
    public RoznamaResponse submitRoznamaToPo(
            Long caseId,
            Long roznamaId,
            Long hearingId,
            LocalDate hearingDate,
            Principal principal
    ) {
        String login = normalizeLogin(principal);
        if (roznamaId != null) {
            resolveRoznama(caseId, roznamaId, login);
        }
        ensureRoznamaHearingContext(caseId, hearingId, hearingDate, login);
        return toRoznamaResponse(submitOrderSheetToPo(caseId, principal));
    }

    @Transactional
    public RoznamaResponse signRoznama(
            Long caseId,
            Long roznamaId,
            Long hearingId,
            LocalDate hearingDate,
            CaseOrderSheetSignRequest request,
            Principal principal
    ) {
        String login = normalizeLogin(principal);
        if (roznamaId != null) {
            resolveRoznama(caseId, roznamaId, login);
        }
        ensureRoznamaHearingContext(caseId, hearingId, hearingDate, login);
        return toRoznamaResponse(signOrderSheet(caseId, request, principal));
    }

    @Transactional
    public RoznamaResponse revertRoznamaToClerk(
            Long caseId,
            Long roznamaId,
            Long hearingId,
            LocalDate hearingDate,
            CaseWorkflowRevertRequest request,
            Principal principal
    ) {
        String login = normalizeLogin(principal);
        if (roznamaId != null) {
            resolveRoznama(caseId, roznamaId, login);
        }
        ensureRoznamaHearingContext(caseId, hearingId, hearingDate, login);
        return toRoznamaResponse(revertOrderSheetToClerk(caseId, request, principal));
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

    @Transactional
    public CaseJudgmentResponse passFinalJudgment(Long caseId, CaseJudgmentRequest request, Principal principal) {
        if (request == null || trimToNull(request.getJudgmentSummary()) == null) {
            throw new IllegalArgumentException("judgmentSummary is required.");
        }
        String login = normalizeLogin(principal);
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        if ("DISPOSED".equalsIgnoreCase(caseRow.getStatus())) {
            CaseJudgmentResponse existing = new CaseJudgmentResponse();
            existing.setCaseId(caseRow.getId());
            existing.setCaseNo(caseRow.getCaseNo());
            existing.setStatus(caseRow.getStatus());
            existing.setDisposedAt(caseRow.getDisposedAt());
            existing.setMessage("Case already disposed.");
            return existing;
        }

        caseRow.setStatus("DISPOSED");
        caseRow.setDisposedAt(Instant.now());
        caseRow.setDisposedByOfficerLoginId(login);
        caseRow.setJudgmentSummary(request.getJudgmentSummary().trim());
        caseRegistryRepository.save(caseRow);

        CaseJudgmentWorkflow wf = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElseGet(CaseJudgmentWorkflow::new);
        if (wf.getCaseRegistry() == null) {
            wf.setCaseRegistry(caseRow);
        }
        wf.setFinalSummary(request.getJudgmentSummary().trim());
        wf.setPublishedSummary(request.getJudgmentSummary().trim());
        wf.setFinalizedByLoginId(login);
        wf.setPublishedByLoginId(login);
        wf.setPublishedAt(caseRow.getDisposedAt());
        wf.setStatus(CaseJudgmentWorkflowStatus.PUBLISHED);
        caseJudgmentWorkflowRepository.save(wf);

        CaseJudgmentResponse out = new CaseJudgmentResponse();
        out.setCaseId(caseRow.getId());
        out.setCaseNo(caseRow.getCaseNo());
        out.setStatus(caseRow.getStatus());
        out.setDisposedAt(caseRow.getDisposedAt());
        out.setMessage("Final judgment saved and case disposed.");
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

    private static boolean isPo(EmployeePosting posting) {
        Long designationId = posting.getDesignation() != null ? posting.getDesignation().getId() : null;
        return Objects.equals(designationId, 1L);
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

    private static String requiredText(String value, String field) {
        String t = trimToNull(value);
        if (t == null) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return t;
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

    private static CaseHearingResponse toHearingResponse(CaseHearing row) {
        CaseHearingResponse out = new CaseHearingResponse();
        out.setHearingId(row.getId());
        out.setCaseId(row.getCaseRegistry() != null ? row.getCaseRegistry().getId() : null);
        out.setCaseNo(row.getCaseRegistry() != null ? row.getCaseRegistry().getCaseNo() : null);
        out.setHearingNo(row.getHearingNo());
        out.setHearingDate(row.getHearingDate());
        out.setStatus(row.getStatus());
        out.setNoticeGenerated(row.getNoticeGenerated());
        out.setRemarks(row.getRemarks());
        out.setCreatedAt(row.getCreatedAt());
        out.setUpdatedAt(row.getUpdatedAt());
        return out;
    }

    private CaseOrderSheet resolveRoznama(Long caseId, Long roznamaId, String login) {
        if (roznamaId == null) {
            throw new IllegalArgumentException("roznama id is required.");
        }
        resolveOfficerCase(caseId, login);
        CaseOrderSheet sheet = caseOrderSheetRepository.findById(roznamaId)
                .orElseThrow(() -> new IllegalArgumentException("Roznama not found."));
        if (sheet.getCaseRegistry() == null || !Objects.equals(sheet.getCaseRegistry().getId(), caseId)) {
            throw new IllegalArgumentException("Roznama does not belong to case.");
        }
        return sheet;
    }

    private void ensureRoznamaHearingContext(Long caseId, Long hearingId, LocalDate hearingDate, String login) {
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Roznama not found. Save draft first."));
        if (hearingId == null && hearingDate == null) {
            if (sheet.getCurrentHearing() == null) {
                throw new IllegalArgumentException("hearingId or hearingDate is required.");
            }
            return;
        }
        LocalDate defaultDate = hearingDate != null ? hearingDate : LocalDate.now();
        CaseHearing hearing = resolveHearingForRoznama(caseRow, hearingId, hearingDate, defaultDate);
        if (sheet.getCurrentHearing() != null && !Objects.equals(sheet.getCurrentHearing().getId(), hearing.getId())) {
            throw new IllegalArgumentException(
                    "Roznama is linked to a different hearing. Save draft again with this hearingId/hearingDate."
            );
        }
        if (sheet.getCurrentHearing() == null) {
            sheet.setCurrentHearing(hearing);
            sheet.setUpdatedByLoginId(login);
            caseOrderSheetRepository.save(sheet);
        }
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
        boolean sameHearing = sheet.getCurrentHearing() != null
                && Objects.equals(sheet.getCurrentHearing().getId(), hearing.getId());
        if (sameHearing) {
            return;
        }
        if (sheet.getStatus() == CaseOrderSheetStatus.PO_SIGNED) {
            sheet.setStatus(CaseOrderSheetStatus.CLERK_DRAFT);
            sheet.setDraftContent("");
            sheet.setFinalContent(null);
            sheet.setDigitalSignatureRef(null);
            sheet.setPoFinalizedByLoginId(null);
            sheet.setPoSignedByLoginId(null);
            sheet.setContent("");
            sheet.setCurrentHearing(hearing);
            sheet.setUpdatedByLoginId(login);
            return;
        }
        if (sheet.getStatus() != null && sheet.getStatus() != CaseOrderSheetStatus.CLERK_DRAFT) {
            throw new IllegalArgumentException(
                    "Roznama for another hearing is in progress. Complete, sign, or revert it before starting this hearing date."
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
        view.proceedingStage = "ROZNAMA_NOT_STARTED";
        view.canEdit = !hasInProgressRoznamaOnOtherHearing(sheet, hearing);

        if (sheet != null && sheet.getCurrentHearing() != null
                && Objects.equals(sheet.getCurrentHearing().getId(), hearing.getId())) {
            view.linkedToHearing = true;
            view.roznamaId = sheet.getId();
            view.roznamaStatus = sheet.getStatus() != null ? sheet.getStatus().name() : null;
            view.proceedingStage = toRoznamaProceedingStage(sheet.getStatus());
            view.draftContent = sheet.getDraftContent();
            view.finalContent = sheet.getFinalContent();
            view.updatedAt = sheet.getUpdatedAt();
            view.canEdit = sheet.getStatus() == CaseOrderSheetStatus.CLERK_DRAFT;
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
            case PO_SCRUTINY -> "ROZNAMA_PO_SCRUTINY";
            case PO_FINALIZED -> "ROZNAMA_PO_FINALIZED";
            case PO_SIGNED -> "ROZNAMA_PO_SIGNED";
        };
    }

    private static String parseHistoryStatus(String remarks) {
        String r = trimToNull(remarks);
        if (r == null) {
            return null;
        }
        int sep = r.indexOf(" | ");
        return sep >= 0 ? r.substring(0, sep) : r;
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

    private static CaseWorkflowActionResponse buildWorkflowAction(Long caseId, Long noticeId, String status, String message) {
        CaseWorkflowActionResponse out = new CaseWorkflowActionResponse();
        out.setCaseId(caseId);
        out.setNoticeId(noticeId);
        out.setStatus(status);
        out.setMessage(message);
        return out;
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

    private static CaseJudgmentWorkflowResponse toJudgmentWorkflowResponse(CaseRegistry caseRow, CaseJudgmentWorkflow row) {
        CaseJudgmentWorkflowResponse out = new CaseJudgmentWorkflowResponse();
        out.setCaseId(caseRow.getId());
        out.setCaseNo(caseRow.getCaseNo());
        out.setCaseStatus(caseRow.getStatus());
        if (row != null) {
            out.setWorkflowStatus(row.getStatus() != null ? row.getStatus().name() : null);
            out.setDraftSummary(row.getDraftSummary());
            out.setFinalSummary(row.getFinalSummary());
            out.setPublishedSummary(row.getPublishedSummary());
            out.setPublishedAt(row.getPublishedAt());
            out.setUpdatedAt(row.getUpdatedAt());
        }
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

    private static CaseInboxItemResponse toCaseInboxItem(CaseRegistry row, CaseOrderSheet roznama) {
        CaseInboxItemResponse out = new CaseInboxItemResponse();
        out.setCaseId(row.getId());
        out.setCaseNo(row.getCaseNo());
        out.setStatus(row.getStatus());
        out.setFilingApplicationId(row.getFilingApplicationId());
        out.setCaseCategoryId(row.getCaseCategory() != null ? row.getCaseCategory().getId() : null);
        out.setCaseCategoryName(row.getCaseCategory() != null ? row.getCaseCategory().getName() : null);
        out.setOfficeId(row.getOffice() != null ? row.getOffice().getId() : null);
        out.setOfficeName(row.getOffice() != null ? row.getOffice().getName() : null);
        out.setApprovedAt(row.getApprovedAt());
        out.setDisposedAt(row.getDisposedAt());
        if (roznama != null) {
            out.setRoznamaId(roznama.getId());
            out.setProceedingStage(toRoznamaProceedingStage(roznama.getStatus()));
        } else {
            out.setProceedingStage("ROZNAMA_NOT_STARTED");
        }
        return out;
    }
}
