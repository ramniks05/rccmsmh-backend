package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.ScrutinyChecklistItemSimpleResponse;
import com.maharashtra.rccms.dto.ScrutinyChecklistReplaceSimpleRequest;
import com.maharashtra.rccms.model.master.ScrutinyChecklistItem;
import com.maharashtra.rccms.repository.ScrutinyChecklistItemRepository;
import com.maharashtra.rccms.service.ScrutinyChecklistSimpleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/scrutiny-checklists")
@SuppressWarnings("null")
public class AdminScrutinyChecklistSimpleController {

    private final ScrutinyChecklistItemRepository itemRepository;
    private final ScrutinyChecklistSimpleService simpleService;

    public AdminScrutinyChecklistSimpleController(
            ScrutinyChecklistItemRepository itemRepository,
            ScrutinyChecklistSimpleService simpleService
    ) {
        this.itemRepository = itemRepository;
        this.simpleService = simpleService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam("caseCategoryId") Long caseCategoryId,
            @RequestParam("subjectId") Long subjectId
    ) {
        List<ScrutinyChecklistItemSimpleResponse> items = itemRepository
                .findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(caseCategoryId, subjectId)
                .stream()
                .map(AdminScrutinyChecklistSimpleController::toResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping
    public ResponseEntity<?> replace(@RequestBody ScrutinyChecklistReplaceSimpleRequest request) {
        try {
            simpleService.replace(request.getCaseCategoryId(), request.getSubjectId(), request.getItems());
            return ResponseEntity.ok(Map.of("message", "Scrutiny checklist saved."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
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

