package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findFirstByEmailIgnoreCase(String email);
    Optional<Employee> findFirstByEmployeeCodeIgnoreCase(String employeeCode);
}

