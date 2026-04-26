package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.DocumentTypeMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentTypeMappingRepository extends JpaRepository<DocumentTypeMapping, Long> {
    List<DocumentTypeMapping> findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(Long caseCategoryId, Long subjectId);
    void deleteByCaseCategoryIdAndSubjectId(Long caseCategoryId, Long subjectId);
}

