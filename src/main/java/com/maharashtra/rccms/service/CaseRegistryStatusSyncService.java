package com.maharashtra.rccms.service;

import com.maharashtra.rccms.model.caseflow.CaseHearing;
import com.maharashtra.rccms.model.caseflow.CaseNoticeStatus;
import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import com.maharashtra.rccms.repository.CaseHearingRepository;
import com.maharashtra.rccms.repository.CaseNoticeRepository;
import com.maharashtra.rccms.repository.CaseRegistryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CaseRegistryStatusSyncService {

    private final CaseRegistryRepository caseRegistryRepository;
    private final CaseHearingRepository caseHearingRepository;
    private final CaseNoticeRepository caseNoticeRepository;

    public CaseRegistryStatusSyncService(
            CaseRegistryRepository caseRegistryRepository,
            CaseHearingRepository caseHearingRepository,
            CaseNoticeRepository caseNoticeRepository
    ) {
        this.caseRegistryRepository = caseRegistryRepository;
        this.caseHearingRepository = caseHearingRepository;
        this.caseNoticeRepository = caseNoticeRepository;
    }

    /**
     * Notice served for the given hearing only. Does not treat an older hearing's served notice
     * as served for a newly scheduled hearing after adjourn.
     */
    public boolean isNoticeServedForCase(Long caseId, CaseHearing hearing) {
        if (hearing == null) {
            return caseId != null
                    && caseNoticeRepository.existsByCaseRegistry_IdAndStatus(caseId, CaseNoticeStatus.SERVED);
        }
        if (Boolean.TRUE.equals(hearing.getNoticeServed())) {
            return true;
        }
        return caseNoticeRepository.existsByHearingIdAndStatus(hearing.getId(), CaseNoticeStatus.SERVED);
    }

    /** Runs in a new transaction so it works when called from read-only inbox/dashboard queries. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncNoticeServedIfNeeded(Long caseId, Long hearingId) {
        if (caseId == null) {
            return;
        }
        CaseRegistry caseRow = caseRegistryRepository.findById(caseId).orElse(null);
        if (caseRow == null || "DISPOSED".equalsIgnoreCase(caseRow.getStatus())) {
            return;
        }
        boolean served = caseNoticeRepository.existsByCaseRegistry_IdAndStatus(caseId, CaseNoticeStatus.SERVED);
        if (!served && hearingId != null) {
            served = caseNoticeRepository.existsByHearingIdAndStatus(hearingId, CaseNoticeStatus.SERVED);
        }
        if (!served || hearingId == null) {
            return;
        }
        CaseHearing hearing = caseHearingRepository.findById(hearingId).orElse(null);
        if (hearing == null) {
            return;
        }
        if (!Boolean.TRUE.equals(hearing.getNoticeServed())) {
            hearing.setNoticeServed(true);
            caseHearingRepository.save(hearing);
        }
        if (Boolean.TRUE.equals(hearing.getNoticeServed())) {
            String current = caseRow.getStatus();
            if (current == null
                    || "ACTIVE".equalsIgnoreCase(current)
                    || "HEARING_SCHEDULED".equalsIgnoreCase(current)) {
                caseRow.setStatus("NOTICE_SERVED");
                caseRegistryRepository.save(caseRow);
            }
        }
    }
}
