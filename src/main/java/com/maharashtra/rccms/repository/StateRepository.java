package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long> {
    Optional<State> findFirstByLgdCode(String lgdCode);

    Optional<State> findFirstByNameIgnoreCase(String name);
}

