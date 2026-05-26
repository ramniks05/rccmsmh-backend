package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.service.ApplicationDocumentChecklistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/document-types")
@SuppressWarnings("null")
public class DocumentTypeQueryController {

    private final ApplicationDocumentChecklistService documentChecklistService;

    public DocumentTypeQueryController(ApplicationDocumentChecklistService documentChecklistService) {
        this.documentChecklistService = documentChecklistService;
    }

    /**
     * Filing requirements for a CaseCategory + Subject (includes required flag and display order).
     */
    @GetMapping("/by-case-category-subject")
    public ResponseEntity<?> byCaseCategoryAndSubject(
            @RequestParam("caseCategoryId") Long caseCategoryId,
            @RequestParam("subjectId") Long subjectId
    ) {
        try {
            return ResponseEntity.ok(documentChecklistService.listFilingRequirements(caseCategoryId, subjectId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
