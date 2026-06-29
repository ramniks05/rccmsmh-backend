package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.Division;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DivisionRepository extends JpaRepository<Division, Long> {
    List<Division> findByStateIdOrderByNameAsc(Long stateId);

    Optional<Division> findFirstByStateIdAndDivisionCode(Long stateId, String divisionCode);
}
