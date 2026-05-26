package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.DocumentTypeMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentTypeMappingRepository extends JpaRepository<DocumentTypeMapping, Long> {
    List<DocumentTypeMapping> findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(Long caseCategoryId, Long subjectId);

    List<DocumentTypeMapping> findByCaseCategoryIdOrderBySubjectIdAscDisplayOrderAsc(Long caseCategoryId);

    long countByDocumentTypeId(Long documentTypeId);

    void deleteByCaseCategoryIdAndSubjectId(Long caseCategoryId, Long subjectId);
}

