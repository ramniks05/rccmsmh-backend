package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.Act;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActRepository extends JpaRepository<Act, Long> {
    Optional<Act> findFirstByActCodeIgnoreCase(String actCode);
}

