package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.caseflow.CaseHearing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CaseHearingRepository extends JpaRepository<CaseHearing, Long> {
    List<CaseHearing> findByCaseRegistryIdOrderByHearingNoAsc(Long caseId);
    Optional<CaseHearing> findFirstByCaseRegistryIdOrderByHearingNoDesc(Long caseId);
    List<CaseHearing> findByCaseRegistryOfficeIdAndHearingDateOrderByCaseRegistryIdAscHearingNoAsc(Long officeId, LocalDate hearingDate);
}
