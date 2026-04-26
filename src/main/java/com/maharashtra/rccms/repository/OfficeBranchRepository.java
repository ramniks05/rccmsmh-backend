package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.OfficeBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfficeBranchRepository extends JpaRepository<OfficeBranch, Long> {
    List<OfficeBranch> findByOfficeIdOrderByNameAsc(Long officeId);
}

