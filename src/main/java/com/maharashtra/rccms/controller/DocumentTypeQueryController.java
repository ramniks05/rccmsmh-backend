package com.maharashtra.rccms.controller;

import com.maharashtra.rccms.dto.DocumentTypeResponse;
import com.maharashtra.rccms.model.master.DocumentType;
import com.maharashtra.rccms.repository.DocumentTypeMappingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/document-types")
@SuppressWarnings("null")
public class DocumentTypeQueryController {

    private final DocumentTypeMappingRepository mappingRepository;

    public DocumentTypeQueryController(DocumentTypeMappingRepository mappingRepository) {
        this.mappingRepository = mappingRepository;
    }

    /**
     * Fetch document types allowed for the given CaseCategory + Subject combination.
     * Used by filing UI to show only relevant documents.
     */
    @GetMapping("/by-case-category-subject")
    public ResponseEntity<?> byCaseCategoryAndSubject(
            @RequestParam("caseCategoryId") Long caseCategoryId,
            @RequestParam("subjectId") Long subjectId
    ) {
        List<DocumentTypeResponse> items = mappingRepository
                .findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(caseCategoryId, subjectId)
                .stream()
                .map(mapping -> toDocTypeResponse(mapping.getDocumentType()))
                .filter(doc -> doc != null)
                .toList();

        return ResponseEntity.ok(items);
    }

    private static DocumentTypeResponse toDocTypeResponse(DocumentType dt) {
        if (dt == null) return null;
        return new DocumentTypeResponse(
                dt.getId(),
                dt.getCode(),
                dt.getName(),
                dt.getLocalName(),
                dt.isValidForPhotoId(),
                dt.isValidForAddress(),
                dt.getSourceUrl()
        );
    }
}

