package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.DocumentTypeMappingConfiguredSubjectResponse;
import com.maharashtra.rccms.dto.DocumentTypeMappingItemRequest;
import com.maharashtra.rccms.dto.DocumentTypeMappingItemResponse;
import com.maharashtra.rccms.dto.DocumentTypeMappingListResponse;
import com.maharashtra.rccms.dto.DocumentTypeResponse;
import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.model.master.DocumentType;
import com.maharashtra.rccms.model.master.DocumentTypeMapping;
import com.maharashtra.rccms.model.master.Subject;
import com.maharashtra.rccms.repository.CaseCategoryRepository;
import com.maharashtra.rccms.repository.DocumentTypeMappingRepository;
import com.maharashtra.rccms.repository.DocumentTypeRepository;
import com.maharashtra.rccms.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@SuppressWarnings("null")
public class DocumentTypeMappingService {

    private final DocumentTypeMappingRepository mappingRepository;
    private final CaseCategoryRepository caseCategoryRepository;
    private final SubjectRepository subjectRepository;
    private final DocumentTypeRepository documentTypeRepository;

    public DocumentTypeMappingService(
            DocumentTypeMappingRepository mappingRepository,
            CaseCategoryRepository caseCategoryRepository,
            SubjectRepository subjectRepository,
            DocumentTypeRepository documentTypeRepository
    ) {
        this.mappingRepository = mappingRepository;
        this.caseCategoryRepository = caseCategoryRepository;
        this.subjectRepository = subjectRepository;
        this.documentTypeRepository = documentTypeRepository;
    }

    @Transactional
    public void replaceMappings(Long caseCategoryId, Long subjectId, List<DocumentTypeMappingItemRequest> items) {
        if (caseCategoryId == null) throw new IllegalArgumentException("caseCategoryId is required");
        if (subjectId == null) throw new IllegalArgumentException("subjectId is required");
        if (items == null) throw new IllegalArgumentException("items is required");

        CaseCategory caseCategory = caseCategoryRepository.findById(caseCategoryId).orElse(null);
        if (caseCategory == null) throw new IllegalArgumentException("Invalid caseCategoryId");

        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject == null) throw new IllegalArgumentException("Invalid subjectId");

        Set<Long> seenDocTypeIds = new HashSet<>();
        for (DocumentTypeMappingItemRequest item : items) {
            if (item == null) throw new IllegalArgumentException("items contains null");
            if (item.getDocumentTypeId() == null) throw new IllegalArgumentException("documentTypeId is required");
            if (!seenDocTypeIds.add(item.getDocumentTypeId())) {
                throw new IllegalArgumentException("Duplicate documentTypeId in items: " + item.getDocumentTypeId());
            }
        }

        mappingRepository.deleteByCaseCategoryIdAndSubjectId(caseCategoryId, subjectId);

        for (DocumentTypeMappingItemRequest item : items) {
            DocumentType documentType = documentTypeRepository.findById(item.getDocumentTypeId()).orElse(null);
            if (documentType == null) throw new IllegalArgumentException("Invalid documentTypeId: " + item.getDocumentTypeId());

            DocumentTypeMapping mapping = new DocumentTypeMapping();
            mapping.setCaseCategory(caseCategory);
            mapping.setSubject(subject);
            mapping.setDocumentType(documentType);
            mapping.setRequired(item.getRequired() != null && item.getRequired());
            mapping.setDisplayOrder(item.getDisplayOrder() == null ? 0 : item.getDisplayOrder());

            mappingRepository.save(mapping);
        }
    }

    @Transactional(readOnly = true)
    public DocumentTypeMappingListResponse getMappings(Long caseCategoryId, Long subjectId) {
        if (caseCategoryId == null) throw new IllegalArgumentException("caseCategoryId is required");
        if (subjectId == null) throw new IllegalArgumentException("subjectId is required");

        CaseCategory caseCategory = caseCategoryRepository.findById(caseCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid caseCategoryId"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subjectId"));

        DocumentTypeMappingListResponse out = new DocumentTypeMappingListResponse();
        out.setCaseCategoryId(caseCategory.getId());
        out.setCaseCategoryCode(caseCategory.getCode());
        out.setCaseCategoryName(caseCategory.getName());
        out.setSubjectId(subject.getId());
        out.setSubjectCode(subject.getSubjectCode());
        out.setSubjectName(subject.getSubjectName());
        out.setItems(mappingRepository.findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(caseCategoryId, subjectId).stream()
                .map(DocumentTypeMappingService::toItemResponse)
                .toList());
        return out;
    }

    @Transactional(readOnly = true)
    public List<DocumentTypeMappingConfiguredSubjectResponse> listConfiguredSubjects(Long caseCategoryId) {
        if (caseCategoryId == null) throw new IllegalArgumentException("caseCategoryId is required");
        if (!caseCategoryRepository.existsById(caseCategoryId)) {
            throw new IllegalArgumentException("Invalid caseCategoryId");
        }

        Map<Long, DocumentTypeMappingConfiguredSubjectResponse> bySubject = new HashMap<>();
        for (DocumentTypeMapping mapping : mappingRepository.findByCaseCategoryIdOrderBySubjectIdAscDisplayOrderAsc(caseCategoryId)) {
            Subject subject = mapping.getSubject();
            if (subject == null || subject.getId() == null) {
                continue;
            }
            DocumentTypeMappingConfiguredSubjectResponse row = bySubject.computeIfAbsent(subject.getId(), id -> {
                DocumentTypeMappingConfiguredSubjectResponse created = new DocumentTypeMappingConfiguredSubjectResponse();
                created.setSubjectId(subject.getId());
                created.setSubjectCode(subject.getSubjectCode());
                created.setSubjectName(subject.getSubjectName());
                return created;
            });
            row.setMappedDocumentCount(row.getMappedDocumentCount() + 1);
            if (mapping.isRequired()) {
                row.setRequiredDocumentCount(row.getRequiredDocumentCount() + 1);
            }
        }

        return bySubject.values().stream()
                .sorted(Comparator.comparing(DocumentTypeMappingConfiguredSubjectResponse::getSubjectName,
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    static DocumentTypeMappingItemResponse toItemResponse(DocumentTypeMapping mapping) {
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
        return new DocumentTypeMappingItemResponse(
                mapping.getId(),
                documentTypeId,
                mapping.isRequired(),
                mapping.getDisplayOrder(),
                dto
        );
    }
}

