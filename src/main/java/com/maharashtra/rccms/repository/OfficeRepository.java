package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfficeRepository extends JpaRepository<Office, Long> {
    List<Office> findByLevelAndLocationIdOrderByNameAsc(String level, Long locationId);
    List<Office> findByDepartmentIdAndLevelAndLocationIdOrderByNameAsc(Long departmentId, String level, Long locationId);
    Optional<Office> findFirstByOfficeCodeIgnoreCase(String officeCode);

    /** {@code short_name} may store ePCIS sub-office code; {@code office_code} is the primary routing code. */
    Optional<Office> findFirstByShortNameIgnoreCase(String shortName);
}

