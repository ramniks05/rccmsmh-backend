package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflowHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseJudgmentWorkflowHistoryRepository extends JpaRepository<CaseJudgmentWorkflowHistory, Long> {
    List<CaseJudgmentWorkflowHistory> findByCaseRegistryIdOrderByCreatedAtAscIdAsc(Long caseId);
}
