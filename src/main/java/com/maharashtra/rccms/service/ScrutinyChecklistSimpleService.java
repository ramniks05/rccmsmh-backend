package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.ScrutinyChecklistItemSimpleRequest;
import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.model.master.ScrutinyChecklistItem;
import com.maharashtra.rccms.model.master.Subject;
import com.maharashtra.rccms.repository.CaseCategoryRepository;
import com.maharashtra.rccms.repository.ScrutinyChecklistItemRepository;
import com.maharashtra.rccms.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@SuppressWarnings("null")
public class ScrutinyChecklistSimpleService {

    private final ScrutinyChecklistItemRepository itemRepository;
    private final CaseCategoryRepository caseCategoryRepository;
    private final SubjectRepository subjectRepository;

    public ScrutinyChecklistSimpleService(
            ScrutinyChecklistItemRepository itemRepository,
            CaseCategoryRepository caseCategoryRepository,
            SubjectRepository subjectRepository
    ) {
        this.itemRepository = itemRepository;
        this.caseCategoryRepository = caseCategoryRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public void replace(Long caseCategoryId, Long subjectId, List<ScrutinyChecklistItemSimpleRequest> items) {
        if (caseCategoryId == null) throw new IllegalArgumentException("caseCategoryId is required");
        if (subjectId == null) throw new IllegalArgumentException("subjectId is required");
        if (items == null) throw new IllegalArgumentException("items is required");

        CaseCategory caseCategory = caseCategoryRepository.findById(caseCategoryId).orElse(null);
        if (caseCategory == null) throw new IllegalArgumentException("Invalid caseCategoryId");

        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject == null) throw new IllegalArgumentException("Invalid subjectId");

        Set<Integer> seenOrders = new HashSet<>();
        for (ScrutinyChecklistItemSimpleRequest item : items) {
            if (item == null) throw new IllegalArgumentException("items contains null");
            if (item.getQuestionText() == null || item.getQuestionText().trim().isEmpty()) {
                throw new IllegalArgumentException("questionText is required");
            }
            if (item.getDisplayOrder() == null) throw new IllegalArgumentException("displayOrder is required");
            if (!seenOrders.add(item.getDisplayOrder())) {
                throw new IllegalArgumentException("Duplicate displayOrder in items: " + item.getDisplayOrder());
            }
        }

        itemRepository.deleteByCaseCategoryIdAndSubjectId(caseCategoryId, subjectId);

        for (ScrutinyChecklistItemSimpleRequest item : items) {
            ScrutinyChecklistItem entity = new ScrutinyChecklistItem();
            entity.setCaseCategory(caseCategory);
            entity.setSubject(subject);
            entity.setQuestionText(item.getQuestionText().trim());
            entity.setQuestionTextLocal(item.getQuestionTextLocal());
            entity.setDisplayOrder(item.getDisplayOrder());
            entity.setActive(item.getActive() == null || item.getActive());
            itemRepository.save(entity);
        }
    }
}

