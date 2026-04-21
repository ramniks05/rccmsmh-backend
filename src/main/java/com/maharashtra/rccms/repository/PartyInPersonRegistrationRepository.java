package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.PartyInPersonRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyInPersonRegistrationRepository extends JpaRepository<PartyInPersonRegistration, Long> {
    boolean existsByEmail(String email);
    Optional<PartyInPersonRegistration> findByEmail(String email);
}
