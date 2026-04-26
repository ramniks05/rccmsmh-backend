package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfficeRepository extends JpaRepository<Office, Long> {
    List<Office> findByLevelAndLocationIdOrderByNameAsc(String level, Long locationId);
    List<Office> findByDepartmentIdAndLevelAndLocationIdOrderByNameAsc(Long departmentId, String level, Long locationId);
}

