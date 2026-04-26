package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.DocumentTypeMappingItemResponse;
import com.maharashtra.rccms.dto.DocumentTypeMappingReplaceRequest;
import com.maharashtra.rccms.dto.DocumentTypeResponse;
import com.maharashtra.rccms.model.master.DocumentType;
import com.maharashtra.rccms.model.master.DocumentTypeMapping;
import com.maharashtra.rccms.repository.DocumentTypeMappingRepository;
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

    private final DocumentTypeMappingRepository mappingRepository;
    private final DocumentTypeMappingService mappingService;

    public AdminDocumentTypeMappingController(
            DocumentTypeMappingRepository mappingRepository,
            DocumentTypeMappingService mappingService
    ) {
        this.mappingRepository = mappingRepository;
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
        List<DocumentTypeMappingItemResponse> items = mappingRepository
                .findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(caseCategoryId, subjectId)
                .stream()
                .map(AdminDocumentTypeMappingController::toItemResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    /**
     * Replace mapping items for a CaseCategory + Subject combination.
     */
    @PutMapping
    public ResponseEntity<?> replace(@RequestBody DocumentTypeMappingReplaceRequest request) {
        try {
            mappingService.replaceMappings(request.getCaseCategoryId(), request.getSubjectId(), request.getItems());
            return ResponseEntity.ok(Map.of("message", "Mapping saved."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private static DocumentTypeMappingItemResponse toItemResponse(DocumentTypeMapping mapping) {
        DocumentType dt = mapping.getDocumentType();
        DocumentTypeResponse dto = dt == null ? null : new DocumentTypeResponse(
                dt.getId(),
                dt.getCode(),
                dt.getName(),
                dt.getLocalName(),
                dt.isValidForPhotoId(),
                dt.isValidForAddress(),
                dt.getSourceUrl()
        );
        Long documentTypeId = dt == null ? null : dt.getId();
        return new DocumentTypeMappingItemResponse(documentTypeId, mapping.isRequired(), mapping.getDisplayOrder(), dto);
    }
}

