package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.workflow.CaseWorkflowContextResponse;
import com.maharashtra.rccms.dto.workflow.WorkflowArtifactContextResponse;
import com.maharashtra.rccms.dto.workflow.WorkflowHearingContextResponse;
import com.maharashtra.rccms.model.EmployeePosting;
import com.maharashtra.rccms.model.caseflow.CaseHearing;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflow;
import com.maharashtra.rccms.model.caseflow.CaseNotice;
import com.maharashtra.rccms.model.caseflow.CaseNoticeStatus;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheet;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.repository.CaseHearingRepository;
import com.maharashtra.rccms.repository.CaseJudgmentWorkflowRepository;
import com.maharashtra.rccms.repository.CaseNoticeRepository;
import com.maharashtra.rccms.repository.CaseOrderSheetRepository;
import com.maharashtra.rccms.repository.CaseRegistryRepository;
import com.maharashtra.rccms.repository.EmployeePostingRepository;
import com.maharashtra.rccms.repository.EmployeeRepository;
import com.maharashtra.rccms.workflow.config.CaseWorkflowDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@SuppressWarnings("null")
public class WorkflowContextService {

    private final CaseRegistryRepository caseRegistryRepository;
    private final CaseHearingRepository caseHearingRepository;
    private final CaseNoticeRepository caseNoticeRepository;
    private final CaseOrderSheetRepository caseOrderSheetRepository;
    private final CaseJudgmentWorkflowRepository caseJudgmentWorkflowRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeePostingRepository employeePostingRepository;
    private final WorkflowPolicyService workflowPolicyService;
    private final CaseWorkflowConfigService workflowConfigService;

    public WorkflowContextService(
            CaseRegistryRepository caseRegistryRepository,
            CaseHearingRepository caseHearingRepository,
            CaseNoticeRepository caseNoticeRepository,
            CaseOrderSheetRepository caseOrderSheetRepository,
            CaseJudgmentWorkflowRepository caseJudgmentWorkflowRepository,
            EmployeeRepository employeeRepository,
            EmployeePostingRepository employeePostingRepository,
            WorkflowPolicyService workflowPolicyService,
            CaseWorkflowConfigService workflowConfigService
    ) {
        this.caseRegistryRepository = caseRegistryRepository;
        this.caseHearingRepository = caseHearingRepository;
        this.caseNoticeRepository = caseNoticeRepository;
        this.caseOrderSheetRepository = caseOrderSheetRepository;
        this.caseJudgmentWorkflowRepository = caseJudgmentWorkflowRepository;
        this.employeeRepository = employeeRepository;
        this.employeePostingRepository = employeePostingRepository;
        this.workflowPolicyService = workflowPolicyService;
        this.workflowConfigService = workflowConfigService;
    }

    @Transactional(readOnly = true)
    public CaseWorkflowContextResponse buildCaseContext(Long caseId, Long hearingId, Principal principal) {
        String login = normalizeLogin(principal);
        EmployeePosting posting = resolveOfficerPosting(login);
        Long officeId = posting.getOffice() != null ? posting.getOffice().getId() : null;
        CaseRegistry caseRow = caseRegistryRepository.findByIdAndOfficeId(caseId, officeId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found for officer office."));

        CaseWorkflowDefinition def = workflowConfigService.resolveForCategory(caseRow.getCaseCategory());
        List<CaseHearing> hearings = caseHearingRepository.findByCaseRegistryIdOrderByHearingNoAsc(caseId);
        CaseHearing activeHearing = resolveActiveHearing(hearings, hearingId);
        CaseNotice draftNotice = activeHearing != null
                ? caseNoticeRepository.findFirstByHearingIdAndStatusInOrderByIdDesc(
                        activeHearing.getId(),
                        WorkflowPolicyService.editableNoticeStatuses()
                ).orElse(null)
                : null;
        if (draftNotice == null && activeHearing != null && !Boolean.TRUE.equals(activeHearing.getNoticeServed())) {
            draftNotice = findActiveNoticeForHearing(activeHearing.getId());
        }

        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null);
        CaseJudgmentWorkflow judgment = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null);

        CaseWorkflowContextResponse out = new CaseWorkflowContextResponse();
        out.setCaseId(caseRow.getId());
        out.setCaseNo(caseRow.getCaseNo());
        out.setCaseStatus(caseRow.getStatus());
        out.setFilingApplicationId(caseRow.getFilingApplicationId());
        CaseCategory cat = caseRow.getCaseCategory();
        if (cat != null) {
            out.setCaseCategoryId(cat.getId());
            out.setCaseCategoryCode(cat.getCode());
            out.setCaseCategoryName(cat.getName());
        }
        out.setBlueprintCode(def.getBlueprintCode());

        WorkflowArtifactContextResponse noticeCtx = new WorkflowArtifactContextResponse();
        noticeCtx.setArtifact("NOTICE");
        if (draftNotice != null) {
            noticeCtx.setArtifactId(draftNotice.getId());
            noticeCtx.setStatus(draftNotice.getStatus() != null ? draftNotice.getStatus().name() : null);
        }
        if (activeHearing != null) {
            noticeCtx.setHearingId(activeHearing.getId());
            noticeCtx.setHearingNo(activeHearing.getHearingNo());
            noticeCtx.setNoticeServed(Boolean.TRUE.equals(activeHearing.getNoticeServed()));
        }
        noticeCtx.setConfig(def.getNotice());
        noticeCtx.setAllowedActions(workflowPolicyService.noticeAllowed(caseRow, posting, activeHearing, draftNotice));
        out.setNotice(noticeCtx);

        WorkflowArtifactContextResponse roznamaCtx = new WorkflowArtifactContextResponse();
        roznamaCtx.setArtifact("ROZNAMA");
        if (sheet != null) {
            roznamaCtx.setArtifactId(sheet.getId());
            roznamaCtx.setStatus(sheet.getStatus() != null ? sheet.getStatus().name() : null);
            if (sheet.getHearingOutcome() != null) {
                roznamaCtx.setHearingOutcome(sheet.getHearingOutcome().name());
            }
        }
        if (activeHearing != null) {
            roznamaCtx.setHearingId(activeHearing.getId());
            roznamaCtx.setHearingNo(activeHearing.getHearingNo());
            roznamaCtx.setNoticeServed(Boolean.TRUE.equals(activeHearing.getNoticeServed()));
        }
        roznamaCtx.setConfig(def.getRoznama());
        roznamaCtx.setAllowedActions(workflowPolicyService.roznamaAllowed(caseRow, posting, activeHearing, sheet));
        out.setRoznama(roznamaCtx);

        WorkflowArtifactContextResponse judgmentCtx = new WorkflowArtifactContextResponse();
        judgmentCtx.setArtifact("JUDGMENT");
        if (judgment != null) {
            judgmentCtx.setArtifactId(judgment.getId());
            judgmentCtx.setStatus(judgment.getStatus() != null ? judgment.getStatus().name() : null);
        }
        judgmentCtx.setConfig(def.getJudgment());
        judgmentCtx.setAllowedActions(workflowPolicyService.judgmentAllowed(caseRow, posting, judgment));
        out.setJudgment(judgmentCtx);

        List<WorkflowHearingContextResponse> hearingRows = new ArrayList<>();
        for (CaseHearing h : hearings) {
            WorkflowHearingContextResponse hr = new WorkflowHearingContextResponse();
            hr.setHearingId(h.getId());
            hr.setHearingNo(h.getHearingNo());
            hr.setHearingDate(h.getHearingDate());
            hr.setHearingStatus(h.getStatus());
            hr.setNoticeServed(Boolean.TRUE.equals(h.getNoticeServed()));
            CaseNotice activeNotice = findActiveNoticeForHearing(h.getId());
            if (activeNotice != null) {
                hr.setActiveNoticeId(activeNotice.getId());
                hr.setActiveNoticeStatus(activeNotice.getStatus() != null ? activeNotice.getStatus().name() : null);
            }
            CaseNotice editable = caseNoticeRepository.findFirstByHearingIdAndStatusInOrderByIdDesc(
                    h.getId(),
                    WorkflowPolicyService.editableNoticeStatuses()
            ).orElse(activeNotice);
            if (Objects.equals(h.getId(), activeHearing != null ? activeHearing.getId() : null)) {
                hr.setAllowedActions(workflowPolicyService.noticeAllowed(caseRow, posting, h, editable));
            }
            hearingRows.add(hr);
        }
        out.setHearings(hearingRows);

        out.setAllowedActions(workflowPolicyService.caseAllowedActions(
                caseRow,
                posting,
                activeHearing,
                draftNotice,
                sheet,
                judgment
        ));
        return out;
    }

    private static CaseHearing resolveActiveHearing(List<CaseHearing> hearings, Long hearingId) {
        if (hearings == null || hearings.isEmpty()) {
            return null;
        }
        if (hearingId != null) {
            for (CaseHearing h : hearings) {
                if (Objects.equals(h.getId(), hearingId)) {
                    return h;
                }
            }
        }
        CaseHearing latest = hearings.get(hearings.size() - 1);
        for (int i = hearings.size() - 1; i >= 0; i--) {
            CaseHearing h = hearings.get(i);
            if (!"COMPLETED".equalsIgnoreCase(h.getStatus())) {
                return h;
            }
        }
        return latest;
    }

    private EmployeePosting resolveOfficerPosting(String login) {
        var employee = employeeRepository.findFirstByEmailIgnoreCase(login)
                .or(() -> {
                    if (login.endsWith("@officer.local")) {
                        String code = login.substring(0, login.length() - "@officer.local".length()).trim();
                        return employeeRepository.findFirstByEmployeeCodeIgnoreCase(code);
                    }
                    return java.util.Optional.empty();
                })
                .orElseThrow(() -> new IllegalArgumentException("Officer employee profile not found."));
        return employeePostingRepository.findFirstByEmployeeIdAndToDateIsNull(employee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Current posting not found for officer."));
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

    private static String normalizeLogin(Principal principal) {
        Objects.requireNonNull(principal);
        return principal.getName().trim().toLowerCase(Locale.ROOT);
    }
}
