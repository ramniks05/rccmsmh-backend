package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.filing.ApplicationDocumentChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationDocumentChecklistRepository extends JpaRepository<ApplicationDocumentChecklist, Long> {

    List<ApplicationDocumentChecklist> findByApplicationIdOrderByDisplayOrderAscIdAsc(Long applicationId);

    void deleteByApplicationId(Long applicationId);
}
