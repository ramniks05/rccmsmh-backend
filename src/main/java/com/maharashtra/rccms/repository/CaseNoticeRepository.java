package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.caseflow.CaseNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CaseNoticeRepository extends JpaRepository<CaseNotice, Long> {
    List<CaseNotice> findByCaseRegistryIdOrderByIdDesc(Long caseId);
    Optional<CaseNotice> findByIdAndCaseRegistryId(Long id, Long caseId);
}
