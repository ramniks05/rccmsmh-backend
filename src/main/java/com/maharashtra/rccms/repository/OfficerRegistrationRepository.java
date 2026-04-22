package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.OfficerRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfficerRegistrationRepository extends JpaRepository<OfficerRegistration, Long> {
    boolean existsByEmail(String email);
    Optional<OfficerRegistration> findByEmail(String email);
}
