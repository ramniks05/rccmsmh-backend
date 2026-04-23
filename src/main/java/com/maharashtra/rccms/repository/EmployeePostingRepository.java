package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.EmployeePosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeePostingRepository extends JpaRepository<EmployeePosting, Long> {
    List<EmployeePosting> findByEmployeeIdOrderByFromDateDesc(Long employeeId);
    Optional<EmployeePosting> findFirstByEmployeeIdAndToDateIsNull(Long employeeId);
}

