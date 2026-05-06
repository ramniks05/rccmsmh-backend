package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.caseflow.CaseHearingResponse;
import com.maharashtra.rccms.dto.caseflow.CaseHearingScheduleRequest;
import com.maharashtra.rccms.dto.caseflow.CaseInboxItemResponse;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentRequest;
import com.maharashtra.rccms.dto.caseflow.CaseJudgmentResponse;
import com.maharashtra.rccms.dto.caseflow.OfficerDashboardResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetHistoryResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetResponse;
import com.maharashtra.rccms.dto.caseflow.CaseOrderSheetUpsertRequest;
import com.maharashtra.rccms.model.Employee;
import com.maharashtra.rccms.model.EmployeePosting;
import com.maharashtra.rccms.model.caseflow.CaseHearing;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheet;
import com.maharashtra.rccms.model.caseflow.CaseOrderSheetHistory;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.repository.CaseHearingRepository;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class CaseProceedingService {

    private final CaseRegistryRepository caseRegistryRepository;
    private final CaseHearingRepository caseHearingRepository;
    private final CaseOrderSheetRepository caseOrderSheetRepository;
    private final CaseOrderSheetHistoryRepository caseOrderSheetHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeePostingRepository employeePostingRepository;
    private final FilingApplicationService filingApplicationService;

    public CaseProceedingService(
            CaseRegistryRepository caseRegistryRepository,
            CaseHearingRepository caseHearingRepository,
            CaseOrderSheetRepository caseOrderSheetRepository,
            CaseOrderSheetHistoryRepository caseOrderSheetHistoryRepository,
            EmployeeRepository employeeRepository,
            EmployeePostingRepository employeePostingRepository,
            FilingApplicationService filingApplicationService
    ) {
        this.caseRegistryRepository = caseRegistryRepository;
        this.caseHearingRepository = caseHearingRepository;
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
        for (CaseRegistry row : rows) {
            if (s == null && "DISPOSED".equalsIgnoreCase(row.getStatus())) {
                continue;
            }
            out.add(toCaseInboxItem(row));
        }
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
        CaseRegistry caseRow = resolveOfficerCase(caseId, login);
        assertNotDisposed(caseRow);

        CaseHearing hearing = null;
        if (request.getHearingId() != null) {
            hearing = caseHearingRepository.findById(request.getHearingId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid hearingId."));
            if (!Objects.equals(hearing.getCaseRegistry().getId(), caseId)) {
                throw new IllegalArgumentException("hearingId does not belong to case.");
            }
            if (!"COMPLETED".equalsIgnoreCase(hearing.getStatus())) {
                hearing.setStatus("COMPLETED");
                caseHearingRepository.save(hearing);
            }
        }

        CaseOrderSheet sheet = caseOrderSheetRepository.findByCaseRegistryId(caseId).orElse(null);
        if (sheet == null) {
            sheet = new CaseOrderSheet();
            sheet.setCaseRegistry(caseRow);
        }
        sheet.setContent(request.getContent().trim());
        sheet.setUpdatedByLoginId(login);
        sheet = caseOrderSheetRepository.save(sheet);

        CaseOrderSheetHistory hist = new CaseOrderSheetHistory();
        hist.setCaseRegistry(caseRow);
        hist.setCaseHearing(hearing);
        hist.setContent(sheet.getContent());
        hist.setRemarks(trimToNull(request.getRemarks()));
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
        String login = normalizeLogin(principal);
        resolveOfficerCase(caseId, login);
        List<CaseOrderSheetHistoryResponse> out = new ArrayList<>();
        for (CaseOrderSheetHistory row : caseOrderSheetHistoryRepository.findByCaseRegistryIdOrderByCreatedAtDesc(caseId)) {
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

    private Long resolveOfficerCurrentOfficeId(String login) {
        Employee employee = resolveOfficerEmployee(login);
        EmployeePosting posting = employeePostingRepository.findFirstByEmployeeIdAndToDateIsNull(employee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Current posting not found for officer."));
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

    private static CaseOrderSheetResponse toOrderSheetResponse(CaseOrderSheet sheet) {
        CaseOrderSheetResponse out = new CaseOrderSheetResponse();
        out.setCaseId(sheet.getCaseRegistry() != null ? sheet.getCaseRegistry().getId() : null);
        out.setCaseNo(sheet.getCaseRegistry() != null ? sheet.getCaseRegistry().getCaseNo() : null);
        out.setContent(sheet.getContent());
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
        out.setRemarks(row.getRemarks());
        out.setCreatedAt(row.getCreatedAt());
        out.setCreatedByLoginId(row.getCreatedByLoginId());
        return out;
    }

    private static CaseInboxItemResponse toCaseInboxItem(CaseRegistry row) {
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
        return out;
    }
}
