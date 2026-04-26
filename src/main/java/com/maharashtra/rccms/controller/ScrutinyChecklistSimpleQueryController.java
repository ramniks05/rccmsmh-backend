package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.ScrutinyChecklistItemSimpleResponse;
import com.maharashtra.rccms.model.master.ScrutinyChecklistItem;
import com.maharashtra.rccms.repository.ScrutinyChecklistItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scrutiny-checklists")
@SuppressWarnings("null")
public class ScrutinyChecklistSimpleQueryController {

    private final ScrutinyChecklistItemRepository itemRepository;

    public ScrutinyChecklistSimpleQueryController(ScrutinyChecklistItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/by-case-category-subject")
    public ResponseEntity<?> byCaseCategoryAndSubject(
            @RequestParam("caseCategoryId") Long caseCategoryId,
            @RequestParam("subjectId") Long subjectId
    ) {
        List<ScrutinyChecklistItemSimpleResponse> items = itemRepository
                .findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(caseCategoryId, subjectId)
                .stream()
                .filter(ScrutinyChecklistItem::isActive)
                .map(ScrutinyChecklistSimpleQueryController::toResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    private static ScrutinyChecklistItemSimpleResponse toResponse(ScrutinyChecklistItem item) {
        Long caseCategoryId = item.getCaseCategory() == null ? null : item.getCaseCategory().getId();
        Long subjectId = item.getSubject() == null ? null : item.getSubject().getId();
        return new ScrutinyChecklistItemSimpleResponse(
                item.getId(),
                caseCategoryId,
                subjectId,
                item.getQuestionText(),
                item.getQuestionTextLocal(),
                item.getDisplayOrder(),
                item.isActive()
        );
    }
}

