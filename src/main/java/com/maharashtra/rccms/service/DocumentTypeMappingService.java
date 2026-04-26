package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.DocumentTypeMappingItemRequest;
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

import java.util.HashSet;
import java.util.List;
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
}

