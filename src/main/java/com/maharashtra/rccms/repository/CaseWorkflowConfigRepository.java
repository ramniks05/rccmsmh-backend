package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.CaseWorkflowConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaseWorkflowConfigRepository extends JpaRepository<CaseWorkflowConfig, Long> {
    Optional<CaseWorkflowConfig> findByCaseCategoryIdAndActiveTrue(Long caseCategoryId);
    Optional<CaseWorkflowConfig> findByCaseCategoryId(Long caseCategoryId);
}
