package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByStateIdOrderByNameAsc(Long stateId);

    List<District> findByStateIdAndDivisionCodeOrderByNameAsc(Long stateId, String divisionCode);

    Optional<District> findFirstByLgdCode(String lgdCode);

    Optional<District> findFirstByLgdCodeAndState_LgdCode(String lgdCode, String stateLgdCode);
}
