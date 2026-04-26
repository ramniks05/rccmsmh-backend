package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.CaseCategoryResponse;
import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.model.master.OfficeType;
import com.maharashtra.rccms.repository.CaseCategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/case-categories")
@SuppressWarnings("null")
public class CaseCategoryController {

    private final CaseCategoryRepository caseCategoryRepository;

    public CaseCategoryController(CaseCategoryRepository caseCategoryRepository) {
        this.caseCategoryRepository = caseCategoryRepository;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        List<CaseCategoryResponse> items = caseCategoryRepository.findAll().stream()
                .map(CaseCategoryController::toResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        CaseCategory category = caseCategoryRepository.findById(id).orElse(null);
        if (category == null) return ResponseEntity.badRequest().body(Map.of("error", "Invalid caseCategoryId"));
        return ResponseEntity.ok(toResponse(category));
    }

    private static CaseCategoryResponse toResponse(CaseCategory c) {
        OfficeType hearingOfficeType = c.getHearingOfficeType();
        Long hearingOfficeTypeId = hearingOfficeType == null ? null : hearingOfficeType.getId();
        String hearingOfficeTypeName = hearingOfficeType == null ? null : hearingOfficeType.getName();
        String hearingOfficeTypeLocalName = hearingOfficeType == null ? null : hearingOfficeType.getLocalName();
        CaseCategory next = c.getNextCategory();
        Long nextId = next == null ? null : next.getId();
        String nextCode = next == null ? null : next.getCode();
        String nextName = next == null ? null : next.getName();

        return new CaseCategoryResponse(
                c.getId(),
                c.getCode(),
                c.getName(),
                c.getLocalName(),
                c.getSequenceNo(),
                hearingOfficeTypeId,
                hearingOfficeTypeName,
                hearingOfficeTypeLocalName,
                nextId,
                nextCode,
                nextName
        );
    }
}

