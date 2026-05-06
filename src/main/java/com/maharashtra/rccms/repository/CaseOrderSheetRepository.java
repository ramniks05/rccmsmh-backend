package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.caseflow.CaseOrderSheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaseOrderSheetRepository extends JpaRepository<CaseOrderSheet, Long> {
    Optional<CaseOrderSheet> findByCaseRegistryId(Long caseId);
}
