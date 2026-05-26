package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.filing.*;
import com.maharashtra.rccms.filing.DisputedLandFieldSupport;
import com.maharashtra.rccms.filing.FilingJsonCodec;
import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.model.Employee;
import com.maharashtra.rccms.model.EmployeePosting;
import com.maharashtra.rccms.model.PartyInPersonRegistration;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.dto.caseflow.CaseHearingResponse;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetHistoryResponse;
import com.maharashtra.rccms.model.caseflow.CaseHearing;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflow;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflowHistory;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflowStatus;
import com.maharashtra.rccms.model.caseflow.CaseNotice;
import com.maharashtra.rccms.model.caseflow.CaseNoticeStatus;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheet;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheetHistory;
import com.maharashtra.rccms.model.filing.*;
import com.maharashtra.rccms.model.master.*;
import com.maharashtra.rccms.repository.*;
import com.maharashtra.rccms.workflow.WorkflowAction;
import com.maharashtra.rccms.workflow.config.CaseWorkflowDefinition;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Persists the Category 1 objection filing aggregate ({@link FilingApplication}). Case registry / numbering belongs to officer acceptance flows.
 */
@Service
@SuppressWarnings("null")
public class FilingApplicationService {
    private static final long PRESIDING_OFFICER_DESIGNATION_ID = 1L;
    private static final String PINCODE_REGEX = "^\\d{6}$";
    private static final String MOBILE_REGEX = "^\\d{10}$";
    private static final String DOB_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private final FilingApplicationRepository filingApplicationRepository;
    private final CaseRegistryRepository caseRegistryRepository;
    private final CaseCategoryRepository caseCategoryRepository;
    private final SubjectRepository subjectRepository;
    private final DistrictRepository districtRepository;
    private final SubdistrictRepository subdistrictRepository;
    private final TalukaRepository talukaRepository;
    private final OfficeRepository officeRepository;
    private final ActRepository actRepository;
    private final SectionRepository sectionRepository;
    private final AdvocateRegistrationRepository advocateRegistrationRepository;
    private final PartyInPersonRegistrationRepository partyInPersonRegistrationRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeePostingRepository employeePostingRepository;
    private final CaseNoticeRepository caseNoticeRepository;
    private final CaseHearingRepository caseHearingRepository;
    private final CaseOrderSheetRepository caseOrderSheetRepository;
    private final CaseOrderSheetHistoryRepository caseOrderSheetHistoryRepository;
    private final CaseJudgmentWorkflowRepository caseJudgmentWorkflowRepository;
    private final ApplicationHistoryRepository applicationHistoryRepository;
    private final WorkflowPolicyService workflowPolicyService;
    private final CaseWorkflowConfigService caseWorkflowConfigService;
    private final CaseJudgmentWorkflowHistoryRepository judgmentWorkflowHistoryRepository;
    private final FilingApplicationChildCleanup filingApplicationChildCleanup;
    private final ApplicationDocumentChecklistService applicationDocumentChecklistService;
    private final DocumentTypeRepository documentTypeRepository;
    private final FilingJsonCodec filingJsonCodec;

    public FilingApplicationService(
            FilingApplicationRepository filingApplicationRepository,
            CaseRegistryRepository caseRegistryRepository,
            CaseCategoryRepository caseCategoryRepository,
            SubjectRepository subjectRepository,
            DistrictRepository districtRepository,
            SubdistrictRepository subdistrictRepository,
            TalukaRepository talukaRepository,
            OfficeRepository officeRepository,
            ActRepository actRepository,
            SectionRepository sectionRepository,
            AdvocateRegistrationRepository advocateRegistrationRepository,
            PartyInPersonRegistrationRepository partyInPersonRegistrationRepository,
            EmployeeRepository employeeRepository,
            EmployeePostingRepository employeePostingRepository,
            CaseNoticeRepository caseNoticeRepository,
            CaseHearingRepository caseHearingRepository,
            CaseOrderSheetRepository caseOrderSheetRepository,
            CaseOrderSheetHistoryRepository caseOrderSheetHistoryRepository,
            CaseJudgmentWorkflowRepository caseJudgmentWorkflowRepository,
            ApplicationHistoryRepository applicationHistoryRepository,
            WorkflowPolicyService workflowPolicyService,
            CaseWorkflowConfigService caseWorkflowConfigService,
            CaseJudgmentWorkflowHistoryRepository judgmentWorkflowHistoryRepository,
            FilingApplicationChildCleanup filingApplicationChildCleanup,
            ApplicationDocumentChecklistService applicationDocumentChecklistService,
            DocumentTypeRepository documentTypeRepository,
            FilingJsonCodec filingJsonCodec
    ) {
        this.filingApplicationRepository = filingApplicationRepository;
        this.caseRegistryRepository = caseRegistryRepository;
        this.caseCategoryRepository = caseCategoryRepository;
        this.subjectRepository = subjectRepository;
        this.districtRepository = districtRepository;
        this.subdistrictRepository = subdistrictRepository;
        this.talukaRepository = talukaRepository;
        this.officeRepository = officeRepository;
        this.actRepository = actRepository;
        this.sectionRepository = sectionRepository;
        this.advocateRegistrationRepository = advocateRegistrationRepository;
        this.partyInPersonRegistrationRepository = partyInPersonRegistrationRepository;
        this.employeeRepository = employeeRepository;
        this.employeePostingRepository = employeePostingRepository;
        this.caseNoticeRepository = caseNoticeRepository;
        this.caseHearingRepository = caseHearingRepository;
        this.caseOrderSheetRepository = caseOrderSheetRepository;
        this.caseOrderSheetHistoryRepository = caseOrderSheetHistoryRepository;
        this.caseJudgmentWorkflowRepository = caseJudgmentWorkflowRepository;
        this.applicationHistoryRepository = applicationHistoryRepository;
        this.workflowPolicyService = workflowPolicyService;
        this.caseWorkflowConfigService = caseWorkflowConfigService;
        this.judgmentWorkflowHistoryRepository = judgmentWorkflowHistoryRepository;
        this.filingApplicationChildCleanup = filingApplicationChildCleanup;
        this.applicationDocumentChecklistService = applicationDocumentChecklistService;
        this.documentTypeRepository = documentTypeRepository;
        this.filingJsonCodec = filingJsonCodec;
    }

    @Transactional
    public ApplicationSaveResponse save(ApplicationSavePayload payload, Principal principal) {
        Objects.requireNonNull(principal);
        Objects.requireNonNull(payload);

        ApplicationStatus status = parseStatus(requiredText(payload.getStatus(), "status"));
        AdvocateRegistration advocateFiler = null;
        PartyInPersonRegistration partyFiler = null;

        Optional<AdvocateRegistration> maybeAdvocate = advocateRegistrationRepository.findByEmail(normalizeLogin(principal.getName()));
        if (maybeAdvocate.isPresent()) {
            advocateFiler = maybeAdvocate.get();
        } else {
            partyFiler = partyInPersonRegistrationRepository.findByEmail(normalizeLogin(principal.getName()))
                    .orElseThrow(() -> new IllegalArgumentException("Unknown filer login."));
        }

        UUID clientRef = parseClientRef(payload.getClientApplicationRef());

        FilingApplication entity = locateOrCreate(zeroToNull(payload.getApplicationId()), clientRef);
        boolean isNewApplication = entity.getId() == null;
        ApplicationStatus previousStatus = isNewApplication
                ? null
                : filingApplicationRepository.findById(entity.getId()).map(FilingApplication::getStatus).orElse(null);

        CaseCategory category = caseCategoryRepository.findById(requiredId(payload.getCaseCategoryId(), "caseCategoryId"))
                .orElseThrow(() -> new IllegalArgumentException("Invalid caseCategoryId."));

        if (entity.getId() != null && payload.getForm() == null && !hasEvolvedSaveSections(payload)) {
            throw new IllegalArgumentException(
                    "Provide nested form or at least one of header, description, parties, disputed order/lands, attachments, or vakalatnama when updating.");
        }

        if (entity.getId() != null) {
            assertOwnership(entity, advocateFiler, partyFiler);
        }

        entity = replaceApplicationChildren(entity);

        entity.setCaseCategory(category);
        entity.setStatus(status);
        if (clientRef != null) {
            entity.setClientApplicationRef(clientRef);
        }
        attachFiler(entity, advocateFiler, partyFiler);

        boolean submit = status == ApplicationStatus.SUBMITTED;
        ApplicationFormNestedPayload form = payload.getForm();
        if (form != null) {
            applyFormHeader(entity, form);
            applyApplicants(entity, form.getApplicants(), submit);
            applyRespondents(entity, form.getRespondents(), submit);
            List<VakalatnamaGroupPayload> vakCombined = combineVakalatnama(
                    payload.getVakalatnamaAssignments(),
                    form.getVakalatnamaAssignments());
            applyVakalatnamaGroups(entity, vakCombined, principal.getName());
        } else {
            ApplicationFilingHeaderPayload header = payload.getHeader();
            if (header != null) {
                applyFilingHeader(entity, header);
            }
            applyApplicants(entity, payload.getApplicants(), submit);
            applyRespondents(entity, payload.getRespondents(), submit);
            applyVakalatnamaGroups(entity, payload.getVakalatnamaAssignments(), principal.getName());
        }

        applyDescription(entity, payload.getDescription());
        if (payload.getDescription() == null && form != null && hasText(trimToNull(form.getApplicationDescription()))) {
            applyDescription(entity, descriptionFromJoinedText(form.getApplicationDescription()));
        }

        ApplicationDisputedOrderPayload mergedDisputed = mergeDisputedEnvelope(payload);
        applyDisputedOrder(entity, mergedDisputed);

        applyDisputedLands(entity, payload.getDisputedLands());
        fillOfficeFromDisputedLandsIfMissing(entity);
        applyAttachments(entity, payload.getAttachments(), principal.getName());
        storeFormSnapshot(entity, payload);

        if (status == ApplicationStatus.SUBMITTED) {
            applicationDocumentChecklistService.validateMappedAttachmentDocumentTypes(entity);
            validateSubmission(entity);
            applicationDocumentChecklistService.validateRequiredUploadsOnSubmit(entity);
        }

        entity = filingApplicationRepository.save(entity);
        applicationDocumentChecklistService.syncChecklistFromApplication(entity);
        filingApplicationRepository.flush();
        entity = ensureApplicationNumber(entity);
        entity = ensureInitialClerkFlagsOnSubmit(entity, principal.getName());

        String filerRole = advocateFiler != null ? "ADVOCATE" : (partyFiler != null && partyFiler.getRole() != null
                ? partyFiler.getRole().name()
                : "PARTY_IN_PERSON");
        if (status == ApplicationStatus.SUBMITTED && previousStatus != ApplicationStatus.SUBMITTED) {
            recordApplicationHistory(entity, ApplicationHistoryAction.SUBMITTED, null, filerRole, principal.getName());
        } else if (isNewApplication && status == ApplicationStatus.DRAFT) {
            recordApplicationHistory(entity, ApplicationHistoryAction.DRAFT_SAVED, null, filerRole, principal.getName());
        }

        return buildResponse(entity);
    }

    @Transactional
    public OfficerCaseApprovalResponse approveApplication(Long applicationId, Principal principal) {
        if (applicationId == null) {
            throw new IllegalArgumentException("applicationId is required.");
        }
        Objects.requireNonNull(principal);
        String login = normalizeLogin(principal.getName());

        EmployeePosting posting = resolveOfficerCurrentPosting(login);
        Long designationId = posting.getDesignation() != null ? posting.getDesignation().getId() : null;
        if (!Objects.equals(designationId, PRESIDING_OFFICER_DESIGNATION_ID)) {
            throw new IllegalArgumentException("Only Presiding Officer can approve applications.");
        }

        Long officeId = posting.getOffice() != null ? posting.getOffice().getId() : null;
        if (officeId == null) {
            throw new IllegalArgumentException("Officer current posting office is missing.");
        }

        FilingApplication app = filingApplicationRepository.findByIdAndOfficeIdAndStatus(
                        applicationId,
                        officeId,
                        ApplicationStatus.SUBMITTED
                )
                .orElseThrow(() -> new IllegalArgumentException("Application not found for officer inbox."));
        if (!Boolean.TRUE.equals(app.getForwardedToPo()) || Boolean.TRUE.equals(app.getPoRejected())) {
            throw new IllegalArgumentException("Application is not forwarded to PO review.");
        }
        requireFilingAction(app, posting, WorkflowAction.PO_ACCEPT_CASE);

        app = ensureApplicationNumber(app);

        CaseRegistry existing = null;
        if (app.getRegisteredCaseId() != null) {
            existing = caseRegistryRepository.findById(app.getRegisteredCaseId()).orElse(null);
        }
        if (existing == null) {
            existing = caseRegistryRepository.findByFilingApplicationId(app.getId()).orElse(null);
        }
        if (existing != null) {
            if (app.getRegisteredCaseId() == null) {
                app.setRegisteredCaseId(existing.getId());
                app.setApprovedAt(existing.getApprovedAt());
                app.setApprovedByOfficerLoginId(existing.getApprovedByOfficerLoginId());
                filingApplicationRepository.save(app);
            }
            return buildApprovalResponse(app, existing, "Case already generated.");
        }

        CaseRegistry row = new CaseRegistry();
        row.setFilingApplicationId(app.getId());
        row.setCaseCategory(app.getCaseCategory());
        row.setOffice(app.getOffice());
        row.setApprovedByOfficerLoginId(login);
        // case_no is NOT NULL; assign temporary value before first insert (IDENTITY id not available yet).
        row.setCaseNo(buildTemporaryCaseNumber());
        row = caseRegistryRepository.save(row);
        caseRegistryRepository.flush();

        row.setCaseNo(buildCaseNumber(row));
        row = caseRegistryRepository.save(row);

        app.setRegisteredCaseId(row.getId());
        app.setApprovedAt(Instant.now());
        app.setApprovedByOfficerLoginId(login);
        app.setPoApproved(true);
        app.setForwardedToPo(false);
        app.setSentBackToClerk(false);
        app.setLastActionByRole("PRESIDING_OFFICER");
        app.setLastActionAt(Instant.now());
        filingApplicationRepository.save(app);

        recordApplicationHistory(
                app,
                ApplicationHistoryAction.CASE_REGISTERED,
                null,
                "PRESIDING_OFFICER",
                login,
                row.getId(),
                row.getCaseNo()
        );

        return buildApprovalResponse(app, row, "Case generated successfully.");
    }

    @Transactional
    public ApplicationActionResponse forwardToPo(Long applicationId, ApplicationActionRequest request, Principal principal) {
        String remarks = requiredText(request != null ? request.getRemarks() : null, "remarks");
        String login = normalizeLogin(principal.getName());
        EmployeePosting posting = resolveOfficerCurrentPosting(login);
        boolean isPo = isPresidingOfficer(posting);
        if (isPo) {
            throw new IllegalArgumentException("Only clerk can forward application to PO.");
        }
        FilingApplication app = resolveOfficerScopedApplication(applicationId, posting.getOffice().getId());
        if (Boolean.TRUE.equals(app.getPoApproved()) || Boolean.TRUE.equals(app.getPoRejected())) {
            throw new IllegalArgumentException("Application already finalized.");
        }
        requireFilingAction(app, posting, WorkflowAction.FORWARD_TO_PO);
        applicationDocumentChecklistService.syncChecklistFromApplication(app);
        applicationDocumentChecklistService.assertClerkVerificationCompleteForForward(app);
        app.setForwardedToPo(true);
        app.setSentBackToClerk(false);
        app.setClerkRemarks(remarks);
        app.setLastActionByRole("CLERK");
        app.setLastActionAt(Instant.now());
        filingApplicationRepository.save(app);
        recordApplicationHistory(app, ApplicationHistoryAction.FORWARDED_TO_PO, remarks, "CLERK", login);
        return buildActionResponse(app, "Application forwarded to PO.");
    }

    @Transactional
    public ApplicationActionResponse returnToClerk(Long applicationId, ApplicationActionRequest request, Principal principal) {
        String remarks = requiredText(request != null ? request.getRemarks() : null, "remarks");
        String login = normalizeLogin(principal.getName());
        EmployeePosting posting = resolveOfficerCurrentPosting(login);
        if (!isPresidingOfficer(posting)) {
            throw new IllegalArgumentException("Only PO can return application to clerk.");
        }
        FilingApplication app = resolveOfficerScopedApplication(applicationId, posting.getOffice().getId());
        if (!Boolean.TRUE.equals(app.getForwardedToPo())) {
            throw new IllegalArgumentException("Application is not in PO review.");
        }
        requireFilingAction(app, posting, WorkflowAction.PO_RETURN_TO_CLERK);
        app.setForwardedToPo(false);
        app.setSentBackToClerk(true);
        app.setPoRemarks(remarks);
        app.setLastActionByRole("PRESIDING_OFFICER");
        app.setLastActionAt(Instant.now());
        filingApplicationRepository.save(app);
        recordApplicationHistory(app, ApplicationHistoryAction.RETURNED_TO_CLERK, remarks, "PRESIDING_OFFICER", login);
        return buildActionResponse(app, "Application returned to clerk.");
    }

    @Transactional
    public ApplicationActionResponse rejectApplication(Long applicationId, ApplicationActionRequest request, Principal principal) {
        String remarks = requiredText(request != null ? request.getRemarks() : null, "remarks");
        String login = normalizeLogin(principal.getName());
        EmployeePosting posting = resolveOfficerCurrentPosting(login);
        if (!isPresidingOfficer(posting)) {
            throw new IllegalArgumentException("Only PO can reject application.");
        }
        FilingApplication app = resolveOfficerScopedApplication(applicationId, posting.getOffice().getId());
        if (!Boolean.TRUE.equals(app.getForwardedToPo())) {
            throw new IllegalArgumentException("Application is not in PO review.");
        }
        requireFilingAction(app, posting, WorkflowAction.PO_REJECT);
        app.setPoRejected(true);
        app.setForwardedToPo(false);
        app.setSentBackToClerk(false);
        app.setPoRemarks(remarks);
        app.setLastActionByRole("PRESIDING_OFFICER");
        app.setLastActionAt(Instant.now());
        filingApplicationRepository.save(app);
        recordApplicationHistory(app, ApplicationHistoryAction.PO_REJECTED, remarks, "PRESIDING_OFFICER", login);
        return buildActionResponse(app, "Application rejected by PO.");
    }

    @Transactional(readOnly = true)
    public ApplicationHistoryListResponse getApplicationHistory(Long applicationId, Principal principal) {
        FilingApplication app = resolveFilerScopedApplication(applicationId, principal);
        return buildApplicationHistoryList(app, false);
    }

    @Transactional(readOnly = true)
    public ApplicationHistoryListResponse getOfficerApplicationHistory(Long applicationId, Principal principal) {
        FilingApplication app = resolveOfficerHistoryScopedApplication(applicationId, principal);
        return buildApplicationHistoryList(app, true);
    }

    @Transactional(readOnly = true)
    public List<OfficerInboxItemResponse> listOfficerInbox(Principal principal) {
        Objects.requireNonNull(principal);
        String login = normalizeLogin(principal.getName());

        EmployeePosting posting = resolveOfficerCurrentPosting(login);
        boolean officerIsPo = isPresidingOfficer(posting);
        Long officeId = resolveOfficerCurrentOfficeId(login);

        List<FilingApplication> applications =
                filingApplicationRepository.findByOfficeIdAndStatusOrderBySubmittedAtDescCreatedAtDesc(
                        officeId,
                        ApplicationStatus.SUBMITTED
                );

        Map<Long, CaseRegistry> caseById = new HashMap<>();
        Set<Long> registeredCaseIds = new HashSet<>();
        for (FilingApplication app : applications) {
            if (app.getRegisteredCaseId() != null) {
                registeredCaseIds.add(app.getRegisteredCaseId());
            }
        }
        if (!registeredCaseIds.isEmpty()) {
            for (CaseRegistry c : caseRegistryRepository.findAllById(registeredCaseIds)) {
                caseById.put(c.getId(), c);
            }
        }

        List<OfficerInboxItemResponse> out = new ArrayList<>();
        for (FilingApplication app : applications) {
            if (!isAssignedToCurrentOfficerRole(app, officerIsPo)) {
                continue;
            }
            OfficerInboxItemResponse row = new OfficerInboxItemResponse();
            row.setApplicationId(app.getId());
            row.setApplicationNo(app.getApplicationNo());
            row.setClientApplicationRef(app.getClientApplicationRef() != null ? app.getClientApplicationRef().toString() : null);
            row.setCaseId(app.getRegisteredCaseId());
            CaseRegistry caseRow = app.getRegisteredCaseId() != null ? caseById.get(app.getRegisteredCaseId()) : null;
            row.setCaseNo(caseRow != null ? caseRow.getCaseNo() : null);
            row.setCaseStatus(caseRow != null ? caseRow.getStatus() : null);
            row.setCaseCategoryId(app.getCaseCategory() != null ? app.getCaseCategory().getId() : null);
            row.setCaseCategoryName(app.getCaseCategory() != null ? app.getCaseCategory().getName() : null);
            row.setSubjectId(app.getSubject() != null ? app.getSubject().getId() : null);
            row.setSubjectName(app.getSubject() != null ? app.getSubject().getSubjectName() : null);
            row.setOfficeId(app.getOffice() != null ? app.getOffice().getId() : null);
            row.setOfficeName(app.getOffice() != null ? app.getOffice().getName() : null);
            row.setStatus(app.getStatus() != null ? app.getStatus().name() : null);
            row.setProcessingStage(deriveProcessingStage(app));
            row.setCurrentAssigneeRole(Boolean.TRUE.equals(app.getForwardedToPo()) ? "PRESIDING_OFFICER" : "CLERK");
            row.setApplicationDescription(app.getApplicationDescription());
            row.setSubmittedAt(app.getSubmittedAt());
            row.setCreatedAt(app.getCreatedAt());

            if (app.getFiledByAdvocate() != null) {
                row.setFiledByName(app.getFiledByAdvocate().getFullName());
                row.setFiledByRole("ADVOCATE");
            } else if (app.getFiledByParty() != null) {
                row.setFiledByName(app.getFiledByParty().getFullName());
                row.setFiledByRole(app.getFiledByParty().getRole() != null ? app.getFiledByParty().getRole().name() : "PARTY_IN_PERSON");
            }

            out.add(row);
        }
        return out;
    }

    @Transactional(readOnly = true)
    public OfficerApplicationDetailResponse getOfficerApplicationDetail(Long applicationId, Principal principal) {
        if (applicationId == null) {
            throw new IllegalArgumentException("applicationId is required.");
        }
        Objects.requireNonNull(principal);
        String login = normalizeLogin(principal.getName());
        EmployeePosting posting = resolveOfficerCurrentPosting(login);
        boolean officerIsPo = isPresidingOfficer(posting);
        Long officeId = resolveOfficerCurrentOfficeId(login);

        FilingApplication app = filingApplicationRepository.findByIdAndOfficeIdAndStatus(
                        applicationId,
                        officeId,
                        ApplicationStatus.SUBMITTED
                )
                .orElseThrow(() -> new IllegalArgumentException("Application not found for officer inbox."));
        if (!isAssignedToCurrentOfficerRole(app, officerIsPo)) {
            throw new IllegalArgumentException("Application is not assigned to current officer role.");
        }

        String caseNo = null;
        if (app.getRegisteredCaseId() != null) {
            CaseRegistry c = caseRegistryRepository.findById(app.getRegisteredCaseId()).orElse(null);
            caseNo = c != null ? c.getCaseNo() : null;
        }
        OfficerApplicationDetailResponse out = toOfficerApplicationDetailResponse(
                app,
                app.getRegisteredCaseId(),
                caseNo,
                app.getStatus() != null ? app.getStatus().name() : null,
                deriveProcessingStage(app),
                Boolean.TRUE.equals(app.getForwardedToPo()) ? "PRESIDING_OFFICER" : "CLERK"
        );
        if (app.getRegisteredCaseId() != null) {
            out.setNotices(caseNoticeRepository.findByCaseRegistryIdOrderByIdDesc(app.getRegisteredCaseId()).stream()
                    .map(FilingApplicationService::toCaseNoticeResponse)
                    .collect(Collectors.toList()));
        } else {
            out.setNotices(Collections.emptyList());
        }
        CaseWorkflowDefinition def = caseWorkflowConfigService.resolveForCategory(app.getCaseCategory());
        out.setBlueprintCode(def.getBlueprintCode());
        out.setAllowedActions(workflowPolicyService.filingAllowedActions(app, posting));
        out.setApplicationHistory(buildApplicationHistoryList(app, true));
        enrichDocumentChecklist(out, app);
        return out;
    }

    @Transactional(readOnly = true)
    public ApplicationDocumentChecklistResponse getOfficerDocumentChecklist(Long applicationId, Principal principal) {
        FilingApplication app = resolveOfficerScopedApplicationForRead(applicationId, principal);
        applicationDocumentChecklistService.syncChecklistFromApplication(app);
        return applicationDocumentChecklistService.getChecklist(app);
    }

    @Transactional
    public ApplicationDocumentChecklistResponse saveOfficerDocumentChecklist(
            Long applicationId,
            ApplicationDocumentChecklistSaveRequest request,
            Principal principal
    ) {
        String login = normalizeLogin(principal.getName());
        EmployeePosting posting = resolveOfficerCurrentPosting(login);
        if (isPresidingOfficer(posting)) {
            throw new IllegalArgumentException("Only clerk can verify application documents.");
        }
        FilingApplication app = resolveOfficerScopedApplication(applicationId, posting.getOffice().getId());
        if (Boolean.TRUE.equals(app.getPoApproved()) || Boolean.TRUE.equals(app.getPoRejected())) {
            throw new IllegalArgumentException("Application already finalized.");
        }
        return applicationDocumentChecklistService.saveClerkVerification(app, request, login);
    }

    @Transactional(readOnly = true)
    public OfficerApplicationDetailResponse getOfficerCaseDetail(Long caseId, Principal principal) {
        if (caseId == null) {
            throw new IllegalArgumentException("caseId is required.");
        }
        Objects.requireNonNull(principal);
        String login = normalizeLogin(principal.getName());
        Long officeId = resolveOfficerCurrentOfficeId(login);

        CaseRegistry caseRow = caseRegistryRepository.findByIdAndOfficeId(caseId, officeId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found for officer office."));

        FilingApplication app = filingApplicationRepository.findById(caseRow.getFilingApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("Filing application not found for case."));

        OfficerApplicationDetailResponse out = toOfficerApplicationDetailResponse(
                app,
                caseRow.getId(),
                caseRow.getCaseNo(),
                caseRow.getStatus(),
                "CASE_PROCEEDINGS",
                "PRESIDING_OFFICER"
        );
        out.setNotices(caseNoticeRepository.findByCaseRegistryIdOrderByIdDesc(caseRow.getId()).stream()
                .map(FilingApplicationService::toCaseNoticeResponse)
                .collect(Collectors.toList()));
        EmployeePosting posting = resolveOfficerCurrentPosting(login);
        CaseWorkflowDefinition def = caseWorkflowConfigService.resolveForCategory(caseRow.getCaseCategory());
        out.setBlueprintCode(def.getBlueprintCode());
        out.setAllowedActions(workflowPolicyService.filingAllowedActions(app, posting));
        out.setApplicationHistory(buildApplicationHistoryList(app, true));
        enrichDocumentChecklist(out, app);
        return out;
    }

    private void enrichDocumentChecklist(OfficerApplicationDetailResponse out, FilingApplication app) {
        if (out == null || app == null || app.getId() == null) {
            return;
        }
        applicationDocumentChecklistService.syncChecklistFromApplication(app);
        out.setDocumentChecklist(applicationDocumentChecklistService.getChecklist(app));
    }

    private FilingApplication resolveOfficerScopedApplicationForRead(Long applicationId, Principal principal) {
        Objects.requireNonNull(principal);
        String login = normalizeLogin(principal.getName());
        EmployeePosting posting = resolveOfficerCurrentPosting(login);
        Long officeId = posting.getOffice() != null ? posting.getOffice().getId() : null;
        if (officeId == null) {
            throw new IllegalArgumentException("Officer current posting office is missing.");
        }
        return resolveOfficerScopedApplication(applicationId, officeId);
    }

    @Transactional(readOnly = true)
    public List<FilerApplicationListItemResponse> listMyApplications(Principal principal) {
        Objects.requireNonNull(principal);
        String login = normalizeLogin(principal.getName());

        Optional<AdvocateRegistration> advocate = advocateRegistrationRepository.findByEmail(login);
        if (advocate.isPresent()) {
            return advocate.get().getId() == null
                    ? Collections.emptyList()
                    : filingApplicationRepository.findByFiledByAdvocate_IdOrderByUpdatedAtDescIdDesc(advocate.get().getId())
                            .stream()
                            .map(this::toFilerApplicationListItem)
                            .collect(Collectors.toList());
        }

        Optional<PartyInPersonRegistration> party = partyInPersonRegistrationRepository.findByEmail(login);
        if (party.isPresent()) {
            return party.get().getId() == null
                    ? Collections.emptyList()
                    : filingApplicationRepository.findByFiledByParty_IdOrderByUpdatedAtDescIdDesc(party.get().getId())
                            .stream()
                            .map(this::toFilerApplicationListItem)
                            .collect(Collectors.toList());
        }

        throw new IllegalArgumentException("Filer profile not found for current login.");
    }

    @Transactional(readOnly = true)
    public List<CaseNoticeResponse> listServedNoticesForApplication(Long applicationId, Principal principal) {
        return listPartyVisibleNoticesForApplication(applicationId, principal);
    }

    @Transactional(readOnly = true)
    public PartyApplicationPreviewResponse getPartyApplicationPreview(Long applicationId, Principal principal) {
        if (applicationId == null) {
            throw new IllegalArgumentException("applicationId is required.");
        }
        Objects.requireNonNull(principal);
        if (isOfficerPrincipal(principal)) {
            return getOfficerApplicationPreview(applicationId, principal);
        }
        String login = normalizeLogin(principal.getName());
        FilingApplication app = filingApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));
        assertPartyOwnership(app, login);
        return buildApplicationPreview(app, false);
    }

    @Transactional(readOnly = true)
    public PartyApplicationPreviewResponse getOfficerApplicationPreview(Long applicationId, Principal principal) {
        FilingApplication app = resolveOfficerHistoryScopedApplication(applicationId, principal);
        return buildApplicationPreview(app, true);
    }

    private PartyApplicationPreviewResponse buildApplicationPreview(FilingApplication app, boolean officerView) {
        Long caseId = app.getRegisteredCaseId();
        String caseNo = null;
        String caseStatus = null;
        if (caseId != null) {
            CaseRegistry caseRow = caseRegistryRepository.findById(caseId).orElse(null);
            if (caseRow != null) {
                caseNo = caseRow.getCaseNo();
                caseStatus = caseRow.getStatus();
            }
        }

        OfficerApplicationDetailResponse application = toOfficerApplicationDetailResponse(
                app,
                caseId,
                caseNo,
                caseStatus != null ? caseStatus : (app.getStatus() != null ? app.getStatus().name() : null),
                caseId != null ? "CASE_PROCEEDINGS" : deriveProcessingStage(app),
                caseId != null ? null : currentAssigneeRole(app)
        );
        application.setNotices(Collections.emptyList());

        PartyApplicationPreviewResponse out = new PartyApplicationPreviewResponse();
        out.setApplication(application);

        if (caseId != null) {
            if (officerView) {
                out.setNotices(caseNoticeRepository.findByCaseRegistryIdOrderByIdDesc(caseId).stream()
                        .map(FilingApplicationService::toCaseNoticeResponse)
                        .collect(Collectors.toList()));
            } else {
                out.setNotices(loadPartyVisibleNotices(caseId));
            }
            out.setHearings(loadCaseHearingsForParty(caseId, caseNo));
            out.setOrderSheetHistory(loadOrderSheetHistoryForParty(caseId));
            caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).ifPresent(judgment -> {
                if (judgment.getStatus() == CaseJudgmentWorkflowStatus.PUBLISHED) {
                    out.setJudgmentWorkflowStatus(judgment.getStatus().name());
                    out.setJudgmentSummary(trimToNull(judgment.getPublishedSummary()));
                }
            });
        } else {
            out.setNotices(Collections.emptyList());
            out.setHearings(Collections.emptyList());
            out.setOrderSheetHistory(Collections.emptyList());
        }

        out.setApplicationHistory(buildApplicationHistoryList(app, officerView));
        enrichDocumentChecklist(application, app);
        return out;
    }

    @Transactional(readOnly = true)
    public List<CaseNoticeResponse> listPartyVisibleNoticesForApplication(Long applicationId, Principal principal) {
        if (applicationId == null) {
            throw new IllegalArgumentException("applicationId is required.");
        }
        Objects.requireNonNull(principal);
        String login = normalizeLogin(principal.getName());

        FilingApplication app = filingApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));
        assertPartyOwnership(app, login);

        if (app.getRegisteredCaseId() == null) {
            return Collections.emptyList();
        }
        return loadPartyVisibleNotices(app.getRegisteredCaseId());
    }

    private List<CaseNoticeResponse> loadPartyVisibleNotices(Long caseId) {
        List<CaseNoticeResponse> out = new ArrayList<>();
        for (CaseNotice row : caseNoticeRepository.findByCaseRegistryIdOrderByIdDesc(caseId)) {
            if (!isPartyVisibleNoticeStatus(row.getStatus())) {
                continue;
            }
            out.add(toPartyCaseNoticeResponse(row));
        }
        return out;
    }

    private static boolean isPartyVisibleNoticeStatus(CaseNoticeStatus status) {
        return status == CaseNoticeStatus.PO_FINALIZED
                || status == CaseNoticeStatus.PO_SIGNED
                || status == CaseNoticeStatus.SERVED;
    }

    private List<CaseHearingResponse> loadCaseHearingsForParty(Long caseId, String caseNo) {
        List<CaseHearingResponse> out = new ArrayList<>();
        for (CaseHearing row : caseHearingRepository.findByCaseRegistryIdOrderByHearingNoAsc(caseId)) {
            CaseHearingResponse dto = new CaseHearingResponse();
            dto.setHearingId(row.getId());
            dto.setCaseId(caseId);
            dto.setCaseNo(caseNo);
            dto.setHearingNo(row.getHearingNo());
            dto.setHearingDate(row.getHearingDate());
            dto.setStatus(row.getStatus());
            dto.setNoticeGenerated(row.getNoticeGenerated());
            boolean noticeServed = Boolean.TRUE.equals(row.getNoticeServed());
            dto.setNoticeServed(noticeServed);
            dto.setProceedingAllowed(noticeServed);
            dto.setRemarks(row.getRemarks());
            dto.setCreatedAt(row.getCreatedAt());
            dto.setUpdatedAt(row.getUpdatedAt());
            out.add(dto);
        }
        return out;
    }

    private List<CaseOrderSheetHistoryResponse> loadOrderSheetHistoryForParty(Long caseId) {
        List<CaseOrderSheetHistoryResponse> out = new ArrayList<>();
        for (CaseOrderSheetHistory row : caseOrderSheetHistoryRepository.findByCaseRegistryIdOrderByCreatedAtDesc(caseId)) {
            CaseOrderSheetHistoryResponse dto = new CaseOrderSheetHistoryResponse();
            dto.setHistoryId(row.getId());
            if (row.getCaseHearing() != null) {
                dto.setHearingId(row.getCaseHearing().getId());
                dto.setHearingNo(row.getCaseHearing().getHearingNo());
                dto.setHearingDate(row.getCaseHearing().getHearingDate());
            }
            dto.setContent(row.getContent());
            dto.setRemarks(row.getRemarks());
            dto.setCreatedAt(row.getCreatedAt());
            dto.setCreatedByLoginId(row.getCreatedByLoginId());
            out.add(dto);
        }
        return out;
    }

    private ApplicationDisputedOrderPayload mergeDisputedEnvelope(ApplicationSavePayload p) {
        ApplicationDisputedOrderPayload o = p.getDisputedOrder();
        if (o == null) {
            o = new ApplicationDisputedOrderPayload();
        }
        if (o.getMutationFound() == null && p.getMutationFound() != null) {
            o.setMutationFound(p.getMutationFound());
        }
        if (o.getMutationSearched() == null && p.getSearchedMutation() != null) {
            o.setMutationSearched(p.getSearchedMutation());
        }
        if (o.getMutationDetails() == null && p.getMutationDetails() != null) {
            o.setMutationDetails(p.getMutationDetails());
        }
        if (o.getNotice9() == null && p.getNotice9Resolved() != null) {
            o.setNotice9(p.getNotice9Resolved());
        }
        return o;
    }

    private static List<VakalatnamaGroupPayload> combineVakalatnama(
            List<VakalatnamaGroupPayload> root,
            List<VakalatnamaGroupPayload> nested
    ) {
        boolean hasNested = nested != null && !nested.isEmpty();
        boolean hasRoot = root != null && !root.isEmpty();
        if (hasNested) {
            return nested;
        }
        if (hasRoot) {
            return root;
        }
        return Collections.emptyList();
    }

    /**
     * Wipes nested rows before replace-on-save. DB deletes are required because
     * {@code JpaRepository.save()} merges existing aggregates and orphanRemoval on {@code clear()}
     * does not always delete old rows before new inserts (duplicate {@code client_row_key}).
     */
    private FilingApplication replaceApplicationChildren(FilingApplication entity) {
        Long applicationId = entity.getId();
        if (applicationId != null) {
            filingApplicationChildCleanup.deleteAllChildren(applicationId);
            entity = filingApplicationChildCleanup.reloadApplication(applicationId);
            if (entity == null) {
                throw new IllegalArgumentException("Application not found.");
            }
        }
        entity.setDisputedOrder(null);
        entity.getVakalatnamaGroups().clear();
        entity.getApplicants().clear();
        entity.getRespondents().clear();
        entity.getDisputedLands().clear();
        entity.getAttachments().clear();
        entity.getDescriptionParagraphs().clear();
        return entity;
    }

    private static boolean hasEvolvedSaveSections(ApplicationSavePayload payload) {
        if (payload.getHeader() != null || payload.getDescription() != null || payload.getDisputedOrder() != null) {
            return true;
        }
        if (payload.getApplicants() != null && !payload.getApplicants().isEmpty()) {
            return true;
        }
        if (payload.getRespondents() != null && !payload.getRespondents().isEmpty()) {
            return true;
        }
        if (payload.getDisputedLands() != null && !payload.getDisputedLands().isEmpty()) {
            return true;
        }
        if (payload.getAttachments() != null && !payload.getAttachments().isEmpty()) {
            return true;
        }
        if (payload.getVakalatnamaAssignments() != null && !payload.getVakalatnamaAssignments().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Resolves an existing filing row or starts a new aggregate. Stale {@code applicationId} values
     * (e.g. after DB truncate) fall back to {@code clientApplicationRef}, then a new draft.
     */
    private FilingApplication locateOrCreate(Long applicationId, UUID clientRef) {
        if (applicationId != null) {
            Optional<FilingApplication> byId = filingApplicationRepository.findById(applicationId);
            if (byId.isPresent()) {
                return byId.get();
            }
        }
        if (clientRef != null) {
            Optional<FilingApplication> byRef = filingApplicationRepository.findByClientApplicationRef(clientRef);
            if (byRef.isPresent()) {
                return byRef.get();
            }
        }
        return new FilingApplication();
    }

    private static void attachFiler(
            FilingApplication entity,
            AdvocateRegistration advocateFiler,
            PartyInPersonRegistration partyFiler
    ) {
        entity.setFiledByAdvocate(null);
        entity.setFiledByParty(null);
        if (advocateFiler != null) {
            entity.setFiledByAdvocate(advocateFiler);
        }
        if (partyFiler != null) {
            entity.setFiledByParty(partyFiler);
        }
    }

    private void assertOwnership(
            FilingApplication entity,
            AdvocateRegistration advocateFiler,
            PartyInPersonRegistration partyFiler
    ) {
        if (advocateFiler != null
                && entity.getFiledByAdvocate() != null
                && Objects.equals(entity.getFiledByAdvocate().getId(), advocateFiler.getId())) {
            return;
        }
        if (partyFiler != null
                && entity.getFiledByParty() != null
                && Objects.equals(entity.getFiledByParty().getId(), partyFiler.getId())) {
            return;
        }
        throw new IllegalArgumentException("Not allowed to modify this application.");
    }

    private FilerApplicationListItemResponse toFilerApplicationListItem(FilingApplication app) {
        FilerApplicationListItemResponse row = new FilerApplicationListItemResponse();
        row.setApplicationId(app.getId());
        row.setApplicationNo(app.getApplicationNo());
        row.setCaseId(app.getRegisteredCaseId());
        if (app.getRegisteredCaseId() != null) {
            CaseRegistry caseRow = caseRegistryRepository.findById(app.getRegisteredCaseId()).orElse(null);
            if (caseRow != null) {
                row.setCaseNo(caseRow.getCaseNo());
                row.setCaseStatus(caseRow.getStatus());
            }
        }
        row.setCaseCategoryId(app.getCaseCategory() != null ? app.getCaseCategory().getId() : null);
        row.setCaseCategoryName(app.getCaseCategory() != null ? app.getCaseCategory().getName() : null);
        row.setSubjectId(app.getSubject() != null ? app.getSubject().getId() : null);
        row.setSubjectName(app.getSubject() != null ? app.getSubject().getSubjectName() : null);
        row.setOfficeId(app.getOffice() != null ? app.getOffice().getId() : null);
        row.setOfficeName(app.getOffice() != null ? app.getOffice().getName() : null);
        row.setStatus(app.getStatus() != null ? app.getStatus().name() : null);
        row.setApplicationDescription(app.getApplicationDescription());
        row.setSubmittedAt(app.getSubmittedAt());
        row.setCreatedAt(app.getCreatedAt());
        row.setUpdatedAt(app.getUpdatedAt());
        if (app.getFiledByAdvocate() != null) {
            row.setFiledByRole("ADVOCATE");
        } else if (app.getFiledByParty() != null) {
            row.setFiledByRole(app.getFiledByParty().getRole() != null ? app.getFiledByParty().getRole().name() : "PARTY_IN_PERSON");
        }
        return row;
    }

    private void assertPartyOwnership(FilingApplication entity, String login) {
        if (entity.getFiledByAdvocate() != null && hasText(entity.getFiledByAdvocate().getEmail())) {
            if (normalizeLogin(entity.getFiledByAdvocate().getEmail()).equals(login)) {
                return;
            }
        }
        if (entity.getFiledByParty() != null && hasText(entity.getFiledByParty().getEmail())) {
            if (normalizeLogin(entity.getFiledByParty().getEmail()).equals(login)) {
                return;
            }
        }
        throw new IllegalArgumentException("Not allowed to view this application.");
    }

    private void applyFormHeader(FilingApplication entity, ApplicationFormNestedPayload form) {
        applyFilingHeader(entity, toFilingHeaderPayload(form));
    }

    private void applyFilingHeader(FilingApplication entity, ApplicationFilingHeaderPayload header) {
        if (header == null) {
            return;
        }
        Subject subject = resolveSubject(header.getSubjectId());
        entity.setSubject(subject);
        String description = trimToNull(header.getApplicationDescription());
        if (description != null) {
            entity.setApplicationDescription(description);
        }

        entity.setDistrict(resolveDistrict(header.getDistrictId()));
        entity.setSubdistrict(resolveSubdistrict(header.getSubdistrictId()));
        entity.setTaluka(resolveTaluka(header.getTalukaId()));
        Office office = resolveOffice(header.getOfficeId(), header.getOfficeCode());
        entity.setOffice(office);
        Act act = resolveAct(header.getActId(), header.getActCode());
        entity.setAct(act);

        normalizeSection(header, entity, act);

        entity.setMutationYear(header.getMutationYear());
        entity.setMutationTypeFilter(trimToNull(header.getMutationTypeFilter()));
    }

    private static ApplicationFilingHeaderPayload toFilingHeaderPayload(ApplicationFormNestedPayload form) {
        ApplicationFilingHeaderPayload header = new ApplicationFilingHeaderPayload();
        header.setSubjectId(form.getSubjectId());
        header.setApplicationDescription(form.getApplicationDescription());
        header.setDistrictId(form.getDistrictId());
        header.setSubdistrictId(form.getSubdistrictId());
        header.setTalukaId(form.getTalukaId());
        header.setOfficeId(form.getOfficeId());
        header.setOfficeCode(form.getOfficeCode());
        header.setActId(form.getActId());
        header.setActCode(form.getActCode());
        header.setSectionId(form.getSectionId());
        header.setSectionCode(form.getSectionCode());
        header.setSectionCustomText(form.getSectionCustomText());
        header.setMutationYear(form.getMutationYear());
        header.setMutationTypeFilter(form.getMutationTypeFilter());
        return header;
    }

    private void applyDescription(FilingApplication entity, ApplicationDescriptionPayload description) {
        if (description == null) {
            return;
        }
        entity.setAffidavitText(trimToNull(description.getAffidavitText()));
        entity.setPrayerText(trimToNull(description.getPrayerText()));

        List<String> paragraphs = description.getParagraphs();
        if (paragraphs == null || paragraphs.isEmpty()) {
            return;
        }
        int paraNo = 1;
        StringBuilder joined = new StringBuilder();
        for (String raw : paragraphs) {
            String text = trimToNull(raw);
            if (text == null) {
                continue;
            }
            ApplicationDescriptionParagraph row = new ApplicationDescriptionParagraph();
            row.setApplication(entity);
            row.setParaNo(paraNo++);
            row.setText(text);
            entity.getDescriptionParagraphs().add(row);
            if (joined.length() > 0) {
                joined.append("\n\n");
            }
            joined.append(text);
        }
        if (joined.length() > 0) {
            entity.setApplicationDescription(joined.toString());
        }
    }

    private void storeFormSnapshot(FilingApplication entity, ApplicationSavePayload payload) {
        Object snapshot = payload.getFormSnapshot() != null ? payload.getFormSnapshot() : payload;
        entity.setFormSnapshotJson(filingJsonCodec.toJson(snapshot));
    }

    private Subject resolveSubject(Long id) {
        if (id == null || zeroToNull(id) == null) {
            return null;
        }
        return subjectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid subjectId."));
    }

    private District resolveDistrict(Long id) {
        if (id == null || zeroToNull(id) == null) {
            return null;
        }
        return districtRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid districtId."));
    }

    private Subdistrict resolveSubdistrict(Long id) {
        if (id == null || zeroToNull(id) == null) {
            return null;
        }
        return subdistrictRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid subdistrictId."));
    }

    private Taluka resolveTaluka(Long id) {
        if (id == null || zeroToNull(id) == null) {
            return null;
        }
        return talukaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid talukaId."));
    }

    private Office resolveOffice(Long id, String officeCode) {
        Long officeId = zeroToNull(id);
        if (officeId != null) {
            return officeRepository.findById(officeId).orElseThrow(() -> new IllegalArgumentException("Invalid officeId."));
        }
        String code = trimToNull(officeCode);
        if (code != null) {
            return officeRepository.findFirstByOfficeCodeIgnoreCase(code)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid officeCode."));
        }
        return null;
    }

    private Act resolveAct(Long id, String actCode) {
        Long actId = zeroToNull(id);
        if (actId != null) {
            return actRepository.findById(actId).orElseThrow(() -> new IllegalArgumentException("Invalid actId."));
        }
        String code = trimToNull(actCode);
        if (code != null) {
            return actRepository.findFirstByActCodeIgnoreCase(code)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid actCode."));
        }
        return null;
    }

    private void normalizeSection(ApplicationFilingHeaderPayload form, FilingApplication entity, Act resolvedAct) {
        String sectionCustomText = trimToNull(form.getSectionCustomText());
        if (hasText(sectionCustomText)) {
            entity.setSection(null);
            entity.setSectionCustomText(sectionCustomText);
            return;
        }
        Long sectionId = zeroToNull(form.getSectionId());
        if (sectionId != null) {
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid sectionId."));
            entity.setSection(section);
            entity.setSectionCustomText(null);
            return;
        }
        String sectionCode = trimToNull(form.getSectionCode());
        if (sectionCode != null) {
            Section section = (resolvedAct != null && resolvedAct.getId() != null)
                    ? sectionRepository.findFirstByActIdAndSectionCodeIgnoreCase(resolvedAct.getId(), sectionCode)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid sectionCode for selected act."))
                    : sectionRepository.findFirstBySectionCodeIgnoreCase(sectionCode)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid sectionCode."));
            entity.setSection(section);
            entity.setSectionCustomText(null);
            return;
        }
        entity.setSection(null);
        entity.setSectionCustomText(null);
    }

    private static void validateSubmission(FilingApplication entity) {
        if (entity.getSubject() == null) {
            throw new IllegalArgumentException("subjectId is required for submission.");
        }
        if (entity.getOffice() == null) {
            throw new IllegalArgumentException("officeId or officeCode is required for submission.");
        }
        if (entity.getAct() == null) {
            throw new IllegalArgumentException("actId or actCode is required for submission.");
        }
        boolean hasMasterSection = entity.getSection() != null;
        boolean hasCustomSection = hasText(trimToNull(entity.getSectionCustomText()));
        if (!hasMasterSection && !hasCustomSection) {
            throw new IllegalArgumentException("Either sectionId or sectionCustomText is required for submission.");
        }
        if (entity.getApplicants().isEmpty()) {
            throw new IllegalArgumentException("At least one applicant is required for submission.");
        }

        ApplicationDisputedOrder disputed = entity.getDisputedOrder();
        if (disputed == null) {
            throw new IllegalArgumentException("Disputed mutation / order block is required for submission.");
        }
        boolean hasManual = hasText(trimToNull(disputed.getManualInwardNumber()));
        boolean hasInward = hasText(trimToNull(disputed.getInwardNumber()))
                || hasText(trimToNull(disputed.getResolvedInwardNumber()))
                || ((disputed.getSearchMode() != null && disputed.getSearchMode().name().equalsIgnoreCase("INWARD_NUMBER"))
                    && hasText(trimToNull(disputed.getSearchValue())));
        boolean hasSearchSignal = disputed.getMutationSearched() != null || disputed.getMutationFound() != null
                || disputed.getSearchMode() != null || hasText(trimToNull(disputed.getSearchCriteriaCode()))
                || hasText(trimToNull(disputed.getSearchValue()))
                || hasText(trimToNull(disputed.getCriteriaValuesJson()));
        if (!(hasManual || hasInward || hasSearchSignal)) {
            throw new IllegalArgumentException("Disputed mutation / order details incomplete for submission.");
        }
        if (!hasText(trimToNull(entity.getApplicationDescription()))) {
            throw new IllegalArgumentException("applicationDescription is required for submission.");
        }
    }

    private void applyApplicants(
            FilingApplication entity,
            List<ApplicantRowPayload> payloads,
            boolean submit
    ) {
        if (payloads == null) {
            return;
        }
        int autoLine = 1;
        for (ApplicantRowPayload dto : payloads) {
            String key = applicantClientKey(dto);
            if (submit && key == null) {
                throw new IllegalArgumentException("Each applicant must have tempId/clientRowKey for submission.");
            }
            ApplicationApplicant row = new ApplicationApplicant();
            row.setApplication(entity);
            row.setLineNo(dto.getLineNo() != null ? dto.getLineNo() : autoLine++);
            row.setClientRowKey(key != null ? key : UUID.randomUUID().toString());
            row.setFirstName(trimToNull(dto.getFirstName()));
            row.setMiddleName(trimToNull(dto.getMiddleName()));
            row.setLastName(trimToNull(dto.getLastName()));
            row.setPincode(trimToNull(dto.getPincode()));
            row.setDistrict(trimToNull(dto.getDistrict()));
            row.setTaluka(trimToNull(dto.getTaluka()));
            row.setVillage(trimToNull(dto.getVillage()));
            row.setVillageValue(trimToNull(dto.getVillageValue()));
            row.setEmail(trimToNull(dto.getEmail()));
            row.setMobile(trimToNull(dto.getMobile()));
            row.setDob(trimToNull(dto.getDob()));
            row.setAge(trimToNull(dto.getAge()));
            row.setOccupation(trimToNull(dto.getOccupation()));
            row.setAddress(trimToNull(dto.getAddress()));
            row.setName(buildCompatibleFullName(
                    row.getFirstName(),
                    row.getMiddleName(),
                    row.getLastName(),
                    trimToNull(dto.getName())
            ));
            if (submit) {
                validatePartyRow(
                        "Applicant",
                        row.getFirstName(),
                        row.getLastName(),
                        row.getPincode(),
                        row.getDistrict(),
                        row.getTaluka(),
                        row.getVillage(),
                        row.getAddress(),
                        row.getEmail(),
                        row.getMobile(),
                        row.getDob()
                );
            }
            entity.getApplicants().add(row);
        }
    }

    private static String applicantClientKey(ApplicantRowPayload r) {
        if (hasText(r.getClientRowKey())) {
            return r.getClientRowKey().trim();
        }
        if (hasText(r.getTempId())) {
            return r.getTempId().trim();
        }
        return null;
    }

    private void applyRespondents(
            FilingApplication entity,
            List<RespondentRowPayload> payloads,
            boolean submit
    ) {
        if (payloads == null) {
            return;
        }
        int autoLine = 1;
        for (RespondentRowPayload dto : payloads) {
            ApplicationRespondent row = new ApplicationRespondent();
            row.setApplication(entity);
            row.setLineNo(dto.getLineNo() != null ? dto.getLineNo() : autoLine++);
            String key = respondentClientKey(dto);
            row.setClientRowKey(key != null ? key : UUID.randomUUID().toString());
            row.setFirstName(trimToNull(dto.getFirstName()));
            row.setMiddleName(trimToNull(dto.getMiddleName()));
            row.setLastName(trimToNull(dto.getLastName()));
            row.setPincode(trimToNull(dto.getPincode()));
            row.setDistrict(trimToNull(dto.getDistrict()));
            row.setTaluka(trimToNull(dto.getTaluka()));
            row.setVillage(trimToNull(dto.getVillage()));
            row.setVillageValue(trimToNull(dto.getVillageValue()));
            row.setEmail(trimToNull(dto.getEmail()));
            row.setMobile(trimToNull(dto.getMobile()));
            row.setDob(trimToNull(dto.getDob()));
            row.setAge(trimToNull(dto.getAge()));
            row.setOccupation(trimToNull(dto.getOccupation()));
            row.setAddress(trimToNull(dto.getAddress()));
            row.setName(buildCompatibleFullName(
                    row.getFirstName(),
                    row.getMiddleName(),
                    row.getLastName(),
                    trimToNull(dto.getName())
            ));
            if (submit) {
                validatePartyRow(
                        "Respondent",
                        row.getFirstName(),
                        row.getLastName(),
                        row.getPincode(),
                        row.getDistrict(),
                        row.getTaluka(),
                        row.getVillage(),
                        row.getAddress(),
                        row.getEmail(),
                        row.getMobile(),
                        row.getDob()
                );
            }
            entity.getRespondents().add(row);
        }
    }

    private static String respondentClientKey(RespondentRowPayload r) {
        if (hasText(r.getClientRowKey())) {
            return r.getClientRowKey().trim();
        }
        if (hasText(r.getTempId())) {
            return r.getTempId().trim();
        }
        return null;
    }

    private void applyDisputedOrder(FilingApplication app, ApplicationDisputedOrderPayload p) {
        if (p == null || isBlankDisputedPayload(p)) {
            return;
        }
        ApplicationDisputedOrder ord = new ApplicationDisputedOrder();
        ord.setApplication(app);
        app.setDisputedOrder(ord);

        ord.setLandChannel(trimToNull(p.getLandChannel()));
        ord.setSearchCriteriaCode(trimToNull(p.getSearchCriteriaCode()));
        ord.setSearchDisplayText(trimToNull(p.getSearchDisplayText()));
        ord.setResolvedInwardNumber(trimToNull(p.getResolvedInwardNumber()));
        if (p.getLocation() != null) {
            ord.setLocationJson(filingJsonCodec.toJson(p.getLocation()));
        }
        if (p.getCriteriaValues() != null && !p.getCriteriaValues().isEmpty()) {
            ord.setCriteriaValuesJson(filingJsonCodec.toJson(p.getCriteriaValues()));
        }
        if (p.getMutationSnapshot() != null) {
            ord.setMutationSnapshotJson(filingJsonCodec.toJson(p.getMutationSnapshot()));
        }
        if (p.getExternalRefs() != null && !p.getExternalRefs().isEmpty()) {
            ord.setExternalRefsJson(filingJsonCodec.toJson(p.getExternalRefs()));
        }

        DisputedOrderSearchMode searchMode = resolveSearchMode(p);
        ord.setSearchMode(searchMode);
        String searchValue = trimToNull(p.getSearchValue());
        if (searchValue == null && p.getCriteriaValues() != null) {
            Object inward = p.getCriteriaValues().get("inwardNumber");
            if (inward == null) {
                inward = p.getCriteriaValues().get("inwardNo");
            }
            if (inward != null) {
                searchValue = String.valueOf(inward);
            }
        }
        ord.setSearchValue(searchValue);
        ord.setMutationFound(p.getMutationFound());
        ord.setMutationSearched(p.getMutationSearched());

        MutationDetailsPayload md = p.getMutationDetails();
        if (md != null) {
            ord.setInwardNumber(trimToNull(md.getInwardNumber()));
            ord.setInwardDate(md.getInwardDate());
            ord.setMutationType(trimToNull(md.getMutationType()));
            ord.setApplicantName(trimToNull(md.getApplicantName()));
            ord.setVillage(trimToNull(md.getVillage()));
            ord.setOrderStatus(trimToNull(md.getStatus()));
            ord.setAttachFileUrl(trimToNull(md.getAttachFileUrl()));
            // Ignore inline/base64 notice-9 URL payloads; persist only search criteria and lightweight metadata.
            ord.setNotice9UrlResolved(null);
        }

        ord.setManualInwardNumber(trimToNull(p.getManualInwardNumber()));
        ord.setManualInwardDate(p.getManualInwardDate());
        ord.setManualMutationType(trimToNull(p.getManualMutationType()));
        ord.setManualApplicantName(trimToNull(p.getManualApplicantName()));
        ord.setManualVillage(trimToNull(p.getManualVillage()));
        ord.setManualStatus(trimToNull(p.getManualStatus()));

        Notice9ResolvedPayload n = p.getNotice9();
        if (n != null) {
            ord.setNotice9Available(n.getAvailable());
            ord.setNotice9SourceKind(parseEnumQuiet(Notice9SourceKind.class, n.getSourceKind()));
            ord.setNotice9Url(null);
            ord.setNotice9PreviewKind(trimToNull(n.getPreviewKind()));
            ord.setNotice9Json(filingJsonCodec.toJson(n));
        }

        String resolvedInward = trimToNull(p.getResolvedInwardNumber());
        if (resolvedInward != null && !hasText(trimToNull(ord.getInwardNumber()))) {
            ord.setInwardNumber(resolvedInward);
        }
    }

    private static DisputedOrderSearchMode resolveSearchMode(ApplicationDisputedOrderPayload p) {
        DisputedOrderSearchMode mode = parseEnumQuiet(DisputedOrderSearchMode.class, p.getSearchMode());
        if (mode != null) {
            return mode;
        }
        return parseEnumQuiet(DisputedOrderSearchMode.class, p.getSearchCriteriaCode());
    }

    /**
     * @return true when the whole disputed-order block should be omitted
     */
    private static boolean isBlankDisputedPayload(ApplicationDisputedOrderPayload p) {
        if (trimToNull(p.getLandChannel()) != null || trimToNull(p.getSearchCriteriaCode()) != null
                || trimToNull(p.getSearchDisplayText()) != null || trimToNull(p.getResolvedInwardNumber()) != null) {
            return false;
        }
        if (p.getLocation() != null || (p.getCriteriaValues() != null && !p.getCriteriaValues().isEmpty())
                || p.getMutationSnapshot() != null
                || (p.getExternalRefs() != null && !p.getExternalRefs().isEmpty())) {
            return false;
        }
        if (trimToNull(p.getSearchMode()) != null || trimToNull(p.getSearchValue()) != null) {
            return false;
        }
        if (p.getMutationFound() != null || p.getMutationSearched() != null) {
            return false;
        }
        MutationDetailsPayload md = p.getMutationDetails();
        if (md != null && (trimToNull(md.getInwardNumber()) != null
                || md.getInwardDate() != null
                || trimToNull(md.getMutationType()) != null
                || trimToNull(md.getApplicantName()) != null
                || trimToNull(md.getVillage()) != null
                || trimToNull(md.getStatus()) != null
                || trimToNull(md.getAttachFileUrl()) != null)) {
            return false;
        }
        if (trimToNull(p.getManualInwardNumber()) != null || p.getManualInwardDate() != null
                || trimToNull(p.getManualMutationType()) != null || trimToNull(p.getManualApplicantName()) != null
                || trimToNull(p.getManualVillage()) != null || trimToNull(p.getManualStatus()) != null) {
            return false;
        }
        Notice9ResolvedPayload notice9 = p.getNotice9();
        if (notice9 != null) {
            if (notice9.getAvailable() != null || hasText(notice9.getSourceKind()) || hasText(notice9.getPreviewKind())) {
                return false;
            }
        }
        return true;
    }

    private void applyVakalatnamaGroups(FilingApplication app, List<VakalatnamaGroupPayload> groups, String filingLoginId) {
        if (groups == null || groups.isEmpty()) {
            return;
        }
        Map<String, ApplicationApplicant> byKey = new HashMap<>();
        for (ApplicationApplicant applicant : app.getApplicants()) {
            byKey.putIfAbsent(applicant.getClientRowKey(), applicant);
        }

        int gno = 1;
        for (VakalatnamaGroupPayload dto : groups) {
            ApplicationVakalatnamaGroup grp = new ApplicationVakalatnamaGroup();
            grp.setApplication(app);
            grp.setGroupNo(dto.getGroupNo() != null ? dto.getGroupNo() : gno++);

            Long primaryRegistrationId = firstNonNull(dto.getPrimaryAdvocateId(),
                    dto.getAdvocate() != null ? dto.getAdvocate().getId() : null);
            if (primaryRegistrationId != null) {
                AdvocateRegistration adv = advocateRegistrationRepository.findById(primaryRegistrationId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid primaryAdvocateId."));
                grp.setPrimaryAdvocateRegistration(adv);
            }

            AdvocateSnapshotPayload snapPrimary = dto.getAdvocate();
            if (snapPrimary != null) {
                fillPrimarySnapshotIfMissing(grp, snapPrimary);
            }

            if (dto.getApplicantClientRowKeys() != null) {
                for (String rawKey : dto.getApplicantClientRowKeys()) {
                    if (!hasText(rawKey)) {
                        continue;
                    }
                    String kk = rawKey.trim();
                    ApplicationApplicant apl = byKey.get(kk);
                    if (apl == null) {
                        throw new IllegalArgumentException("Unknown applicant tempId/clientRowKey in vakalatnama group: " + kk);
                    }
                    ApplicationVakalatnamaGroupApplicant link = new ApplicationVakalatnamaGroupApplicant();
                    link.setGroup(grp);
                    link.setApplicationApplicant(apl);
                    grp.getApplicantLinks().add(link);
                }
            }

            if (dto.getCoAdvocates() != null) {
                for (VakCoAdvocatePayload co : dto.getCoAdvocates()) {
                    ApplicationVakalatnamaCoAdvocate row = new ApplicationVakalatnamaCoAdvocate();
                    row.setGroup(grp);
                    Long coId = co.getAdvocateId() != null
                            ? co.getAdvocateId()
                            : co.getAdvocate() != null ? co.getAdvocate().getId() : null;
                    if (coId != null) {
                        AdvocateRegistration cra = advocateRegistrationRepository.findById(coId)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid co-advocate id."));
                        row.setAdvocateRegistration(cra);
                    }
                    if (co.getAdvocate() != null) {
                        fillCoSnapshot(row, co.getAdvocate());
                    }
                    grp.getCoAdvocates().add(row);
                }
            }

            app.getVakalatnamaGroups().add(grp);
        }

        Objects.requireNonNull(filingLoginId);
    }

    private static void fillPrimarySnapshotIfMissing(ApplicationVakalatnamaGroup grp, AdvocateSnapshotPayload s) {
        if (grp.getSnapshotFullName() == null && hasText(s.getFullName())) {
            grp.setSnapshotFullName(trimToNull(s.getFullName()));
        }
        if (grp.getSnapshotEmail() == null && hasText(s.getEmail())) {
            grp.setSnapshotEmail(trimToNull(s.getEmail()));
        }
        if (grp.getSnapshotMobile() == null && hasText(s.getMobileNumber())) {
            grp.setSnapshotMobile(trimToNull(s.getMobileNumber()));
        }
        if (grp.getSnapshotAddress() == null && hasText(s.getAddress())) {
            grp.setSnapshotAddress(trimToNull(s.getAddress()));
        }
        if (grp.getSnapshotBarCouncilNumber() == null && hasText(s.getBarCouncilNumber())) {
            grp.setSnapshotBarCouncilNumber(trimToNull(s.getBarCouncilNumber()));
        }
        if (grp.getSnapshotEnrollmentNumber() == null && hasText(s.getEnrollmentNumber())) {
            grp.setSnapshotEnrollmentNumber(trimToNull(s.getEnrollmentNumber()));
        }
        if (grp.getSnapshotLawFirmName() == null && hasText(s.getLawFirmName())) {
            grp.setSnapshotLawFirmName(trimToNull(s.getLawFirmName()));
        }
    }

    private static void fillCoSnapshot(ApplicationVakalatnamaCoAdvocate row, AdvocateSnapshotPayload s) {
        row.setSnapshotFullName(trimToNull(s.getFullName()));
        row.setSnapshotEmail(trimToNull(s.getEmail()));
        row.setSnapshotMobile(trimToNull(s.getMobileNumber()));
        row.setSnapshotAddress(trimToNull(s.getAddress()));
        row.setSnapshotBarCouncilNumber(trimToNull(s.getBarCouncilNumber()));
        row.setSnapshotEnrollmentNumber(trimToNull(s.getEnrollmentNumber()));
        row.setSnapshotLawFirmName(trimToNull(s.getLawFirmName()));
    }

    private void applyDisputedLands(FilingApplication app, List<DisputedLandPayload> lands) {
        if (lands == null || lands.isEmpty()) {
            return;
        }
        int line = 1;
        for (DisputedLandPayload lp : lands) {
            ApplicationDisputedLand row = new ApplicationDisputedLand();
            row.setApplication(app);
            String landTypeRaw = normalizeLandType(lp.getLandType());
            row.setLandType(requiredEnum(DisputedLandType.class, landTypeRaw, "land type"));
            row.setExternalSource(parseEnumQuiet(LandRecordsExternalSource.class, lp.getExternalSource()));
            row.setLineNo(lp.getLineNo() != null ? lp.getLineNo() : line++);

            row.setDistrictCode(trimToNull(lp.getDistrictCode()));
            row.setDistrictName(trimToNull(lp.getDistrictName()));
            row.setTalukaCode(trimToNull(lp.getTalukaCode()));
            row.setTalukaName(trimToNull(lp.getTalukaName()));
            row.setVillageLgdCode(trimToNull(lp.getVillageLgdCode()));
            row.setVillageName(trimToNull(lp.getVillageName()));
            row.setSurveyPin(trimToNull(lp.getSurveyPin()));
            row.setPin1(trimToNull(lp.getPin1()));
            row.setPin2(trimToNull(lp.getPin2()));
            row.setPin3(trimToNull(lp.getPin3()));
            row.setPin4(trimToNull(lp.getPin4()));
            row.setPin5(trimToNull(lp.getPin5()));
            row.setPin6(trimToNull(lp.getPin6()));
            row.setPin7(trimToNull(lp.getPin7()));
            row.setPin8(trimToNull(lp.getPin8()));

            row.setOfficeCode(trimToNull(lp.getOfficeCode()));
            row.setOfficeName(trimToNull(lp.getOfficeName()));
            row.setVillageCode(trimToNull(lp.getVillageCode()));
            row.setCtsNo(trimToNull(lp.getCtsNo()));
            row.setParentCtsNo(trimToNull(lp.getParentCtsNo()));
            row.setSubCtsNo(trimToNull(lp.getSubCtsNo()));
            DisputedLandFieldSupport.persistFromPayload(lp, row, filingJsonCodec);

            app.getDisputedLands().add(row);
        }
    }

    private void fillOfficeFromDisputedLandsIfMissing(FilingApplication app) {
        if (app.getOffice() != null) {
            return;
        }
        for (ApplicationDisputedLand land : app.getDisputedLands()) {
            String officeCode = trimToNull(land.getOfficeCode());
            if (officeCode == null) {
                continue;
            }
            Office office = officeRepository.findFirstByOfficeCodeIgnoreCase(officeCode)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid disputedLands officeCode: " + officeCode));
            app.setOffice(office);
            return;
        }
    }

    private static String normalizeLandType(String raw) {
        if (!hasText(raw)) {
            return raw;
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        // Frontend legacy alias
        if ("URBAN_CTS".equals(normalized)) {
            return "URBAN_PROPERTY_CARD";
        }
        return normalized;
    }

    private void applyAttachments(
            FilingApplication app,
            List<ApplicationAttachmentPayload> list,
            String filingLoginId
    ) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (ApplicationAttachmentPayload ap : list) {
            ApplicationAttachment row = new ApplicationAttachment();
            row.setApplication(app);
            if (ap.getDocumentTypeId() != null) {
                DocumentType documentType = documentTypeRepository.findById(ap.getDocumentTypeId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid attachment documentTypeId: " + ap.getDocumentTypeId()));
                row.setDocumentType(documentType);
                row.setKind(ApplicationAttachmentKind.OTHER);
            } else {
                row.setKind(requiredEnum(ApplicationAttachmentKind.class, ap.getKind(), "attachment kind"));
            }
            row.setStorageKey(requiredText(ap.getStorageKey(), "storageKey"));
            row.setFileName(requiredText(ap.getFileName(), "fileName"));
            row.setMimeType(trimToNull(ap.getMimeType()));
            row.setUploadedAt(ap.getUploadedAt() != null ? ap.getUploadedAt() : Instant.now());
            row.setUploadedByLoginId(trimToNull(ap.getUploadedByLoginId()) != null ? ap.getUploadedByLoginId().trim()
                    : filingLoginId.trim());
            app.getAttachments().add(row);
        }
    }

    private static ApplicationSaveResponse buildResponse(FilingApplication entity) {
        ApplicationSaveResponse r = new ApplicationSaveResponse();
        r.setApplicationId(entity.getId());
        r.setApplicationNo(entity.getApplicationNo());
        r.setClientApplicationRef(entity.getClientApplicationRef() != null ? entity.getClientApplicationRef().toString() : null);
        r.setStatus(entity.getStatus().name());
        Map<String, Long> applicants = new LinkedHashMap<>();
        for (ApplicationApplicant a : entity.getApplicants()) {
            applicants.put(a.getClientRowKey(), a.getId());
        }
        r.setApplicantIdByClientRowKey(applicants);
        Map<String, Long> resp = new LinkedHashMap<>();
        for (ApplicationRespondent x : entity.getRespondents()) {
            if (x.getClientRowKey() != null && hasText(x.getClientRowKey())) {
                resp.put(x.getClientRowKey().trim(), x.getId());
            }
        }
        r.setRespondentIdByClientRowKey(resp);
        r.setMessage(entity.getStatus() == ApplicationStatus.SUBMITTED
                ? "Application submitted."
                : "Draft saved.");
        return r;
    }

    private static ApplicationStatus parseStatus(String raw) {
        try {
            return ApplicationStatus.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new IllegalArgumentException("status must be DRAFT or SUBMITTED.");
        }
    }

    private static <E extends Enum<E>> E parseEnumQuiet(Class<E> type, String raw) {
        if (!hasText(raw)) {
            return null;
        }
        try {
            return Enum.valueOf(type, raw.trim().toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static <E extends Enum<E>> E requiredEnum(Class<E> type, String raw, String fieldLabel) {
        if (!hasText(raw)) {
            throw new IllegalArgumentException(fieldLabel + " is required.");
        }
        try {
            return Enum.valueOf(type, raw.trim().toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid " + fieldLabel + ".");
        }
    }

    private static UUID parseClientRef(String raw) {
        String t = trimToNull(raw);
        if (t == null) {
            return null;
        }
        try {
            UUID u = UUID.fromString(t.trim());
            if ("00000000-0000-0000-0000-000000000000".equalsIgnoreCase(u.toString())) {
                return null;
            }
            return u;
        } catch (IllegalArgumentException ex) {
            // Allow custom client reference formats by converting to deterministic UUID.
            return UUID.nameUUIDFromBytes(t.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    private static Long requiredId(Long id, String label) {
        if (id == null || zeroToNull(id) == null) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return id;
    }

    private static Long zeroToNull(Long id) {
        if (id == null || id == 0L) {
            return null;
        }
        return id;
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String trimToNull(String s) {
        return hasText(s) ? s.trim() : null;
    }

    private static String firstNonBlankString(String... values) {
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

    private static String requiredText(String s, String field) {
        if (!hasText(s)) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return s.trim();
    }

    private static String normalizeLogin(String login) {
        return login.trim().toLowerCase(Locale.ROOT);
    }

    private Long resolveOfficerCurrentOfficeId(String login) {
        EmployeePosting posting = resolveOfficerCurrentPosting(login);
        Long officeId = posting.getOffice() != null ? posting.getOffice().getId() : null;
        if (officeId == null) {
            throw new IllegalArgumentException("Officer current posting office is missing.");
        }
        return officeId;
    }

    private EmployeePosting resolveOfficerCurrentPosting(String login) {
        Employee employee = resolveOfficerEmployee(login);
        return employeePostingRepository.findFirstByEmployeeIdAndToDateIsNull(employee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Current posting not found for officer."));
    }

    private boolean isPresidingOfficer(EmployeePosting posting) {
        Long designationId = posting.getDesignation() != null ? posting.getDesignation().getId() : null;
        return Objects.equals(designationId, PRESIDING_OFFICER_DESIGNATION_ID);
    }

    private boolean isAssignedToCurrentOfficerRole(FilingApplication app, boolean officerIsPo) {
        if (app.getRegisteredCaseId() != null) {
            return false;
        }
        if (Boolean.TRUE.equals(app.getPoApproved()) || Boolean.TRUE.equals(app.getPoRejected())) {
            return false;
        }
        // Simplified office-level visibility: all pending submitted applications
        // for the officer's office should be visible, regardless of current stage.
        return true;
    }

    private FilingApplication resolveOfficerScopedApplication(Long applicationId, Long officeId) {
        return filingApplicationRepository.findByIdAndOfficeIdAndStatus(
                        requiredId(applicationId, "applicationId"),
                        officeId,
                        ApplicationStatus.SUBMITTED
                )
                .orElseThrow(() -> new IllegalArgumentException("Application not found for officer inbox."));
    }

    private FilingApplication ensureInitialClerkFlagsOnSubmit(FilingApplication app, String actorLogin) {
        if (app.getStatus() != ApplicationStatus.SUBMITTED) {
            return app;
        }
        if (app.getForwardedToPo() != null) {
            return app;
        }
        app.setForwardedToPo(false);
        app.setSentBackToClerk(false);
        app.setPoApproved(false);
        app.setPoRejected(false);
        app.setLastActionByRole("CLERK");
        app.setLastActionAt(Instant.now());
        FilingApplication saved = filingApplicationRepository.save(app);
        Objects.requireNonNull(actorLogin);
        return saved;
    }

    private static ApplicationActionResponse buildActionResponse(FilingApplication app, String message) {
        ApplicationActionResponse out = new ApplicationActionResponse();
        out.setApplicationId(app.getId());
        out.setProcessingStage(deriveProcessingStage(app));
        out.setCurrentAssigneeRole(Boolean.TRUE.equals(app.getForwardedToPo()) ? "PRESIDING_OFFICER" : "CLERK");
        out.setMessage(message);
        return out;
    }

    private static String deriveProcessingStage(FilingApplication app) {
        if (app.getStatus() == ApplicationStatus.DRAFT) {
            return "DRAFT";
        }
        if (Boolean.TRUE.equals(app.getPoApproved())) {
            return "PO_APPROVED_CASE_CREATED";
        }
        if (Boolean.TRUE.equals(app.getPoRejected())) {
            return "PO_REJECTED";
        }
        if (Boolean.TRUE.equals(app.getForwardedToPo())) {
            return "PO_UNDER_REVIEW";
        }
        if (Boolean.TRUE.equals(app.getSentBackToClerk())) {
            return "PO_SENT_BACK_TO_CLERK";
        }
        return "CLERK_DRAFT_REVIEW";
    }

    private Employee resolveOfficerEmployee(String login) {
        if (login.endsWith("@officer.local")) {
            String employeeCode = login.substring(0, login.length() - "@officer.local".length()).trim();
            if (hasText(employeeCode)) {
                Optional<Employee> byCode = employeeRepository.findFirstByEmployeeCodeIgnoreCase(employeeCode);
                if (byCode.isPresent()) {
                    return byCode.get();
                }
            }
        }
        return employeeRepository.findFirstByEmailIgnoreCase(login)
                .orElseThrow(() -> new IllegalArgumentException("Officer employee profile not found."));
    }

    @SafeVarargs
    private static <T> T firstNonNull(T... vals) {
        if (vals == null) {
            return null;
        }
        for (T v : vals) {
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    private OfficerApplicationDetailResponse toOfficerApplicationDetailResponse(
            FilingApplication app,
            Long caseId,
            String caseNo,
            String status,
            String processingStage,
            String currentAssigneeRole
    ) {
        OfficerApplicationDetailResponse out = new OfficerApplicationDetailResponse();
        out.setApplicationId(app.getId());
        out.setApplicationNo(app.getApplicationNo());
        out.setClientApplicationRef(app.getClientApplicationRef() != null ? app.getClientApplicationRef().toString() : null);
        out.setCaseId(caseId);
        out.setCaseNo(caseNo);
        out.setCaseCategoryId(app.getCaseCategory() != null ? app.getCaseCategory().getId() : null);
        out.setCaseCategoryName(app.getCaseCategory() != null ? app.getCaseCategory().getName() : null);
        out.setStatus(status);
        out.setProcessingStage(processingStage);
        out.setCurrentAssigneeRole(currentAssigneeRole);
        out.setOfficeId(app.getOffice() != null ? app.getOffice().getId() : null);
        out.setOfficeName(app.getOffice() != null ? app.getOffice().getName() : null);
        out.setSubjectId(app.getSubject() != null ? app.getSubject().getId() : null);
        out.setSubjectName(app.getSubject() != null ? app.getSubject().getSubjectName() : null);
        out.setApplicationDescription(app.getApplicationDescription());
        out.setCreatedAt(app.getCreatedAt());
        out.setUpdatedAt(app.getUpdatedAt());
        out.setSubmittedAt(app.getSubmittedAt());
        out.setAffidavitText(app.getAffidavitText());
        out.setPrayerText(app.getPrayerText());

        if (app.getFiledByAdvocate() != null) {
            out.setFiledByName(app.getFiledByAdvocate().getFullName());
            out.setFiledByRole("ADVOCATE");
        } else if (app.getFiledByParty() != null) {
            out.setFiledByName(app.getFiledByParty().getFullName());
            out.setFiledByRole(app.getFiledByParty().getRole() != null ? app.getFiledByParty().getRole().name() : "PARTY_IN_PERSON");
        }

        if (app.getDisputedOrder() != null) {
            out.setDisputedOrder(toDisputedOrderPayload(app.getDisputedOrder()));
        }
        out.setApplicants(toApplicantPayloads(app.getApplicants()));
        out.setRespondents(toRespondentPayloads(app.getRespondents()));
        out.setDisputedLands(toDisputedLandPayloads(app.getDisputedLands()));
        out.setAttachments(toAttachmentPayloads(app.getAttachments()));
        out.setDescription(toDescriptionPayload(app));
        enrichPreviewResponse(out, app);
        return out;
    }

    private ApplicationDescriptionPayload toDescriptionPayload(FilingApplication app) {
        ApplicationDescriptionPayload description = new ApplicationDescriptionPayload();
        description.setAffidavitText(app.getAffidavitText());
        description.setPrayerText(app.getPrayerText());
        List<String> paragraphs = new ArrayList<>();
        if (app.getDescriptionParagraphs() != null) {
            app.getDescriptionParagraphs().stream()
                    .sorted(Comparator.comparingInt(p -> p.getParaNo() != null ? p.getParaNo() : 0))
                    .forEach(p -> paragraphs.add(p.getText()));
        }
        if (paragraphs.isEmpty() && hasText(app.getApplicationDescription())) {
            paragraphs.addAll(splitDescriptionParagraphs(app.getApplicationDescription()));
        }
        description.setParagraphs(paragraphs);
        return description;
    }

    private static ApplicationDescriptionPayload descriptionFromJoinedText(String text) {
        ApplicationDescriptionPayload description = new ApplicationDescriptionPayload();
        description.setParagraphs(splitDescriptionParagraphs(text));
        return description;
    }

    private static List<String> splitDescriptionParagraphs(String text) {
        List<String> out = new ArrayList<>();
        if (!hasText(text)) {
            return out;
        }
        String normalized = text.replace("\r\n", "\n").trim();
        String[] parts = normalized.split("\\n\\n+");
        for (String part : parts) {
            String trimmed = trimToNull(part);
            if (trimmed != null) {
                out.add(trimmed);
            }
        }
        if (out.isEmpty()) {
            out.add(normalized);
        }
        return out;
    }

    private ApplicationFormNestedPayload toApplicationFormPayload(FilingApplication app) {
        ApplicationFormNestedPayload form = new ApplicationFormNestedPayload();
        form.setSubjectId(app.getSubject() != null ? app.getSubject().getId() : null);
        form.setApplicationDescription(app.getApplicationDescription());
        form.setDistrictId(app.getDistrict() != null ? app.getDistrict().getId() : null);
        form.setSubdistrictId(app.getSubdistrict() != null ? app.getSubdistrict().getId() : null);
        form.setTalukaId(app.getTaluka() != null ? app.getTaluka().getId() : null);
        form.setOfficeId(app.getOffice() != null ? app.getOffice().getId() : null);
        form.setOfficeCode(app.getOffice() != null ? trimToNull(app.getOffice().getOfficeCode()) : null);
        form.setActId(app.getAct() != null ? app.getAct().getId() : null);
        form.setActCode(app.getAct() != null ? trimToNull(app.getAct().getActCode()) : null);
        form.setSectionId(app.getSection() != null ? app.getSection().getId() : null);
        form.setSectionCode(app.getSection() != null ? trimToNull(app.getSection().getSectionCode()) : null);
        form.setSectionCustomText(app.getSectionCustomText());
        form.setMutationYear(app.getMutationYear());
        form.setMutationTypeFilter(app.getMutationTypeFilter());
        form.setApplicants(toApplicantPayloads(app.getApplicants()));
        form.setRespondents(toRespondentPayloads(app.getRespondents()));
        form.setVakalatnamaAssignments(toVakalatnamaGroupPayloads(app.getVakalatnamaGroups()));
        return form;
    }

    private void enrichPreviewResponse(OfficerApplicationDetailResponse out, FilingApplication app) {
        ApplicationDescriptionPayload description = out.getDescription();
        List<DisputedLandPayload> lands = out.getDisputedLands();
        ApplicationDisputedOrderPayload disputedOrder = out.getDisputedOrder();

        ApplicationFormNestedPayload form = toApplicationFormPayload(app);
        form.setDescription(description);
        form.setDisputedOrder(disputedOrder);
        form.setDisputedLands(lands);
        out.setForm(form);

        hydratePreviewFromSnapshot(out, app);

        if (out.getForm() != null) {
            out.getForm().setDescription(out.getDescription());
            out.getForm().setDisputedOrder(out.getDisputedOrder());
            out.getForm().setDisputedLands(out.getDisputedLands());
        }
    }

    private void hydratePreviewFromSnapshot(OfficerApplicationDetailResponse out, FilingApplication app) {
        String snapshotJson = app.getFormSnapshotJson();
        if (!hasText(snapshotJson)) {
            return;
        }
        try {
            ApplicationSavePayload snapshot = filingJsonCodec.readValue(snapshotJson, ApplicationSavePayload.class);
            out.setFormSnapshot(snapshot);
            mergePreviewLandsFromSnapshot(out.getDisputedLands(), snapshot.getDisputedLands());
            if (out.getForm() != null) {
                mergePreviewLandsFromSnapshot(out.getForm().getDisputedLands(), snapshot.getDisputedLands());
            }
            if (out.getDescription() != null && snapshot.getDescription() != null) {
                if (!hasText(out.getDescription().getAffidavitText())) {
                    out.getDescription().setAffidavitText(snapshot.getDescription().getAffidavitText());
                }
                if (!hasText(out.getDescription().getPrayerText())) {
                    out.getDescription().setPrayerText(snapshot.getDescription().getPrayerText());
                }
            }
        } catch (IllegalArgumentException ignored) {
            out.setFormSnapshot(filingJsonCodec.readObject(snapshotJson));
        }
    }

    private static void mergePreviewLandsFromSnapshot(
            List<DisputedLandPayload> current,
            List<DisputedLandPayload> snapshotLands
    ) {
        if (current == null || snapshotLands == null || snapshotLands.isEmpty()) {
            return;
        }
        Map<Integer, DisputedLandPayload> byLine = new HashMap<>();
        for (DisputedLandPayload snap : snapshotLands) {
            if (snap.getLineNo() != null) {
                byLine.put(snap.getLineNo(), snap);
            }
        }
        int index = 0;
        for (DisputedLandPayload land : current) {
            DisputedLandPayload snap = land.getLineNo() != null
                    ? byLine.get(land.getLineNo())
                    : (index < snapshotLands.size() ? snapshotLands.get(index) : null);
            if (snap != null) {
                DisputedLandFieldSupport.mergeSnapshotLand(land, snap);
            }
            index++;
        }
    }

    private ApplicationDisputedOrderPayload toDisputedOrderPayload(ApplicationDisputedOrder ord) {
        ApplicationDisputedOrderPayload dto = new ApplicationDisputedOrderPayload();
        dto.setLandChannel(ord.getLandChannel());
        dto.setSearchCriteriaCode(ord.getSearchCriteriaCode());
        dto.setSearchDisplayText(ord.getSearchDisplayText());
        dto.setResolvedInwardNumber(ord.getResolvedInwardNumber());
        dto.setLocation(filingJsonCodec.readLocation(ord.getLocationJson()));
        dto.setCriteriaValues(filingJsonCodec.readMap(ord.getCriteriaValuesJson()));
        dto.setMutationSnapshot(filingJsonCodec.readObject(ord.getMutationSnapshotJson()));
        dto.setExternalRefs(filingJsonCodec.readMap(ord.getExternalRefsJson()));
        dto.setSearchMode(ord.getSearchMode() != null ? ord.getSearchMode().name() : null);
        if (dto.getSearchMode() == null && hasText(ord.getSearchCriteriaCode())) {
            dto.setSearchMode(ord.getSearchCriteriaCode());
        }
        dto.setSearchValue(ord.getSearchValue());
        dto.setMutationFound(ord.getMutationFound());
        dto.setMutationSearched(ord.getMutationSearched());
        dto.setManualInwardNumber(ord.getManualInwardNumber());
        dto.setManualInwardDate(ord.getManualInwardDate());
        dto.setManualMutationType(ord.getManualMutationType());
        dto.setManualApplicantName(ord.getManualApplicantName());
        dto.setManualVillage(ord.getManualVillage());
        dto.setManualStatus(ord.getManualStatus());

        MutationDetailsPayload md = new MutationDetailsPayload();
        md.setInwardNumber(ord.getInwardNumber());
        md.setInwardDate(ord.getInwardDate());
        md.setMutationType(ord.getMutationType());
        md.setApplicantName(ord.getApplicantName());
        md.setVillage(ord.getVillage());
        md.setStatus(ord.getOrderStatus());
        md.setAttachFileUrl(ord.getAttachFileUrl());
        md.setNotice9Url(null);
        dto.setMutationDetails(md);

        Notice9ResolvedPayload n = filingJsonCodec.readNotice9(ord.getNotice9Json());
        if (n == null) {
            n = new Notice9ResolvedPayload();
            n.setAvailable(ord.getNotice9Available());
            n.setSourceKind(ord.getNotice9SourceKind() != null ? ord.getNotice9SourceKind().name() : null);
            n.setPreviewKind(ord.getNotice9PreviewKind());
            n.setUrl(null);
        }
        dto.setNotice9(n);
        dto.setNotice9Resolved(n);
        return dto;
    }

    private static List<ApplicantRowPayload> toApplicantPayloads(List<ApplicationApplicant> rows) {
        List<ApplicantRowPayload> out = new ArrayList<>();
        if (rows == null) {
            return out;
        }
        for (ApplicationApplicant row : rows) {
            ApplicantRowPayload dto = new ApplicantRowPayload();
            dto.setLineNo(row.getLineNo());
            dto.setClientRowKey(row.getClientRowKey());
            dto.setTempId(row.getClientRowKey());
            dto.setName(row.getName());
            dto.setFirstName(row.getFirstName());
            dto.setMiddleName(row.getMiddleName());
            dto.setLastName(row.getLastName());
            dto.setPincode(row.getPincode());
            dto.setDistrict(row.getDistrict());
            dto.setTaluka(row.getTaluka());
            dto.setVillage(row.getVillage());
            dto.setVillageValue(row.getVillageValue());
            dto.setEmail(row.getEmail());
            dto.setMobile(row.getMobile());
            dto.setDob(row.getDob());
            dto.setAge(row.getAge());
            dto.setOccupation(row.getOccupation());
            dto.setAddress(row.getAddress());
            out.add(dto);
        }
        return out;
    }

    private static List<RespondentRowPayload> toRespondentPayloads(List<ApplicationRespondent> rows) {
        List<RespondentRowPayload> out = new ArrayList<>();
        if (rows == null) {
            return out;
        }
        for (ApplicationRespondent row : rows) {
            RespondentRowPayload dto = new RespondentRowPayload();
            dto.setLineNo(row.getLineNo());
            dto.setClientRowKey(row.getClientRowKey());
            dto.setTempId(row.getClientRowKey());
            dto.setName(row.getName());
            dto.setFirstName(row.getFirstName());
            dto.setMiddleName(row.getMiddleName());
            dto.setLastName(row.getLastName());
            dto.setPincode(row.getPincode());
            dto.setDistrict(row.getDistrict());
            dto.setTaluka(row.getTaluka());
            dto.setVillage(row.getVillage());
            dto.setVillageValue(row.getVillageValue());
            dto.setEmail(row.getEmail());
            dto.setMobile(row.getMobile());
            dto.setDob(row.getDob());
            dto.setAge(row.getAge());
            dto.setOccupation(row.getOccupation());
            dto.setAddress(row.getAddress());
            out.add(dto);
        }
        return out;
    }

    private static String buildCompatibleFullName(String firstName, String middleName, String lastName, String fallbackName) {
        String fn = trimToNull(firstName);
        String mn = trimToNull(middleName);
        String ln = trimToNull(lastName);
        String fromParts = String.join(" ",
                fn == null ? "" : fn,
                mn == null ? "" : mn,
                ln == null ? "" : ln).trim();
        if (hasText(fromParts)) {
            return fromParts;
        }
        return requiredText(fallbackName, "name");
    }

    private static void validatePartyRow(
            String label,
            String firstName,
            String lastName,
            String pincode,
            String district,
            String taluka,
            String village,
            String address,
            String email,
            String mobile,
            String dob
    ) {
        requiredText(firstName, label + " firstName");
        requiredText(lastName, label + " lastName");
        String pin = requiredText(pincode, label + " pincode");
        if (!pin.matches(PINCODE_REGEX)) {
            throw new IllegalArgumentException(label + " pincode must be 6 digits.");
        }
        requiredText(district, label + " district");
        requiredText(taluka, label + " taluka");
        requiredText(village, label + " village");
        String addr = requiredText(address, label + " address");
        if (addr.length() < 5) {
            throw new IllegalArgumentException(label + " address must be at least 5 characters.");
        }
        String mob = requiredText(mobile, label + " mobile");
        if (!mob.matches(MOBILE_REGEX)) {
            throw new IllegalArgumentException(label + " mobile must be 10 digits.");
        }
        String em = trimToNull(email);
        if (em != null && !em.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException(label + " email is invalid.");
        }
        String dobText = trimToNull(dob);
        if (dobText != null && !dobText.matches(DOB_REGEX)) {
            throw new IllegalArgumentException(label + " dob must be YYYY-MM-DD.");
        }
    }

    private List<DisputedLandPayload> toDisputedLandPayloads(List<ApplicationDisputedLand> rows) {
        List<DisputedLandPayload> out = new ArrayList<>();
        if (rows == null) {
            return out;
        }
        for (ApplicationDisputedLand row : rows) {
            DisputedLandPayload dto = new DisputedLandPayload();
            dto.setLineNo(row.getLineNo());
            dto.setLandType(row.getLandType() != null ? row.getLandType().name() : null);
            dto.setExternalSource(row.getExternalSource() != null ? row.getExternalSource().name() : null);
            dto.setDistrictCode(row.getDistrictCode());
            dto.setDistrictName(row.getDistrictName());
            dto.setTalukaCode(row.getTalukaCode());
            dto.setTalukaName(row.getTalukaName());
            dto.setVillageLgdCode(row.getVillageLgdCode());
            dto.setVillageName(row.getVillageName());
            dto.setSurveyPin(row.getSurveyPin());
            dto.setPin1(row.getPin1());
            dto.setPin2(row.getPin2());
            dto.setPin3(row.getPin3());
            dto.setPin4(row.getPin4());
            dto.setPin5(row.getPin5());
            dto.setPin6(row.getPin6());
            dto.setPin7(row.getPin7());
            dto.setPin8(row.getPin8());
            dto.setOfficeCode(row.getOfficeCode());
            dto.setOfficeName(row.getOfficeName());
            dto.setVillageCode(row.getVillageCode());
            dto.setCtsNo(row.getCtsNo());
            dto.setParentCtsNo(row.getParentCtsNo());
            dto.setSubCtsNo(row.getSubCtsNo());
            dto.setTotalArea(row.getTotalArea());
            dto.setDisputedArea(row.getDisputedArea());
            dto.setAreaUnit(row.getAreaUnit());
            dto.setLandHoldersText(row.getLandHoldersText());
            DisputedLandFieldSupport.enrichPayload(row, dto, filingJsonCodec);
            out.add(dto);
        }
        return out;
    }

    private static List<VakalatnamaGroupPayload> toVakalatnamaGroupPayloads(List<ApplicationVakalatnamaGroup> groups) {
        List<VakalatnamaGroupPayload> out = new ArrayList<>();
        if (groups == null) {
            return out;
        }
        for (ApplicationVakalatnamaGroup group : groups) {
            VakalatnamaGroupPayload dto = new VakalatnamaGroupPayload();
            dto.setGroupNo(group.getGroupNo());
            if (group.getPrimaryAdvocateRegistration() != null) {
                dto.setPrimaryAdvocateId(group.getPrimaryAdvocateRegistration().getId());
            }
            AdvocateSnapshotPayload primary = new AdvocateSnapshotPayload();
            primary.setId(dto.getPrimaryAdvocateId());
            primary.setFullName(group.getSnapshotFullName());
            primary.setEmail(group.getSnapshotEmail());
            primary.setMobileNumber(group.getSnapshotMobile());
            primary.setAddress(group.getSnapshotAddress());
            primary.setBarCouncilNumber(group.getSnapshotBarCouncilNumber());
            primary.setEnrollmentNumber(group.getSnapshotEnrollmentNumber());
            primary.setLawFirmName(group.getSnapshotLawFirmName());
            dto.setAdvocate(primary);

            List<String> applicantKeys = new ArrayList<>();
            if (group.getApplicantLinks() != null) {
                for (ApplicationVakalatnamaGroupApplicant link : group.getApplicantLinks()) {
                    if (link.getApplicationApplicant() != null
                            && hasText(link.getApplicationApplicant().getClientRowKey())) {
                        applicantKeys.add(link.getApplicationApplicant().getClientRowKey());
                    }
                }
            }
            dto.setApplicantClientRowKeys(applicantKeys);

            List<VakCoAdvocatePayload> coAdvocates = new ArrayList<>();
            if (group.getCoAdvocates() != null) {
                for (ApplicationVakalatnamaCoAdvocate co : group.getCoAdvocates()) {
                    VakCoAdvocatePayload coDto = new VakCoAdvocatePayload();
                    if (co.getAdvocateRegistration() != null) {
                        coDto.setAdvocateId(co.getAdvocateRegistration().getId());
                    }
                    AdvocateSnapshotPayload snap = new AdvocateSnapshotPayload();
                    snap.setId(coDto.getAdvocateId());
                    snap.setFullName(co.getSnapshotFullName());
                    snap.setEmail(co.getSnapshotEmail());
                    snap.setMobileNumber(co.getSnapshotMobile());
                    snap.setAddress(co.getSnapshotAddress());
                    snap.setBarCouncilNumber(co.getSnapshotBarCouncilNumber());
                    snap.setEnrollmentNumber(co.getSnapshotEnrollmentNumber());
                    snap.setLawFirmName(co.getSnapshotLawFirmName());
                    coDto.setAdvocate(snap);
                    coAdvocates.add(coDto);
                }
            }
            dto.setCoAdvocates(coAdvocates);
            out.add(dto);
        }
        return out;
    }

    private static List<ApplicationAttachmentPayload> toAttachmentPayloads(List<ApplicationAttachment> rows) {
        List<ApplicationAttachmentPayload> out = new ArrayList<>();
        if (rows == null) {
            return out;
        }
        for (ApplicationAttachment row : rows) {
            ApplicationAttachmentPayload dto = new ApplicationAttachmentPayload();
            dto.setKind(row.getKind() != null ? row.getKind().name() : null);
            dto.setDocumentTypeId(row.getDocumentType() != null ? row.getDocumentType().getId() : null);
            dto.setStorageKey(row.getStorageKey());
            dto.setFileName(row.getFileName());
            dto.setMimeType(row.getMimeType());
            dto.setUploadedAt(row.getUploadedAt());
            dto.setUploadedByLoginId(row.getUploadedByLoginId());
            out.add(dto);
        }
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

    private static CaseNoticeResponse toPartyCaseNoticeResponse(CaseNotice row) {
        CaseNoticeResponse out = toCaseNoticeResponse(row);
        out.setDraftContent(null);
        out.setDigitalSignatureRef(null);
        String preview = trimToNull(row.getFinalContent());
        if (preview == null) {
            preview = trimToNull(row.getDraftContent());
        }
        out.setPreviewContent(preview);
        out.setFinalContent(preview);
        return out;
    }

    private static List<String> parseJsonArray(String raw) {
        if (trimToNull(raw) == null) {
            return Collections.emptyList();
        }
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(raw, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                    })
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(v -> !v.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private FilingApplication ensureApplicationNumber(FilingApplication app) {
        if (app.getStatus() != ApplicationStatus.SUBMITTED || hasText(app.getApplicationNo())) {
            return app;
        }
        app.setApplicationNo(buildApplicationNumber(app));
        FilingApplication saved = filingApplicationRepository.save(app);
        filingApplicationRepository.flush();
        return saved;
    }

    private static String buildApplicationNumber(FilingApplication app) {
        int year = app.getSubmittedAt() != null
                ? app.getSubmittedAt().atZone(java.time.ZoneOffset.UTC).getYear()
                : java.time.Year.now(java.time.ZoneOffset.UTC).getValue();
        return String.format(Locale.ROOT, "APP/%04d/%06d", year, app.getId());
    }

    private static String buildCaseNumber(CaseRegistry row) {
        int year = row.getApprovedAt() != null
                ? row.getApprovedAt().atZone(java.time.ZoneOffset.UTC).getYear()
                : java.time.Year.now(java.time.ZoneOffset.UTC).getValue();
        Long categoryId = row.getCaseCategory() != null ? row.getCaseCategory().getId() : null;
        long cat = categoryId != null ? categoryId : 0L;
        return String.format(Locale.ROOT, "CASE/%04d/CAT%d/%06d", year, cat, row.getId());
    }

    private FilingApplication resolveFilerScopedApplication(Long applicationId, Principal principal) {
        if (applicationId == null) {
            throw new IllegalArgumentException("applicationId is required.");
        }
        Objects.requireNonNull(principal);
        String login = normalizeLogin(principal.getName());
        FilingApplication app = filingApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));
        assertPartyOwnership(app, login);
        return app;
    }

    private static boolean isOfficerPrincipal(Principal principal) {
        Objects.requireNonNull(principal);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if ("ROLE_OFFICER".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private FilingApplication resolveOfficerHistoryScopedApplication(Long applicationId, Principal principal) {
        if (applicationId == null) {
            throw new IllegalArgumentException("applicationId is required.");
        }
        Objects.requireNonNull(principal);
        String login = normalizeLogin(principal.getName());
        Long officeId = resolveOfficerCurrentOfficeId(login);

        FilingApplication app = filingApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));
        if (app.getOffice() == null || !Objects.equals(app.getOffice().getId(), officeId)) {
            throw new IllegalArgumentException("Application not found for officer office.");
        }
        return app;
    }

    private ApplicationHistoryListResponse buildApplicationHistoryList(FilingApplication app, boolean officerView) {
        List<ApplicationHistory> rows = applicationHistoryRepository.findByApplication_IdOrderByCreatedAtAscIdAsc(app.getId());
        boolean filingSynthetic = rows.isEmpty();
        List<ApplicationHistoryResponse> entries = filingSynthetic
                ? buildSyntheticApplicationHistory(app)
                : rows.stream().map(this::toApplicationHistoryResponse).collect(Collectors.toList());
        for (ApplicationHistoryResponse entry : entries) {
            entry.setPhase("FILING");
        }

        int filingCount = entries.size();
        Long caseId = app.getRegisteredCaseId();
        String caseNo = null;
        if (caseId != null) {
            CaseRegistry caseRow = caseRegistryRepository.findById(caseId).orElse(null);
            caseNo = caseRow != null ? caseRow.getCaseNo() : null;
            appendProceedingHistory(entries, app, caseId, caseNo, officerView);
        }

        sortHistoryEntries(entries);
        assignSequenceAndApplicationId(entries, app.getId());

        int proceedingCount = entries.size() - filingCount;
        ApplicationHistoryListResponse out = new ApplicationHistoryListResponse();
        out.setApplicationId(app.getId());
        out.setApplicationNo(app.getApplicationNo());
        out.setCaseId(caseId);
        out.setCaseNo(caseNo);
        out.setFilingCount(filingCount);
        out.setProceedingCount(proceedingCount);
        out.setStatus(app.getStatus() != null ? app.getStatus().name() : null);
        String stage = caseId != null ? "CASE_PROCEEDINGS" : deriveProcessingStage(app);
        out.setProcessingStage(stage);
        out.setProcessingStageLabel(processingStageLabel(stage));
        out.setCurrentAssigneeRole(caseId != null ? "PRESIDING_OFFICER" : currentAssigneeRole(app));
        out.setSynthetic(filingSynthetic && proceedingCount == 0);
        out.setTotalCount(entries.size());
        out.setEntries(entries);
        return out;
    }

    private void appendProceedingHistory(
            List<ApplicationHistoryResponse> entries,
            FilingApplication app,
            Long caseId,
            String caseNo,
            boolean officerView
    ) {
        appendHearingHistory(entries, app, caseId, caseNo);
        appendNoticeHistory(entries, app, caseId, caseNo, officerView);
        appendOrderSheetHistory(entries, app, caseId, caseNo, officerView);
        appendJudgmentHistory(entries, app, caseId, caseNo, officerView);
    }

    private void appendHearingHistory(
            List<ApplicationHistoryResponse> entries,
            FilingApplication app,
            Long caseId,
            String caseNo
    ) {
        for (CaseHearing hearing : caseHearingRepository.findByCaseRegistryIdOrderByHearingNoAsc(caseId)) {
            if (shouldSkipHearingScheduledHistoryEntry(hearing)) {
                continue;
            }
            boolean adjournNext = isAdjournCreatedHearing(hearing);
            ApplicationHistoryAction action = adjournNext
                    ? ApplicationHistoryAction.NEXT_HEARING_ADJOURN
                    : ApplicationHistoryAction.HEARING_SCHEDULED;
            String remarks = adjournNext
                    ? String.format(
                            Locale.ROOT,
                            "Next hearing #%d on %s after adjourn (%s)",
                            hearing.getHearingNo(),
                            hearing.getHearingDate(),
                            hearing.getStatus()
                    )
                    : String.format(
                            Locale.ROOT,
                            "Hearing #%d on %s (%s)",
                            hearing.getHearingNo(),
                            hearing.getHearingDate(),
                            hearing.getStatus()
                    );
            entries.add(proceedingEntry(
                    action,
                    hearing.getCreatedAt(),
                    remarks,
                    resolveOfficerActorRoleFromLogin(hearing.getCreatedByLoginId()),
                    hearing.getCreatedByLoginId(),
                    app,
                    caseId,
                    caseNo,
                    "HEARING",
                    hearing.getId(),
                    hearing.getHearingNo(),
                    hearing.getHearingDate(),
                    null
            ));
        }
    }

    /**
     * After notice is served for an adjourn-created hearing, do not show a separate
     * "hearing scheduled" row (avoids confusing entries after "Notice served").
     */
    private static boolean shouldSkipHearingScheduledHistoryEntry(CaseHearing hearing) {
        if (hearing == null || hearing.getHearingNo() == null || hearing.getHearingNo() <= 1) {
            return false;
        }
        return Boolean.TRUE.equals(hearing.getNoticeServed()) && isAdjournCreatedHearing(hearing);
    }

    private static boolean isAdjournCreatedHearing(CaseHearing hearing) {
        String remarks = trimToNull(hearing != null ? hearing.getRemarks() : null);
        return remarks != null && remarks.toLowerCase(Locale.ROOT).contains("adjourn");
    }

    private void appendNoticeHistory(
            List<ApplicationHistoryResponse> entries,
            FilingApplication app,
            Long caseId,
            String caseNo,
            boolean officerView
    ) {
        List<CaseNotice> notices = caseNoticeRepository.findByCaseRegistryIdOrderByIdDesc(caseId);
        Set<Long> hearingIdsWithServedNotice = new HashSet<>();
        for (CaseNotice notice : notices) {
            if (notice.getStatus() == CaseNoticeStatus.SERVED
                    && notice.getHearing() != null
                    && notice.getHearing().getId() != null) {
                hearingIdsWithServedNotice.add(notice.getHearing().getId());
            }
        }
        for (CaseNotice notice : notices) {
            Long hearingId = notice.getHearing() != null ? notice.getHearing().getId() : null;
            if (hearingId != null
                    && hearingIdsWithServedNotice.contains(hearingId)
                    && notice.getStatus() != CaseNoticeStatus.SERVED) {
                continue;
            }
            if (officerView) {
                appendOfficerNoticeMilestones(entries, app, caseId, caseNo, notice);
            } else if (isPartyVisibleNoticeStatus(notice.getStatus())) {
                appendPartyNoticeMilestone(entries, app, caseId, caseNo, notice);
            }
        }
    }

    private void appendOfficerNoticeMilestones(
            List<ApplicationHistoryResponse> entries,
            FilingApplication app,
            Long caseId,
            String caseNo,
            CaseNotice notice
    ) {
        if (notice == null || notice.getStatus() != CaseNoticeStatus.SERVED) {
            return;
        }
        Instant servedAt = notice.getServedAt() != null ? notice.getServedAt() : notice.getUpdatedAt();
        entries.add(oneShotNoticeServedEntry(servedAt, notice, app, caseId, caseNo));
    }

    private ApplicationHistoryResponse oneShotNoticeServedEntry(
            Instant servedAt,
            CaseNotice notice,
            FilingApplication app,
            Long caseId,
            String caseNo
    ) {
        String type = trimToNull(notice.getNoticeType());
        String remarks = type != null
                ? type + " notice served (template applied, digitally signed)"
                : "Notice served (template applied, digitally signed)";
        if (notice.getHearing() != null && notice.getHearing().getHearingNo() != null) {
            remarks = remarks + " (Hearing #" + notice.getHearing().getHearingNo() + ")";
        }
        Integer hearingNo = notice.getHearing() != null ? notice.getHearing().getHearingNo() : null;
        LocalDate hearingDate = notice.getHearing() != null ? notice.getHearing().getHearingDate() : null;
        return proceedingEntry(
                ApplicationHistoryAction.NOTICE_SERVED,
                servedAt,
                remarks,
                resolveOfficerActorRoleFromLogin(notice.getServedByLoginId()),
                firstNonBlankString(notice.getServedByLoginId(), notice.getPoSignedByLoginId()),
                app,
                caseId,
                caseNo,
                "NOTICE",
                notice.getId(),
                hearingNo,
                hearingDate,
                type
        );
    }

    private void appendPartyNoticeMilestone(
            List<ApplicationHistoryResponse> entries,
            FilingApplication app,
            Long caseId,
            String caseNo,
            CaseNotice notice
    ) {
        ApplicationHistoryAction action = notice.getStatus() == CaseNoticeStatus.SERVED
                ? ApplicationHistoryAction.NOTICE_SERVED
                : ApplicationHistoryAction.NOTICE_FINALIZED;
        Instant at = notice.getStatus() == CaseNoticeStatus.SERVED && notice.getServedAt() != null
                ? notice.getServedAt()
                : notice.getUpdatedAt();
        entries.add(noticeEntry(action, at, notice, app, caseId, caseNo, true));
    }

    private ApplicationHistoryResponse noticeEntry(
            ApplicationHistoryAction action,
            Instant at,
            CaseNotice notice,
            FilingApplication app,
            Long caseId,
            String caseNo
    ) {
        boolean poDrafted = notice.getStatus() == CaseNoticeStatus.PO_DRAFT
                || notice.getStatus() == CaseNoticeStatus.PO_SCRUTINY
                || notice.getStatus() == CaseNoticeStatus.PO_FINALIZED
                || notice.getStatus() == CaseNoticeStatus.PO_SIGNED
                || notice.getStatus() == CaseNoticeStatus.SERVED;
        return noticeEntry(action, at, notice, app, caseId, caseNo, poDrafted);
    }

    private ApplicationHistoryResponse noticeEntry(
            ApplicationHistoryAction action,
            Instant at,
            CaseNotice notice,
            FilingApplication app,
            Long caseId,
            String caseNo,
            boolean poDrafted
    ) {
        String type = trimToNull(notice.getNoticeType());
        String remarks = type != null ? type + " notice" : "Case notice";
        if (notice.getHearing() != null && notice.getHearing().getHearingNo() != null) {
            remarks = remarks + " (Hearing #" + notice.getHearing().getHearingNo() + ")";
        }
        Integer hearingNo = notice.getHearing() != null ? notice.getHearing().getHearingNo() : null;
        LocalDate hearingDate = notice.getHearing() != null ? notice.getHearing().getHearingDate() : null;
        String actor = action == ApplicationHistoryAction.NOTICE_DRAFTED && !poDrafted
                ? "CLERK"
                : "PRESIDING_OFFICER";
        String actorLogin = resolveNoticeActorLogin(notice, action);
        return proceedingEntry(
                action,
                at,
                remarks,
                actor,
                actorLogin,
                app,
                caseId,
                caseNo,
                "NOTICE",
                notice.getId(),
                hearingNo,
                hearingDate,
                type
        );
    }

    private static String resolveNoticeActorLogin(CaseNotice notice, ApplicationHistoryAction action) {
        if (notice == null || action == null) {
            return null;
        }
        return switch (action) {
            case NOTICE_SERVED -> firstNonBlankString(
                    notice.getServedByLoginId(),
                    notice.getPoSignedByLoginId(),
                    notice.getPoFinalizedByLoginId()
            );
            case NOTICE_SIGNED -> firstNonBlankString(
                    notice.getPoSignedByLoginId(),
                    notice.getPoFinalizedByLoginId()
            );
            case NOTICE_FINALIZED, NOTICE_IN_PO_SCRUTINY -> trimToNull(notice.getPoFinalizedByLoginId());
            case NOTICE_DRAFTED -> trimToNull(notice.getClerkDraftedByLoginId());
            default -> firstNonBlankString(
                    notice.getServedByLoginId(),
                    notice.getPoSignedByLoginId(),
                    notice.getClerkDraftedByLoginId()
            );
        };
    }

    private void appendOrderSheetHistory(
            List<ApplicationHistoryResponse> entries,
            FilingApplication app,
            Long caseId,
            String caseNo,
            boolean officerView
    ) {
        Long orderSheetId = caseOrderSheetRepository.findByCaseRegistryId(caseId).map(CaseOrderSheet::getId).orElse(null);
        List<CaseOrderSheetHistory> rows = new ArrayList<>(
                caseOrderSheetHistoryRepository.findByCaseRegistryIdOrderByCreatedAtDesc(caseId)
        );
        rows.sort(Comparator.comparing(CaseOrderSheetHistory::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())));
        for (CaseOrderSheetHistory row : rows) {
            String stage = parseOrderSheetHistoryStage(row.getRemarks());
            ApplicationHistoryAction action = mapOrderSheetStageToAction(stage);
            if (!officerView && action != ApplicationHistoryAction.ORDER_SHEET_SIGNED) {
                continue;
            }

            Integer hearingNo = row.getCaseHearing() != null ? row.getCaseHearing().getHearingNo() : null;
            LocalDate hearingDate = row.getCaseHearing() != null ? row.getCaseHearing().getHearingDate() : null;
            String userRemarks = extractOrderSheetUserRemarks(row.getRemarks());
            String label = orderSheetHistoryRemarks(action, hearingNo, hearingDate, userRemarks);

            String actorRole = resolveOrderSheetHistoryActorRole(action, stage, row.getCreatedByLoginId());

            ApplicationHistoryResponse entry = proceedingEntry(
                    action,
                    row.getCreatedAt(),
                    label,
                    actorRole,
                    row.getCreatedByLoginId(),
                    app,
                    caseId,
                    caseNo,
                    "ORDER_SHEET",
                    orderSheetId != null ? orderSheetId : row.getId(),
                    hearingNo,
                    hearingDate,
                    null
            );
            entry.setHistoryId(row.getId());
            entries.add(entry);
        }
    }

    /** One {@link CaseOrderSheet} per case; history rows are audit steps per hearing workflow. */
    private static String parseOrderSheetHistoryStage(String remarks) {
        String r = trimToNull(remarks);
        if (r == null) {
            return null;
        }
        int sep = r.indexOf(" | ");
        return sep >= 0 ? r.substring(0, sep) : r;
    }

    private static String extractOrderSheetUserRemarks(String remarks) {
        String r = trimToNull(remarks);
        if (r == null) {
            return null;
        }
        int sep = r.indexOf(" | ");
        return sep >= 0 ? trimToNull(r.substring(sep + 3)) : null;
    }

    private static ApplicationHistoryAction mapOrderSheetStageToAction(String stage) {
        if (stage == null) {
            return ApplicationHistoryAction.ORDER_SHEET_DRAFT_SAVED;
        }
        return switch (stage) {
            case "CLERK_DRAFT", "PO_DRAFT" -> ApplicationHistoryAction.ORDER_SHEET_DRAFT_SAVED;
            case "PO_SCRUTINY" -> ApplicationHistoryAction.ORDER_SHEET_SUBMITTED_TO_PO;
            case "PO_FINALIZED" -> ApplicationHistoryAction.ORDER_SHEET_FINALIZED;
            case "PO_SIGNED" -> ApplicationHistoryAction.ORDER_SHEET_SIGNED;
            default -> ApplicationHistoryAction.ORDER_SHEET_DRAFT_SAVED;
        };
    }

    private static String orderSheetHistoryRemarks(
            ApplicationHistoryAction action,
            Integer hearingNo,
            LocalDate hearingDate,
            String userRemarks
    ) {
        String hearingPart = hearingNo != null
                ? "Hearing #" + hearingNo + (hearingDate != null ? " (" + hearingDate + ")" : "")
                : "Case order sheet";
        String base = switch (action) {
            case ORDER_SHEET_DRAFT_SAVED -> hearingPart + " — draft saved";
            case ORDER_SHEET_SUBMITTED_TO_PO -> hearingPart + " — submitted to PO";
            case ORDER_SHEET_FINALIZED -> hearingPart + " — finalized";
            case ORDER_SHEET_SIGNED -> hearingPart + " — signed (proceeding recorded)";
            default -> hearingPart;
        };
        return userRemarks != null ? base + ": " + userRemarks : base;
    }

    private void appendJudgmentHistory(
            List<ApplicationHistoryResponse> entries,
            FilingApplication app,
            Long caseId,
            String caseNo,
            boolean officerView
    ) {
        CaseWorkflowDefinition def = caseWorkflowConfigService.resolveForCategory(app.getCaseCategory());
        if (def.getJudgment().isAuditTrailRequired()) {
            CaseJudgmentWorkflow judgmentRow = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId).orElse(null);
            List<CaseJudgmentWorkflowHistory> judgmentHistoryRows =
                    judgmentWorkflowHistoryRepository.findByCaseRegistryIdOrderByCreatedAtAscIdAsc(caseId);
            for (CaseJudgmentWorkflowHistory judgmentHist : judgmentHistoryRows) {
                ApplicationHistoryAction action = mapJudgmentHistoryAction(judgmentHist.getActionCode());
                entries.add(judgmentEntry(
                        action,
                        judgmentHist.getCreatedAt(),
                        judgmentRow,
                        app,
                        caseId,
                        caseNo,
                        judgmentHist.getActorLoginId(),
                        judgmentHist.getRemarks(),
                        judgmentHist.getActorRole()
                ));
            }
            return;
        }

        Optional<CaseJudgmentWorkflow> judgmentOpt = caseJudgmentWorkflowRepository.findByCaseRegistryId(caseId);
        if (judgmentOpt.isEmpty()) {
            return;
        }
        CaseJudgmentWorkflow judgment = judgmentOpt.get();
        CaseJudgmentWorkflowStatus status = judgment.getStatus();
        if (status == null) {
            return;
        }
        if (officerView) {
            if (status == CaseJudgmentWorkflowStatus.PO_DRAFT) {
                entries.add(judgmentEntry(
                        ApplicationHistoryAction.JUDGMENT_PO_DRAFT_SAVED,
                        judgment.getUpdatedAt(),
                        judgment,
                        app,
                        caseId,
                        caseNo,
                        judgment.getDraftedByLoginId(),
                        null
                ));
            } else if (status == CaseJudgmentWorkflowStatus.CLERK_DRAFT) {
                entries.add(judgmentEntry(
                        ApplicationHistoryAction.JUDGMENT_DRAFT_SAVED,
                        judgment.getUpdatedAt(),
                        judgment,
                        app,
                        caseId,
                        caseNo,
                        judgment.getDraftedByLoginId(),
                        null
                ));
            } else if (status == CaseJudgmentWorkflowStatus.PO_SCRUTINY) {
                entries.add(judgmentEntry(
                        ApplicationHistoryAction.JUDGMENT_PO_DRAFT_SAVED,
                        judgment.getCreatedAt(),
                        judgment,
                        app,
                        caseId,
                        caseNo,
                        judgment.getDraftedByLoginId(),
                        null
                ));
                entries.add(judgmentEntry(
                        ApplicationHistoryAction.JUDGMENT_SUBMITTED_TO_PO,
                        judgment.getUpdatedAt(),
                        judgment,
                        app,
                        caseId,
                        caseNo,
                        judgment.getDraftedByLoginId(),
                        null
                ));
            } else if (status == CaseJudgmentWorkflowStatus.PO_FINALIZED) {
                entries.add(judgmentEntry(
                        ApplicationHistoryAction.JUDGMENT_FINALIZED,
                        judgment.getUpdatedAt(),
                        judgment,
                        app,
                        caseId,
                        caseNo,
                        judgment.getFinalizedByLoginId(),
                        null
                ));
            } else if (status == CaseJudgmentWorkflowStatus.PUBLISHED) {
                entries.add(judgmentEntry(
                        ApplicationHistoryAction.JUDGMENT_PUBLISHED,
                        judgment.getPublishedAt() != null ? judgment.getPublishedAt() : judgment.getUpdatedAt(),
                        judgment,
                        app,
                        caseId,
                        caseNo,
                        judgment.getPublishedByLoginId(),
                        null
                ));
            }
        } else if (status == CaseJudgmentWorkflowStatus.PUBLISHED) {
            entries.add(judgmentEntry(
                    ApplicationHistoryAction.JUDGMENT_PUBLISHED,
                    judgment.getPublishedAt() != null ? judgment.getPublishedAt() : judgment.getUpdatedAt(),
                    judgment,
                    app,
                    caseId,
                    caseNo,
                    judgment.getPublishedByLoginId(),
                    null
            ));
        }
    }

    private ApplicationHistoryResponse judgmentEntry(
            ApplicationHistoryAction action,
            Instant at,
            CaseJudgmentWorkflow judgment,
            FilingApplication app,
            Long caseId,
            String caseNo,
            String actorLoginId,
            String userRemarks
    ) {
        return judgmentEntry(action, at, judgment, app, caseId, caseNo, actorLoginId, userRemarks, null);
    }

    private ApplicationHistoryResponse judgmentEntry(
            ApplicationHistoryAction action,
            Instant at,
            CaseJudgmentWorkflow judgment,
            FilingApplication app,
            Long caseId,
            String caseNo,
            String actorLoginId,
            String userRemarks,
            String storedActorRole
    ) {
        String remarks = trimToNull(userRemarks);
        if (remarks == null && judgment != null) {
            String summary = trimToNull(judgment.getPublishedSummary());
            if (summary == null) {
                summary = trimToNull(judgment.getFinalSummary());
            }
            if (summary == null) {
                summary = trimToNull(judgment.getDraftSummary());
            }
            remarks = summary != null && summary.length() > 200 ? summary.substring(0, 200) + "..." : summary;
        }
        String role = trimToNull(storedActorRole);
        if (role == null) {
            role = resolveJudgmentActorRoleFallback(action, actorLoginId);
        }
        Long refId = judgment != null ? judgment.getId() : null;
        return proceedingEntry(
                action,
                at,
                remarks,
                role,
                actorLoginId,
                app,
                caseId,
                caseNo,
                "JUDGMENT",
                refId,
                null,
                null,
                null
        );
    }

    private static ApplicationHistoryAction mapJudgmentHistoryAction(String actionCode) {
        if (actionCode == null) {
            return ApplicationHistoryAction.JUDGMENT_DRAFT_SAVED;
        }
        return switch (actionCode) {
            case "PO_DRAFT_JUDGMENT", "UPDATE_PO_JUDGMENT" -> ApplicationHistoryAction.JUDGMENT_PO_DRAFT_SAVED;
            case "SEND_JUDGMENT_TO_CLERK", "REVERT_JUDGMENT_TO_CLERK" -> ApplicationHistoryAction.JUDGMENT_SENT_TO_CLERK;
            case "CLERK_UPDATE_JUDGMENT" -> ApplicationHistoryAction.JUDGMENT_DRAFT_SAVED;
            case "SUBMIT_JUDGMENT_TO_PO" -> ApplicationHistoryAction.JUDGMENT_SUBMITTED_TO_PO;
            case "FINALIZE_JUDGMENT" -> ApplicationHistoryAction.JUDGMENT_FINALIZED;
            case "PUBLISH_JUDGMENT", "SIGN_AND_PUBLISH_JUDGMENT" -> ApplicationHistoryAction.JUDGMENT_PUBLISHED;
            default -> ApplicationHistoryAction.JUDGMENT_DRAFT_SAVED;
        };
    }

    private String resolveJudgmentActorRoleFallback(ApplicationHistoryAction action, String actorLoginId) {
        return switch (action) {
            case JUDGMENT_DRAFT_SAVED, JUDGMENT_SENT_TO_CLERK, JUDGMENT_SUBMITTED_TO_PO -> "CLERK";
            case JUDGMENT_PO_DRAFT_SAVED, JUDGMENT_FINALIZED, JUDGMENT_PUBLISHED -> "PRESIDING_OFFICER";
            default -> resolveOfficerActorRoleFromLogin(actorLoginId);
        };
    }

    private String resolveOfficerActorRoleFromLogin(String loginId) {
        String login = trimToNull(loginId);
        if (login == null) {
            return "CLERK";
        }
        try {
            Employee employee = resolveOfficerEmployee(login);
            return employeePostingRepository.findFirstByEmployeeIdAndToDateIsNull(employee.getId())
                    .map(posting -> posting.getDesignation() != null
                            && Objects.equals(posting.getDesignation().getId(), PRESIDING_OFFICER_DESIGNATION_ID)
                            ? "PRESIDING_OFFICER"
                            : "CLERK")
                    .orElse("CLERK");
        } catch (IllegalArgumentException ex) {
            return "CLERK";
        }
    }

    private void requireFilingAction(FilingApplication app, EmployeePosting posting, WorkflowAction action) {
        java.util.Set<WorkflowAction> allowed = new java.util.LinkedHashSet<>();
        for (String code : workflowPolicyService.filingAllowedActions(app, posting)) {
            allowed.add(WorkflowAction.valueOf(code));
        }
        workflowPolicyService.requireAction(action, allowed);
    }

    private ApplicationHistoryResponse proceedingEntry(
            ApplicationHistoryAction action,
            Instant createdAt,
            String remarks,
            String actorRole,
            String actorLoginId,
            FilingApplication app,
            Long caseId,
            String caseNo,
            String referenceType,
            Long referenceId,
            Integer hearingNo,
            LocalDate hearingDate,
            String noticeType
    ) {
        ApplicationHistoryResponse out = new ApplicationHistoryResponse();
        out.setPhase("PROCEEDING");
        out.setHistoryId(null);
        out.setApplicationId(app.getId());
        out.setAction(action.name());
        out.setActionLabel(actionLabel(action));
        out.setRemarks(remarks);
        out.setActorRole(actorRole);
        out.setActorRoleLabel(actorRoleLabel(actorRole));
        out.setActorLoginId(actorLoginId);
        out.setApplicationNo(app.getApplicationNo());
        out.setStatus(null);
        out.setCaseId(caseId);
        out.setCaseNo(caseNo);
        out.setProcessingStage("CASE_PROCEEDINGS");
        out.setProcessingStageLabel(processingStageLabel("CASE_PROCEEDINGS"));
        out.setCreatedAt(createdAt != null ? createdAt : Instant.now());
        out.setSynthetic(true);
        out.setReferenceType(referenceType);
        out.setReferenceId(referenceId);
        out.setHearingNo(hearingNo);
        out.setHearingDate(hearingDate);
        out.setNoticeType(noticeType);
        return out;
    }

    private static void sortHistoryEntries(List<ApplicationHistoryResponse> entries) {
        entries.sort(Comparator
                .comparing(ApplicationHistoryResponse::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparingInt(FilingApplicationService::proceedingHistorySortKey)
                .thenComparing(e -> e.getHistoryId() != null ? e.getHistoryId() : e.getReferenceId(), Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private static int proceedingHistorySortKey(ApplicationHistoryResponse entry) {
        if (entry == null || entry.getAction() == null) {
            return 0;
        }
        try {
            return proceedingActionOrder(ApplicationHistoryAction.valueOf(entry.getAction()));
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }

    /** Stable order within the same timestamp: schedule → notice → roznamma. */
    private static int proceedingActionOrder(ApplicationHistoryAction action) {
        return switch (action) {
            case HEARING_SCHEDULED, NEXT_HEARING_ADJOURN -> 10;
            case NOTICE_DRAFTED -> 20;
            case NOTICE_IN_PO_SCRUTINY -> 25;
            case NOTICE_FINALIZED -> 30;
            case NOTICE_SIGNED -> 35;
            case NOTICE_SERVED -> 40;
            case ORDER_SHEET_DRAFT_SAVED -> 50;
            case ORDER_SHEET_SUBMITTED_TO_PO -> 55;
            case ORDER_SHEET_FINALIZED -> 60;
            case ORDER_SHEET_SIGNED -> 70;
            case JUDGMENT_PO_DRAFT_SAVED -> 80;
            case JUDGMENT_SENT_TO_CLERK -> 85;
            case JUDGMENT_DRAFT_SAVED -> 90;
            case JUDGMENT_SUBMITTED_TO_PO -> 95;
            case JUDGMENT_FINALIZED -> 100;
            case JUDGMENT_PUBLISHED -> 110;
            default -> 0;
        };
    }

    private String resolveOrderSheetHistoryActorRole(
            ApplicationHistoryAction action,
            String stage,
            String createdByLoginId
    ) {
        if (action == ApplicationHistoryAction.ORDER_SHEET_SUBMITTED_TO_PO
                || action == ApplicationHistoryAction.ORDER_SHEET_FINALIZED
                || action == ApplicationHistoryAction.ORDER_SHEET_SIGNED) {
            return "PRESIDING_OFFICER";
        }
        if (action == ApplicationHistoryAction.ORDER_SHEET_DRAFT_SAVED && stage != null && stage.startsWith("PO")) {
            return "PRESIDING_OFFICER";
        }
        return resolveOfficerActorRoleFromLogin(createdByLoginId);
    }

    private static String currentAssigneeRole(FilingApplication app) {
        if (app.getStatus() == ApplicationStatus.DRAFT) {
            return "FILER";
        }
        if (Boolean.TRUE.equals(app.getPoApproved()) || Boolean.TRUE.equals(app.getPoRejected())) {
            return null;
        }
        return Boolean.TRUE.equals(app.getForwardedToPo()) ? "PRESIDING_OFFICER" : "CLERK";
    }

    private static void assignSequenceAndApplicationId(List<ApplicationHistoryResponse> entries, Long applicationId) {
        int seq = 1;
        for (ApplicationHistoryResponse entry : entries) {
            entry.setSequence(seq++);
            if (entry.getApplicationId() == null) {
                entry.setApplicationId(applicationId);
            }
        }
    }

    private void recordApplicationHistory(
            FilingApplication app,
            ApplicationHistoryAction action,
            String remarks,
            String actorRole,
            String actorLoginId
    ) {
        recordApplicationHistory(app, action, remarks, actorRole, actorLoginId, null, null);
    }

    private void recordApplicationHistory(
            FilingApplication app,
            ApplicationHistoryAction action,
            String remarks,
            String actorRole,
            String actorLoginId,
            Long caseId,
            String caseNo
    ) {
        ApplicationHistory row = new ApplicationHistory();
        row.setApplication(app);
        row.setAction(action);
        row.setRemarks(trimToNull(remarks));
        row.setActorRole(trimToNull(actorRole));
        row.setActorLoginId(requiredText(actorLoginId, "actorLoginId"));
        row.setApplicationNo(trimToNull(app.getApplicationNo()));
        row.setCaseId(caseId);
        row.setCaseNo(trimToNull(caseNo));
        row.setProcessingStage(deriveProcessingStage(app));
        row.setApplicationStatus(app.getStatus());
        applicationHistoryRepository.save(row);
    }

    private ApplicationHistoryResponse toApplicationHistoryResponse(ApplicationHistory row) {
        ApplicationHistoryResponse out = new ApplicationHistoryResponse();
        out.setHistoryId(row.getId());
        Long appId = row.getApplication() != null ? row.getApplication().getId() : null;
        out.setApplicationId(appId);
        out.setAction(row.getAction() != null ? row.getAction().name() : null);
        out.setActionLabel(actionLabel(row.getAction()));
        out.setRemarks(row.getRemarks());
        out.setActorRole(row.getActorRole());
        out.setActorRoleLabel(actorRoleLabel(row.getActorRole()));
        out.setActorLoginId(row.getActorLoginId());
        out.setApplicationNo(row.getApplicationNo());
        out.setStatus(row.getApplicationStatus() != null ? row.getApplicationStatus().name() : null);
        out.setCaseId(row.getCaseId());
        out.setCaseNo(row.getCaseNo());
        out.setProcessingStage(row.getProcessingStage());
        out.setProcessingStageLabel(processingStageLabel(row.getProcessingStage()));
        out.setCreatedAt(row.getCreatedAt());
        out.setSynthetic(false);
        out.setPhase("FILING");
        return out;
    }

    private static String actionLabel(ApplicationHistoryAction action) {
        if (action == null) {
            return "";
        }
        return switch (action) {
            case DRAFT_SAVED -> "Draft saved";
            case SUBMITTED -> "Application submitted";
            case FORWARDED_TO_PO -> "Forwarded to Presiding Officer";
            case RETURNED_TO_CLERK -> "Returned to clerk";
            case PO_REJECTED -> "Rejected by Presiding Officer";
            case CASE_REGISTERED -> "Case registered";
            case HEARING_SCHEDULED -> "Hearing scheduled";
            case NEXT_HEARING_ADJOURN -> "Next hearing scheduled after adjourn";
            case NOTICE_DRAFTED -> "Notice drafted";
            case NOTICE_IN_PO_SCRUTINY -> "Notice under PO scrutiny";
            case NOTICE_FINALIZED -> "Notice finalized";
            case NOTICE_SIGNED -> "Notice digitally signed";
            case NOTICE_SERVED -> "Notice served";
            case ORDER_SHEET_DRAFT_SAVED -> "Order sheet draft saved";
            case ORDER_SHEET_SUBMITTED_TO_PO -> "Order sheet submitted to PO";
            case ORDER_SHEET_FINALIZED -> "Order sheet finalized";
            case ORDER_SHEET_SIGNED -> "Order sheet signed for hearing";
            case JUDGMENT_PO_DRAFT_SAVED -> "Judgment draft saved by PO";
            case JUDGMENT_SENT_TO_CLERK -> "Judgment sent to clerk for editing";
            case JUDGMENT_DRAFT_SAVED -> "Judgment draft saved by clerk";
            case JUDGMENT_SUBMITTED_TO_PO -> "Judgment submitted to PO";
            case JUDGMENT_FINALIZED -> "Judgment finalized";
            case JUDGMENT_PUBLISHED -> "Judgment published";
        };
    }

    /**
     * Reconstructs a readable timeline for applications created before history logging existed.
     */
    private List<ApplicationHistoryResponse> buildSyntheticApplicationHistory(FilingApplication app) {
        List<ApplicationHistoryResponse> out = new ArrayList<>();
        long seq = 0L;

        String filerLogin = resolveFilerLoginId(app);
        if (app.getCreatedAt() != null) {
            out.add(syntheticHistoryRow(++seq, ApplicationHistoryAction.DRAFT_SAVED, app.getCreatedAt(),
                    "Application created", filerActorRole(app), filerLogin, app.getApplicationNo(), app));
        }
        if (app.getSubmittedAt() != null) {
            out.add(syntheticHistoryRow(++seq, ApplicationHistoryAction.SUBMITTED, app.getSubmittedAt(),
                    "Application submitted", filerActorRole(app), filerLogin, app.getApplicationNo(), app));
        }
        if (hasText(app.getClerkRemarks()) || Boolean.TRUE.equals(app.getForwardedToPo())) {
            Instant at = app.getLastActionAt() != null ? app.getLastActionAt() : app.getSubmittedAt();
            out.add(syntheticHistoryRow(++seq, ApplicationHistoryAction.FORWARDED_TO_PO, at,
                    app.getClerkRemarks(), "CLERK", null, app.getApplicationNo(), app));
        }
        if (Boolean.TRUE.equals(app.getSentBackToClerk()) && hasText(app.getPoRemarks())) {
            Instant at = app.getLastActionAt() != null ? app.getLastActionAt() : app.getSubmittedAt();
            out.add(syntheticHistoryRow(++seq, ApplicationHistoryAction.RETURNED_TO_CLERK, at,
                    app.getPoRemarks(), "PRESIDING_OFFICER", app.getApprovedByOfficerLoginId(), app.getApplicationNo(), app));
        }
        if (Boolean.TRUE.equals(app.getPoRejected())) {
            Instant at = app.getLastActionAt() != null ? app.getLastActionAt() : app.getSubmittedAt();
            out.add(syntheticHistoryRow(++seq, ApplicationHistoryAction.PO_REJECTED, at,
                    app.getPoRemarks(), "PRESIDING_OFFICER", app.getApprovedByOfficerLoginId(), app.getApplicationNo(), app));
        }
        if (app.getRegisteredCaseId() != null || Boolean.TRUE.equals(app.getPoApproved())) {
            Instant at = app.getApprovedAt() != null ? app.getApprovedAt() : app.getLastActionAt();
            String caseNo = null;
            if (app.getRegisteredCaseId() != null) {
                CaseRegistry caseRow = caseRegistryRepository.findById(app.getRegisteredCaseId()).orElse(null);
                caseNo = caseRow != null ? caseRow.getCaseNo() : null;
            }
            ApplicationHistoryResponse row = syntheticHistoryRow(++seq, ApplicationHistoryAction.CASE_REGISTERED, at,
                    "Case registered", "PRESIDING_OFFICER", app.getApprovedByOfficerLoginId(), app.getApplicationNo(), app);
            row.setCaseId(app.getRegisteredCaseId());
            row.setCaseNo(caseNo);
            out.add(row);
        }
        return out;
    }

    private static ApplicationHistoryResponse syntheticHistoryRow(
            long historyId,
            ApplicationHistoryAction action,
            Instant createdAt,
            String remarks,
            String actorRole,
            String actorLoginId,
            String applicationNo,
            FilingApplication app
    ) {
        String stage = syntheticProcessingStageForAction(action);
        ApplicationHistoryResponse out = new ApplicationHistoryResponse();
        out.setHistoryId(historyId);
        out.setApplicationId(app.getId());
        out.setAction(action.name());
        out.setActionLabel(actionLabel(action));
        out.setRemarks(remarks);
        out.setActorRole(actorRole);
        out.setActorRoleLabel(actorRoleLabel(actorRole));
        out.setActorLoginId(actorLoginId);
        out.setApplicationNo(applicationNo);
        out.setStatus(syntheticStatusForAction(action, app));
        out.setProcessingStage(stage);
        out.setProcessingStageLabel(processingStageLabel(stage));
        out.setCreatedAt(createdAt);
        out.setSynthetic(true);
        out.setPhase("FILING");
        return out;
    }

    private static String syntheticProcessingStageForAction(ApplicationHistoryAction action) {
        return switch (action) {
            case DRAFT_SAVED -> "DRAFT";
            case SUBMITTED, RETURNED_TO_CLERK -> "CLERK_DRAFT_REVIEW";
            case FORWARDED_TO_PO -> "PO_UNDER_REVIEW";
            case PO_REJECTED -> "PO_REJECTED";
            case CASE_REGISTERED -> "PO_APPROVED_CASE_CREATED";
            default -> "CASE_PROCEEDINGS";
        };
    }

    private static String syntheticStatusForAction(ApplicationHistoryAction action, FilingApplication app) {
        return switch (action) {
            case DRAFT_SAVED -> ApplicationStatus.DRAFT.name();
            case SUBMITTED, FORWARDED_TO_PO, RETURNED_TO_CLERK, PO_REJECTED, CASE_REGISTERED ->
                    app.getStatus() != null ? app.getStatus().name() : ApplicationStatus.SUBMITTED.name();
            default -> app.getStatus() != null ? app.getStatus().name() : ApplicationStatus.SUBMITTED.name();
        };
    }

    private static String processingStageLabel(String stage) {
        if (stage == null || stage.isBlank()) {
            return "";
        }
        return switch (stage) {
            case "DRAFT" -> "Draft";
            case "CLERK_DRAFT_REVIEW" -> "With clerk for review";
            case "PO_UNDER_REVIEW" -> "With Presiding Officer";
            case "PO_SENT_BACK_TO_CLERK" -> "Returned to clerk by PO";
            case "PO_REJECTED" -> "Rejected by Presiding Officer";
            case "PO_APPROVED_CASE_CREATED" -> "Case registered";
            case "CASE_PROCEEDINGS" -> "Case proceedings";
            default -> stage.replace('_', ' ').toLowerCase(Locale.ROOT);
        };
    }

    private static String actorRoleLabel(String role) {
        if (role == null || role.isBlank()) {
            return "";
        }
        return switch (role) {
            case "ADVOCATE" -> "Advocate";
            case "PARTY_IN_PERSON" -> "Party in person";
            case "PARTY_IN_PERSON_REPRESENTATIVE" -> "Party representative";
            case "CLERK" -> "Clerk";
            case "PRESIDING_OFFICER" -> "Presiding Officer";
            case "FILER" -> "Filer";
            default -> role.replace('_', ' ').toLowerCase(Locale.ROOT);
        };
    }

    private static String filerActorRole(FilingApplication app) {
        if (app.getFiledByAdvocate() != null) {
            return "ADVOCATE";
        }
        if (app.getFiledByParty() != null && app.getFiledByParty().getRole() != null) {
            return app.getFiledByParty().getRole().name();
        }
        return "PARTY_IN_PERSON";
    }

    private static String resolveFilerLoginId(FilingApplication app) {
        if (app.getFiledByAdvocate() != null && hasText(app.getFiledByAdvocate().getEmail())) {
            return app.getFiledByAdvocate().getEmail();
        }
        if (app.getFiledByParty() != null && hasText(app.getFiledByParty().getEmail())) {
            return app.getFiledByParty().getEmail();
        }
        return null;
    }

    private static String buildTemporaryCaseNumber() {
        long now = System.currentTimeMillis();
        long rnd = Math.abs(java.util.concurrent.ThreadLocalRandom.current().nextInt(1000, 9999));
        return String.format(Locale.ROOT, "TMP/%d/%d", now, rnd);
    }

    private static OfficerCaseApprovalResponse buildApprovalResponse(
            FilingApplication app,
            CaseRegistry row,
            String message
    ) {
        OfficerCaseApprovalResponse out = new OfficerCaseApprovalResponse();
        out.setApplicationId(app.getId());
        out.setApplicationNo(app.getApplicationNo());
        out.setCaseId(row.getId());
        out.setCaseNo(row.getCaseNo());
        out.setMessage(message);
        return out;
    }

}
