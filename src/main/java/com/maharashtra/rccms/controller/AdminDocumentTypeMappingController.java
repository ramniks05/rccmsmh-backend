package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.DocumentTypeMappingConfiguredSubjectResponse;
import com.maharashtra.rccms.dto.DocumentTypeMappingListResponse;
import com.maharashtra.rccms.dto.DocumentTypeMappingReplaceRequest;
import com.maharashtra.rccms.service.DocumentTypeMappingService;
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
@RequestMapping("/api/admin/document-type-mappings")
@SuppressWarnings("null")
public class AdminDocumentTypeMappingController {

    private final DocumentTypeMappingService mappingService;

    public AdminDocumentTypeMappingController(DocumentTypeMappingService mappingService) {
        this.mappingService = mappingService;
    }

    /**
     * List mapping items for a CaseCategory + Subject combination.
     */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam("caseCategoryId") Long caseCategoryId,
            @RequestParam("subjectId") Long subjectId
    ) {
        try {
            DocumentTypeMappingListResponse result = mappingService.getMappings(caseCategoryId, subjectId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Summary of subjects that already have document mappings for a case category.
     */
    @GetMapping("/configured-subjects")
    public ResponseEntity<?> listConfiguredSubjects(@RequestParam("caseCategoryId") Long caseCategoryId) {
        try {
            List<DocumentTypeMappingConfiguredSubjectResponse> items = mappingService.listConfiguredSubjects(caseCategoryId);
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Replace mapping items for a CaseCategory + Subject combination.
     */
    @PutMapping
    public ResponseEntity<?> replace(@RequestBody DocumentTypeMappingReplaceRequest request) {
        try {
            mappingService.replaceMappings(request.getCaseCategoryId(), request.getSubjectId(), request.getItems());
            DocumentTypeMappingListResponse saved = mappingService.getMappings(
                    request.getCaseCategoryId(),
                    request.getSubjectId()
            );
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
