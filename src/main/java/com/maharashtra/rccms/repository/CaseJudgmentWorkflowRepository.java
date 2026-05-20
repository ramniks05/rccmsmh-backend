package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.caseflow.CaseJudgmentWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaseJudgmentWorkflowRepository extends JpaRepository<CaseJudgmentWorkflow, Long> {
    Optional<CaseJudgmentWorkflow> findByCaseRegistryId(Long caseId);
}
