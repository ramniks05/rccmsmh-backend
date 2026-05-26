package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.caseflow.CaseNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.maharashtra.rccms.model.caseflow.CaseNoticeStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CaseNoticeRepository extends JpaRepository<CaseNotice, Long> {
    List<CaseNotice> findByCaseRegistryIdOrderByIdDesc(Long caseId);
    Optional<CaseNotice> findByIdAndCaseRegistryId(Long id, Long caseId);
    Optional<CaseNotice> findFirstByHearingIdAndStatusInOrderByIdDesc(Long hearingId, Collection<CaseNoticeStatus> statuses);
    List<CaseNotice> findByHearingIdOrderByIdDesc(Long hearingId);

    boolean existsByHearingIdAndStatus(Long hearingId, CaseNoticeStatus status);

    boolean existsByCaseRegistry_IdAndStatus(Long caseRegistryId, CaseNoticeStatus status);
}
