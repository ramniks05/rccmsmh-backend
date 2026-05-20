package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.filing.*;
import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.model.Employee;
import com.maharashtra.rccms.model.EmployeePosting;
import com.maharashtra.rccms.model.PartyInPersonRegistration;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.dto.caseflow.CaseHearingResponse;
import com.maharashtra.rccms.dto.caseflow.CaseNoticeResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetHistoryResponse;
import com.maharashtra.rccms.model.caseflow.CaseHearing;
import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflowStatus;
import com.maharashtra.rccms.model.caseflow.CaseNotice;
import com.maharashtra.rccms.model.caseflow.CaseNoticeStatus;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheetHistory;
import com.maharashtra.rccms.model.filing.*;
import com.maharashtra.rccms.model.master.*;
import com.maharashtra.rccms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
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
    private final CaseOrderSheetHistoryRepository caseOrderSheetHistoryRepository;
    private final CaseJudgmentWorkflowRepository caseJudgmentWorkflowRepository;

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
            CaseOrderSheetHistoryRepository caseOrderSheetHistoryRepository,
            CaseJudgmentWorkflowRepository caseJudgmentWorkflowRepository
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
        this.caseOrderSheetHistoryRepository = caseOrderSheetHistoryRepository;
        this.caseJudgmentWorkflowRepository = caseJudgmentWorkflowRepository;
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

        FilingApplication entity = locateOrCreate(payload.getApplicationId(), clientRef);

        CaseCategory category = caseCategoryRepository.findById(requiredId(payload.getCaseCategoryId(), "caseCategoryId"))
                .orElseThrow(() -> new IllegalArgumentException("Invalid caseCategoryId."));

        if (entity.getId() != null && payload.getForm() == null) {
            throw new IllegalArgumentException("Nested form envelope is required when updating an existing application.");
        }

        if (entity.getId() != null) {
            assertOwnership(entity, advocateFiler, partyFiler);
        }

        clearChildren(entity);

        entity.setCaseCategory(category);
        entity.setStatus(status);
        if (clientRef != null) {
            entity.setClientApplicationRef(clientRef);
        }
        attachFiler(entity, advocateFiler, partyFiler);

        ApplicationFormNestedPayload form = payload.getForm();
        if (form != null) {
            applyFormHeader(entity, form);
            applyApplicants(entity, form.getApplicants(), status == ApplicationStatus.SUBMITTED);
            applyRespondents(entity, form.getRespondents(), status == ApplicationStatus.SUBMITTED);
            List<VakalatnamaGroupPayload> vakCombined = combineVakalatnama(
                    payload.getVakalatnamaAssignments(),
                    form.getVakalatnamaAssignments());
            applyVakalatnamaGroups(entity, vakCombined, principal.getName());
        } else {
            applyVakalatnamaGroups(entity, payload.getVakalatnamaAssignments(), principal.getName());
        }

        ApplicationDisputedOrderPayload mergedDisputed = mergeDisputedEnvelope(payload);
        applyDisputedOrder(entity, mergedDisputed);

        applyDisputedLands(entity, payload.getDisputedLands());
        fillOfficeFromDisputedLandsIfMissing(entity);
        applyAttachments(entity, payload.getAttachments(), principal.getName());

        if (status == ApplicationStatus.SUBMITTED) {
            validateSubmission(entity);
        }

        entity = filingApplicationRepository.save(entity);
        filingApplicationRepository.flush();
        entity = ensureApplicationNumber(entity);
        entity = ensureInitialClerkFlagsOnSubmit(entity, principal.getName());

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
        app.setForwardedToPo(true);
        app.setSentBackToClerk(false);
        app.setClerkRemarks(remarks);
        app.setLastActionByRole("CLERK");
        app.setLastActionAt(Instant.now());
        filingApplicationRepository.save(app);
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
        app.setForwardedToPo(false);
        app.setSentBackToClerk(true);
        app.setPoRemarks(remarks);
        app.setLastActionByRole("PRESIDING_OFFICER");
        app.setLastActionAt(Instant.now());
        filingApplicationRepository.save(app);
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
        app.setPoRejected(true);
        app.setForwardedToPo(false);
        app.setSentBackToClerk(false);
        app.setPoRemarks(remarks);
        app.setLastActionByRole("PRESIDING_OFFICER");
        app.setLastActionAt(Instant.now());
        filingApplicationRepository.save(app);
        return buildActionResponse(app, "Application rejected by PO.");
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
        return out;
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
        return out;
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
        String login = normalizeLogin(principal.getName());

        FilingApplication app = filingApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));
        assertPartyOwnership(app, login);

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
                caseId != null ? "CASE_PROCEEDINGS" : null,
                null
        );
        application.setNotices(Collections.emptyList());

        PartyApplicationPreviewResponse out = new PartyApplicationPreviewResponse();
        out.setApplication(application);

        if (caseId != null) {
            out.setNotices(loadPartyVisibleNotices(caseId));
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
        if (o.getNotice9Resolved() == null && p.getNotice9Resolved() != null) {
            o.setNotice9Resolved(p.getNotice9Resolved());
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

    private static void clearChildren(FilingApplication entity) {
        entity.setDisputedOrder(null);
        entity.getApplicants().clear();
        entity.getRespondents().clear();
        entity.getVakalatnamaGroups().clear();
        entity.getDisputedLands().clear();
        entity.getAttachments().clear();
    }

    private FilingApplication locateOrCreate(Long applicationId, UUID clientRef) {
        if (applicationId != null) {
            return filingApplicationRepository.findById(applicationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid applicationId."));
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
        Subject subject = resolveSubject(form.getSubjectId());
        entity.setSubject(subject);
        entity.setApplicationDescription(trimToNull(form.getApplicationDescription()));

        entity.setDistrict(resolveDistrict(form.getDistrictId()));
        entity.setSubdistrict(resolveSubdistrict(form.getSubdistrictId()));
        entity.setTaluka(resolveTaluka(form.getTalukaId()));
        Office office = resolveOffice(form.getOfficeId(), form.getOfficeCode());
        entity.setOffice(office);
        Act act = resolveAct(form.getActId(), form.getActCode());
        entity.setAct(act);

        normalizeSection(form, entity, act);

        entity.setMutationYear(form.getMutationYear());
        entity.setMutationTypeFilter(trimToNull(form.getMutationTypeFilter()));
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

    private void normalizeSection(ApplicationFormNestedPayload form, FilingApplication entity, Act resolvedAct) {
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
                || ((disputed.getSearchMode() != null && disputed.getSearchMode().name().equalsIgnoreCase("INWARD_NUMBER"))
                    && hasText(trimToNull(disputed.getSearchValue())));
        boolean hasSearchSignal = disputed.getMutationSearched() != null || disputed.getMutationFound() != null
                || disputed.getSearchMode() != null || hasText(trimToNull(disputed.getSearchValue()));
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

        ord.setSearchMode(parseEnumQuiet(DisputedOrderSearchMode.class, p.getSearchMode()));
        ord.setSearchValue(trimToNull(p.getSearchValue()));
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

        Notice9ResolvedPayload n = p.getNotice9Resolved();
        if (n != null) {
            ord.setNotice9Available(n.getAvailable());
            ord.setNotice9SourceKind(parseEnumQuiet(Notice9SourceKind.class, n.getSourceKind()));
            // Notice-9 URL/data is intentionally not persisted to avoid oversized varchar payloads.
            ord.setNotice9Url(null);
            ord.setNotice9PreviewKind(trimToNull(n.getPreviewKind()));
        }
    }

    /**
     * @return true when the whole disputed-order block should be omitted
     */
    private static boolean isBlankDisputedPayload(ApplicationDisputedOrderPayload p) {
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
        if (p.getNotice9Resolved() != null) {
            Notice9ResolvedPayload n = p.getNotice9Resolved();
            if (n.getAvailable() != null || hasText(n.getSourceKind()) || hasText(n.getPreviewKind())) {
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
            row.setKind(requiredEnum(ApplicationAttachmentKind.class, ap.getKind(), "attachment kind"));
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

    private static OfficerApplicationDetailResponse toOfficerApplicationDetailResponse(
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
        out.setForm(toApplicationFormPayload(app));

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
        return out;
    }

    private static ApplicationFormNestedPayload toApplicationFormPayload(FilingApplication app) {
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
        return form;
    }

    private static ApplicationDisputedOrderPayload toDisputedOrderPayload(ApplicationDisputedOrder ord) {
        ApplicationDisputedOrderPayload dto = new ApplicationDisputedOrderPayload();
        dto.setSearchMode(ord.getSearchMode() != null ? ord.getSearchMode().name() : null);
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

        Notice9ResolvedPayload n = new Notice9ResolvedPayload();
        n.setAvailable(ord.getNotice9Available());
        n.setSourceKind(ord.getNotice9SourceKind() != null ? ord.getNotice9SourceKind().name() : null);
        n.setPreviewKind(ord.getNotice9PreviewKind());
        n.setUrl(null);
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

    private static List<DisputedLandPayload> toDisputedLandPayloads(List<ApplicationDisputedLand> rows) {
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
