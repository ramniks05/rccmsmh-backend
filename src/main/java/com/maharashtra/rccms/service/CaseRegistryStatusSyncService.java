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

    public boolean isNoticeServedForCase(Long caseId, CaseHearing hearing) {
        if (hearing != null && Boolean.TRUE.equals(hearing.getNoticeServed())) {
            return true;
        }
        if (hearing != null && caseNoticeRepository.existsByHearingIdAndStatus(hearing.getId(), CaseNoticeStatus.SERVED)) {
            return true;
        }
        return caseId != null && caseNoticeRepository.existsByCaseRegistry_IdAndStatus(caseId, CaseNoticeStatus.SERVED);
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
        if (!served) {
            return;
        }
        if (hearingId != null) {
            caseHearingRepository.findById(hearingId).ifPresent(hearing -> {
                if (!Boolean.TRUE.equals(hearing.getNoticeServed())) {
                    hearing.setNoticeServed(true);
                    caseHearingRepository.save(hearing);
                }
            });
        }
        if (!"NOTICE_SERVED".equalsIgnoreCase(caseRow.getStatus())) {
            caseRow.setStatus("NOTICE_SERVED");
            caseRegistryRepository.save(caseRow);
        }
    }
}
