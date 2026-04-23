package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

