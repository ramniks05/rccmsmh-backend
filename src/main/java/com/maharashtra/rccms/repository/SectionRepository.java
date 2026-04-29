package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
    long deleteByActId(Long actId);
    java.util.List<Section> findByActId(Long actId);
}

