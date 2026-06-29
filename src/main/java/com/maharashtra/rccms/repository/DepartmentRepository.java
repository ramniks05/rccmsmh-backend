package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByStateIdOrderByNameAsc(Long stateId);
}

