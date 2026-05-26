package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.caseflow.HearingAttendanceEntryRequest;
import com.maharashtra.rccms.dto.caseflow.HearingAttendanceItemResponse;
import com.maharashtra.rccms.dto.caseflow.HearingAttendanceResponse;
import com.maharashtra.rccms.dto.caseflow.HearingAttendanceSaveRequest;
import com.maharashtra.rccms.model.caseflow.CaseHearing;
import com.maharashtra.rccms.model.caseflow.CaseHearingAttendance;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.model.caseflow.HearingPartyType;
import com.maharashtra.rccms.model.filing.ApplicationApplicant;
import com.maharashtra.rccms.model.filing.ApplicationRespondent;
import com.maharashtra.rccms.model.filing.FilingApplication;
import com.maharashtra.rccms.repository.CaseHearingAttendanceRepository;
import com.maharashtra.rccms.repository.FilingApplicationRepository;
import com.maharashtra.rccms.workflow.config.CaseWorkflowDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class CaseHearingAttendanceService {

    private final CaseHearingAttendanceRepository attendanceRepository;
    private final FilingApplicationRepository filingApplicationRepository;
    private final WorkflowPolicyService workflowPolicyService;

    public CaseHearingAttendanceService(
            CaseHearingAttendanceRepository attendanceRepository,
            FilingApplicationRepository filingApplicationRepository,
            WorkflowPolicyService workflowPolicyService
    ) {
        this.attendanceRepository = attendanceRepository;
        this.filingApplicationRepository = filingApplicationRepository;
        this.workflowPolicyService = workflowPolicyService;
    }

    public boolean isAttendanceRequired(CaseRegistry caseRow) {
        CaseWorkflowDefinition def = workflowPolicyService.definitionFor(caseRow);
        return def.getRoznama() != null && def.getRoznama().isAllowAttendance();
    }

    @Transactional(readOnly = true)
    public HearingAttendanceResponse getAttendance(CaseRegistry caseRow, CaseHearing hearing) {
        return buildResponse(caseRow, hearing, loadSavedAttendance(hearing.getId()));
    }

    @Transactional
    public HearingAttendanceResponse saveAttendance(
            CaseRegistry caseRow,
            CaseHearing hearing,
            HearingAttendanceSaveRequest request,
            String login
    ) {
        if (request == null || request.getEntries() == null || request.getEntries().isEmpty()) {
            throw new IllegalArgumentException("At least one attendance entry is required.");
        }
        FilingApplication app = loadApplication(caseRow);
        Map<String, MandatoryParty> mandatoryParties = mandatoryPartyIndex(app);
        Map<String, CaseHearingAttendance> existing = loadSavedAttendance(hearing.getId());

        Set<String> seenMandatory = new HashSet<>();
        for (HearingAttendanceEntryRequest entry : request.getEntries()) {
            HearingPartyType partyType = parsePartyType(entry.getPartyType());
            if (entry.getPresent() == null) {
                throw new IllegalArgumentException("present is required for each attendance entry (true or false).");
            }

            if (partyType == HearingPartyType.OTHER) {
                saveOtherParty(hearing, entry, login, existing);
                continue;
            }

            if (entry.getPartyRefId() == null) {
                throw new IllegalArgumentException("partyRefId is required for " + partyType.name() + " attendance.");
            }
            String slotKey = slotKey(partyType, entry.getPartyRefId(), null);
            MandatoryParty mandatory = mandatoryParties.get(slotKey);
            if (mandatory == null) {
                throw new IllegalArgumentException("Unknown party for this case: " + slotKey);
            }
            seenMandatory.add(slotKey);
            upsertAttendance(hearing, existing, slotKey, partyType, entry.getPartyRefId(),
                    mandatory.name(), mandatory.lineNo(), entry.getPresent(), login);
        }

        for (String slotKey : mandatoryParties.keySet()) {
            if (!seenMandatory.contains(slotKey)) {
                MandatoryParty party = mandatoryParties.get(slotKey);
                throw new IllegalArgumentException(
                        "Attendance is mandatory for all parties. Missing: " + party.name()
                );
            }
        }

        return buildResponse(caseRow, hearing, loadSavedAttendance(hearing.getId()));
    }

    @Transactional
    public void saveAttendanceIfProvided(
            CaseRegistry caseRow,
            CaseHearing hearing,
            List<HearingAttendanceEntryRequest> entries,
            String login
    ) {
        if (entries == null || entries.isEmpty()) {
            return;
        }
        HearingAttendanceSaveRequest request = new HearingAttendanceSaveRequest();
        request.setEntries(entries);
        saveAttendance(caseRow, hearing, request, login);
    }

    public void assertAttendanceCompleteIfRequired(CaseRegistry caseRow, CaseHearing hearing) {
        if (!isAttendanceRequired(caseRow)) {
            return;
        }
        HearingAttendanceResponse view = buildResponse(caseRow, hearing, loadSavedAttendance(hearing.getId()));
        if (!view.isAttendanceComplete()) {
            throw new IllegalArgumentException(
                    "Mark attendance for all applicants and respondents before signing roznamma."
            );
        }
    }

    public void enrichRoznamaResponse(
            com.maharashtra.rccms.dto.caseflow.RoznamaResponse out,
            CaseRegistry caseRow,
            CaseHearing hearing
    ) {
        HearingAttendanceResponse attendance = getAttendance(caseRow, hearing);
        out.setAttendanceRequired(attendance.isAttendanceRequired());
        out.setAttendanceComplete(attendance.isAttendanceComplete());
        out.setAttendance(attendance.getEntries());
    }

    private HearingAttendanceResponse buildResponse(
            CaseRegistry caseRow,
            CaseHearing hearing,
            Map<String, CaseHearingAttendance> savedBySlot
    ) {
        FilingApplication app = loadApplication(caseRow);
        boolean required = isAttendanceRequired(caseRow);
        List<HearingAttendanceItemResponse> entries = new ArrayList<>();

        for (ApplicationApplicant applicant : sortedApplicants(app)) {
            entries.add(toItemResponse(
                    applicant.getId(),
                    HearingPartyType.APPLICANT,
                    applicant.getName(),
                    applicant.getLineNo(),
                    savedBySlot.get(slotKey(HearingPartyType.APPLICANT, applicant.getId(), null)),
                    true
            ));
        }
        for (ApplicationRespondent respondent : sortedRespondents(app)) {
            entries.add(toItemResponse(
                    respondent.getId(),
                    HearingPartyType.RESPONDENT,
                    respondent.getName(),
                    respondent.getLineNo(),
                    savedBySlot.get(slotKey(HearingPartyType.RESPONDENT, respondent.getId(), null)),
                    true
            ));
        }
        for (CaseHearingAttendance other : savedBySlot.values().stream()
                .filter(row -> row.getPartyType() == HearingPartyType.OTHER)
                .sorted(Comparator.comparing(CaseHearingAttendance::getId))
                .collect(Collectors.toList())) {
            HearingAttendanceItemResponse item = new HearingAttendanceItemResponse();
            item.setAttendanceId(other.getId());
            item.setPartyType(HearingPartyType.OTHER.name());
            item.setOtherPartyKey(extractOtherPartyKey(other.getPartySlotKey()));
            item.setPartyName(other.getPartyName());
            item.setLineNo(other.getLineNo());
            item.setPresent(other.getPresent());
            item.setMandatory(false);
            item.setUpdatedAt(other.getUpdatedAt());
            entries.add(item);
        }

        HearingAttendanceResponse out = new HearingAttendanceResponse();
        out.setCaseId(caseRow.getId());
        out.setHearingId(hearing.getId());
        out.setAttendanceRequired(required);
        out.setAttendanceComplete(required && isMandatoryComplete(entries));
        out.setEntries(entries);
        return out;
    }

    private static boolean isMandatoryComplete(List<HearingAttendanceItemResponse> entries) {
        List<HearingAttendanceItemResponse> mandatory = entries.stream()
                .filter(HearingAttendanceItemResponse::isMandatory)
                .toList();
        if (mandatory.isEmpty()) {
            return true;
        }
        return mandatory.stream().noneMatch(entry -> entry.getPresent() == null);
    }

    private static HearingAttendanceItemResponse toItemResponse(
            Long partyRefId,
            HearingPartyType partyType,
            String partyName,
            Integer lineNo,
            CaseHearingAttendance saved,
            boolean mandatory
    ) {
        HearingAttendanceItemResponse item = new HearingAttendanceItemResponse();
        item.setPartyType(partyType.name());
        item.setPartyRefId(partyRefId);
        item.setPartyName(partyName);
        item.setLineNo(lineNo);
        item.setMandatory(mandatory);
        if (saved != null) {
            item.setAttendanceId(saved.getId());
            item.setPresent(saved.getPresent());
            item.setUpdatedAt(saved.getUpdatedAt());
        }
        return item;
    }

    private void saveOtherParty(
            CaseHearing hearing,
            HearingAttendanceEntryRequest entry,
            String login,
            Map<String, CaseHearingAttendance> existing
    ) {
        String partyName = trimToNull(entry.getPartyName());
        if (partyName == null) {
            throw new IllegalArgumentException("partyName is required for OTHER attendance entries.");
        }
        String otherKey = trimToNull(entry.getOtherPartyKey());
        if (otherKey == null) {
            otherKey = UUID.randomUUID().toString();
        }
        String slotKey = slotKey(HearingPartyType.OTHER, null, otherKey);
        upsertAttendance(hearing, existing, slotKey, HearingPartyType.OTHER, null,
                partyName, null, entry.getPresent(), login);
    }

    private void upsertAttendance(
            CaseHearing hearing,
            Map<String, CaseHearingAttendance> existing,
            String slotKey,
            HearingPartyType partyType,
            Long partyRefId,
            String partyName,
            Integer lineNo,
            Boolean present,
            String login
    ) {
        CaseHearingAttendance row = existing.get(slotKey);
        if (row == null) {
            row = new CaseHearingAttendance();
            row.setCaseHearing(hearing);
            row.setPartySlotKey(slotKey);
        }
        row.setPartyType(partyType);
        row.setPartyRefId(partyRefId);
        row.setPartyName(partyName);
        row.setLineNo(lineNo);
        row.setPresent(Boolean.TRUE.equals(present));
        row.setMarkedByLoginId(login);
        attendanceRepository.save(row);
        existing.put(slotKey, row);
    }

    private Map<String, CaseHearingAttendance> loadSavedAttendance(Long hearingId) {
        Map<String, CaseHearingAttendance> out = new HashMap<>();
        for (CaseHearingAttendance row : attendanceRepository.findByCaseHearingIdOrderByPartyTypeAscLineNoAscIdAsc(hearingId)) {
            out.put(row.getPartySlotKey(), row);
        }
        return out;
    }

    private FilingApplication loadApplication(CaseRegistry caseRow) {
        return filingApplicationRepository.findById(caseRow.getFilingApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("Filing application not found for case."));
    }

    private static Map<String, MandatoryParty> mandatoryPartyIndex(FilingApplication app) {
        Map<String, MandatoryParty> out = new HashMap<>();
        for (ApplicationApplicant applicant : sortedApplicants(app)) {
            out.put(
                    slotKey(HearingPartyType.APPLICANT, applicant.getId(), null),
                    new MandatoryParty(applicant.getName(), applicant.getLineNo())
            );
        }
        for (ApplicationRespondent respondent : sortedRespondents(app)) {
            out.put(
                    slotKey(HearingPartyType.RESPONDENT, respondent.getId(), null),
                    new MandatoryParty(respondent.getName(), respondent.getLineNo())
            );
        }
        return out;
    }

    private static List<ApplicationApplicant> sortedApplicants(FilingApplication app) {
        return app.getApplicants().stream()
                .sorted(Comparator.comparing(ApplicationApplicant::getLineNo, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(ApplicationApplicant::getId))
                .collect(Collectors.toList());
    }

    private static List<ApplicationRespondent> sortedRespondents(FilingApplication app) {
        return app.getRespondents().stream()
                .sorted(Comparator.comparing(ApplicationRespondent::getLineNo, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(ApplicationRespondent::getId))
                .collect(Collectors.toList());
    }

    private static HearingPartyType parsePartyType(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("partyType is required.");
        }
        try {
            return HearingPartyType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid partyType: " + raw);
        }
    }

    static String slotKey(HearingPartyType partyType, Long partyRefId, String otherPartyKey) {
        Objects.requireNonNull(partyType, "partyType");
        return switch (partyType) {
            case APPLICANT -> "APPLICANT_" + partyRefId;
            case RESPONDENT -> "RESPONDENT_" + partyRefId;
            case OTHER -> "OTHER_" + Objects.requireNonNull(otherPartyKey, "otherPartyKey");
        };
    }

    private static String extractOtherPartyKey(String slotKey) {
        if (slotKey == null || !slotKey.startsWith("OTHER_")) {
            return null;
        }
        return slotKey.substring("OTHER_".length());
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private record MandatoryParty(String name, Integer lineNo) {}
}
