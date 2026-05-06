package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.filing.*;
import com.maharashtra.rccms.model.AdvocateRegistration;
import com.maharashtra.rccms.model.Employee;
import com.maharashtra.rccms.model.EmployeePosting;
import com.maharashtra.rccms.model.PartyInPersonRegistration;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.model.filing.*;
import com.maharashtra.rccms.model.master.*;
import com.maharashtra.rccms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.*;

/**
 * Persists the Category 1 objection filing aggregate ({@link FilingApplication}). Case registry / numbering belongs to officer acceptance flows.
 */
@Service
@SuppressWarnings("null")
public class FilingApplicationService {
    private static final long PRESIDING_OFFICER_DESIGNATION_ID = 1L;

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
            EmployeePostingRepository employeePostingRepository
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
            applyRespondents(entity, form.getRespondents());
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

        OfficerApplicationDetailResponse out = new OfficerApplicationDetailResponse();
        out.setApplicationId(app.getId());
        out.setApplicationNo(app.getApplicationNo());
        out.setClientApplicationRef(app.getClientApplicationRef() != null ? app.getClientApplicationRef().toString() : null);
        out.setCaseId(app.getRegisteredCaseId());
        if (app.getRegisteredCaseId() != null) {
            CaseRegistry c = caseRegistryRepository.findById(app.getRegisteredCaseId()).orElse(null);
            out.setCaseNo(c != null ? c.getCaseNo() : null);
        }
        out.setCaseCategoryId(app.getCaseCategory() != null ? app.getCaseCategory().getId() : null);
        out.setCaseCategoryName(app.getCaseCategory() != null ? app.getCaseCategory().getName() : null);
        out.setStatus(app.getStatus() != null ? app.getStatus().name() : null);
        out.setProcessingStage(deriveProcessingStage(app));
        out.setCurrentAssigneeRole(Boolean.TRUE.equals(app.getForwardedToPo()) ? "PRESIDING_OFFICER" : "CLERK");
        out.setOfficeId(app.getOffice() != null ? app.getOffice().getId() : null);
        out.setOfficeName(app.getOffice() != null ? app.getOffice().getName() : null);
        out.setSubjectId(app.getSubject() != null ? app.getSubject().getId() : null);
        out.setSubjectName(app.getSubject() != null ? app.getSubject().getSubjectName() : null);
        out.setApplicationDescription(app.getApplicationDescription());
        out.setCreatedAt(app.getCreatedAt());
        out.setUpdatedAt(app.getUpdatedAt());
        out.setSubmittedAt(app.getSubmittedAt());

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

    private void applyFormHeader(FilingApplication entity, ApplicationFormNestedPayload form) {
        Subject subject = resolveSubject(form.getSubjectId());
        entity.setSubject(subject);
        entity.setApplicationDescription(trimToNull(form.getApplicationDescription()));

        entity.setDistrict(resolveDistrict(form.getDistrictId()));
        entity.setSubdistrict(resolveSubdistrict(form.getSubdistrictId()));
        entity.setTaluka(resolveTaluka(form.getTalukaId()));
        entity.setOffice(resolveOffice(form.getOfficeId()));
        entity.setAct(resolveAct(form.getActId()));

        normalizeSection(form, entity);

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

    private Office resolveOffice(Long id) {
        if (id == null || zeroToNull(id) == null) {
            return null;
        }
        return officeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid officeId."));
    }

    private Act resolveAct(Long id) {
        if (id == null || zeroToNull(id) == null) {
            return null;
        }
        return actRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid actId."));
    }

    private void normalizeSection(ApplicationFormNestedPayload form, FilingApplication entity) {
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
        } else {
            entity.setSection(null);
            entity.setSectionCustomText(null);
        }
    }

    private static void validateSubmission(FilingApplication entity) {
        if (entity.getSubject() == null) {
            throw new IllegalArgumentException("subjectId is required for submission.");
        }
        if (entity.getDistrict() == null) {
            throw new IllegalArgumentException("districtId is required for submission.");
        }
        if (entity.getTaluka() == null) {
            throw new IllegalArgumentException("talukaId is required for submission.");
        }
        if (entity.getOffice() == null) {
            throw new IllegalArgumentException("officeId is required for submission.");
        }
        if (entity.getAct() == null) {
            throw new IllegalArgumentException("actId is required for submission.");
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
        boolean hasSearchSignal = disputed.getMutationSearched() != null || disputed.getMutationFound() != null
                || disputed.getSearchMode() != null || hasText(trimToNull(disputed.getSearchValue()));
        boolean hasApiFields = Boolean.TRUE.equals(disputed.getMutationFound())
                && (hasText(trimToNull(disputed.getInwardNumber()))
                || hasText(trimToNull(disputed.getApplicantName()))
                || hasText(trimToNull(disputed.getMutationType())));
        if (!(hasManual || hasApiFields || hasSearchSignal)) {
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
            row.setName(requiredText(dto.getName(), "Applicant name"));
            row.setMobile(trimToNull(dto.getMobile()));
            row.setAddress(trimToNull(dto.getAddress()));
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

    private void applyRespondents(FilingApplication entity, List<RespondentRowPayload> payloads) {
        if (payloads == null) {
            return;
        }
        int autoLine = 1;
        for (RespondentRowPayload dto : payloads) {
            ApplicationRespondent row = new ApplicationRespondent();
            row.setApplication(entity);
            row.setLineNo(dto.getLineNo() != null ? dto.getLineNo() : autoLine++);
            row.setClientRowKey(trimToNull(dto.getClientRowKey()));
            row.setName(requiredText(dto.getName(), "Respondent name"));
            row.setMobile(trimToNull(dto.getMobile()));
            row.setAddress(trimToNull(dto.getAddress()));
            entity.getRespondents().add(row);
        }
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
            row.setLandType(requiredEnum(DisputedLandType.class, lp.getLandType(), "land type"));
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
            throw new IllegalArgumentException("Invalid clientApplicationRef (UUID expected).");
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
        return officerIsPo ? Boolean.TRUE.equals(app.getForwardedToPo())
                : !Boolean.TRUE.equals(app.getForwardedToPo());
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
            dto.setMobile(row.getMobile());
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
            dto.setName(row.getName());
            dto.setMobile(row.getMobile());
            dto.setAddress(row.getAddress());
            out.add(dto);
        }
        return out;
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
