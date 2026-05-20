package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.caseflow.CaseOrderSheetHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CaseOrderSheetHistoryRepository extends JpaRepository<CaseOrderSheetHistory, Long> {
    List<CaseOrderSheetHistory> findByCaseRegistryIdOrderByCreatedAtDesc(Long caseId);

    List<CaseOrderSheetHistory> findByCaseRegistryIdInOrderByCreatedAtDesc(Collection<Long> caseRegistryIds);
}
