package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.master.ScrutinyChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrutinyChecklistItemRepository extends JpaRepository<ScrutinyChecklistItem, Long> {
    List<ScrutinyChecklistItem> findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(Long caseCategoryId, Long subjectId);
    void deleteByCaseCategoryIdAndSubjectId(Long caseCategoryId, Long subjectId);
}

