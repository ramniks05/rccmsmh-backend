package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.caseflow.CaseHearing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CaseHearingRepository extends JpaRepository<CaseHearing, Long> {
    List<CaseHearing> findByCaseRegistryIdOrderByHearingNoAsc(Long caseId);

    List<CaseHearing> findByCaseRegistry_IdInOrderByCaseRegistry_IdAscHearingNoDesc(Collection<Long> caseRegistryIds);
    Optional<CaseHearing> findFirstByCaseRegistryIdOrderByHearingNoDesc(Long caseId);
    List<CaseHearing> findByCaseRegistryOfficeIdAndHearingDateOrderByCaseRegistryIdAscHearingNoAsc(Long officeId, LocalDate hearingDate);

    List<CaseHearing> findByCaseRegistryIdAndHearingDateOrderByHearingNoAsc(Long caseRegistryId, LocalDate hearingDate);

    /** Hearings with a date assigned where notice is not yet served (any hearing date). */
    @Query("""
            SELECT h FROM CaseHearing h
            JOIN h.caseRegistry c
            WHERE c.office.id = :officeId
              AND h.hearingDate IS NOT NULL
              AND (h.noticeServed IS NULL OR h.noticeServed = false)
              AND UPPER(c.status) <> 'DISPOSED'
            ORDER BY h.hearingDate ASC, c.id ASC, h.hearingNo ASC
            """)
    List<CaseHearing> findPendingNoticeServeByOfficeId(@Param("officeId") Long officeId);
}
