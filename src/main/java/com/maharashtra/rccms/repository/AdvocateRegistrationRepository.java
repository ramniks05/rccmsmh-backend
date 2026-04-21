package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.AdvocateRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdvocateRegistrationRepository extends JpaRepository<AdvocateRegistration, Long> {
    boolean existsByEmail(String email);
    Optional<AdvocateRegistration> findByEmail(String email);
}
