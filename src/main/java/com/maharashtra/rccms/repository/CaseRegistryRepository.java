package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.caseflow.CaseRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CaseRegistryRepository extends JpaRepository<CaseRegistry, Long> {
    Optional<CaseRegistry> findByFilingApplicationId(Long filingApplicationId);
    Optional<CaseRegistry> findByIdAndOfficeId(Long id, Long officeId);
    List<CaseRegistry> findByOfficeIdOrderByApprovedAtDescIdDesc(Long officeId);
    List<CaseRegistry> findByOfficeIdAndStatusIgnoreCaseOrderByApprovedAtDescIdDesc(Long officeId, String status);
}
