package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByStateIdOrderByNameAsc(Long stateId);
    List<District> findByStateIdAndDivisionIdOrderByNameAsc(Long stateId, Long divisionId);
}

