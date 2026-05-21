package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.filing.ApplicationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationHistoryRepository extends JpaRepository<ApplicationHistory, Long> {

    List<ApplicationHistory> findByApplication_IdOrderByCreatedAtAscIdAsc(Long applicationId);
}
