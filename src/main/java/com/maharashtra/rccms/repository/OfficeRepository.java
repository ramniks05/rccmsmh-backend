package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfficeRepository extends JpaRepository<Office, Long> {
    List<Office> findByOfficeTypeIdOrderByNameAsc(Long officeTypeId);

    List<Office> findByDepartmentIdAndOfficeTypeIdOrderByNameAsc(Long departmentId, Long officeTypeId);

    List<Office> findByDepartmentIdOrderByNameAsc(Long departmentId);

    List<Office> findByOfficeType_BoundaryLevelOrderByNameAsc(String boundaryLevel);

    List<Office> findByDepartmentIdAndOfficeType_BoundaryLevelOrderByNameAsc(Long departmentId, String boundaryLevel);

    Optional<Office> findFirstByOfficeCodeIgnoreCase(String officeCode);

    /** {@code short_name} may store ePCIS sub-office code; {@code office_code} is the primary routing code. */
    Optional<Office> findFirstByShortNameIgnoreCase(String shortName);
}
