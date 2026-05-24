package com.maharashtra.rccms.service;

import com.maharashtra.rccms.dto.DocumentTypeFilingResponse;
import com.maharashtra.rccms.dto.DocumentTypeResponse;
import com.maharashtra.rccms.dto.filing.ApplicationDocumentChecklistEntryRequest;
import com.maharashtra.rccms.dto.filing.ApplicationDocumentChecklistItemResponse;
import com.maharashtra.rccms.dto.filing.ApplicationDocumentChecklistResponse;
import com.maharashtra.rccms.dto.filing.ApplicationDocumentChecklistSaveRequest;
import com.maharashtra.rccms.model.filing.ApplicationAttachment;
import com.maharashtra.rccms.model.filing.ApplicationDocumentChecklist;
import com.maharashtra.rccms.model.filing.FilingApplication;
import com.maharashtra.rccms.model.master.CaseCategory;
import com.maharashtra.rccms.model.master.DocumentType;
import com.maharashtra.rccms.model.master.DocumentTypeMapping;
import com.maharashtra.rccms.model.master.Subject;
import com.maharashtra.rccms.repository.ApplicationDocumentChecklistRepository;
import com.maharashtra.rccms.repository.DocumentTypeMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@SuppressWarnings("null")
public class ApplicationDocumentChecklistService {

    private final ApplicationDocumentChecklistRepository checklistRepository;
    private final DocumentTypeMappingRepository mappingRepository;

    public ApplicationDocumentChecklistService(
            ApplicationDocumentChecklistRepository checklistRepository,
            DocumentTypeMappingRepository mappingRepository
    ) {
        this.checklistRepository = checklistRepository;
        this.mappingRepository = mappingRepository;
    }

    @Transactional(readOnly = true)
    public List<DocumentTypeFilingResponse> listFilingRequirements(Long caseCategoryId, Long subjectId) {
        if (caseCategoryId == null) {
            throw new IllegalArgumentException("caseCategoryId is required.");
        }
        if (subjectId == null) {
            throw new IllegalArgumentException("subjectId is required.");
        }
        return mappingRepository.findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(caseCategoryId, subjectId).stream()
                .map(ApplicationDocumentChecklistService::toFilingRequirement)
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional
    public void syncChecklistFromApplication(FilingApplication application) {
        if (application == null || application.getId() == null) {
            return;
        }
        CaseCategory category = application.getCaseCategory();
        Subject subject = application.getSubject();
        if (category == null || category.getId() == null || subject == null || subject.getId() == null) {
            checklistRepository.deleteByApplicationId(application.getId());
            return;
        }

        List<DocumentTypeMapping> mappings = mappingRepository.findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(
                category.getId(),
                subject.getId()
        );
        Map<Long, ApplicationAttachment> attachmentByDocType = indexAttachmentsByDocumentType(application.getAttachments());
        Map<Long, ApplicationDocumentChecklist> existingByDocType = loadExistingByDocumentType(application.getId());

        Set<Long> activeDocTypeIds = new HashSet<>();
        for (DocumentTypeMapping mapping : mappings) {
            DocumentType documentType = mapping.getDocumentType();
            if (documentType == null || documentType.getId() == null) {
                continue;
            }
            activeDocTypeIds.add(documentType.getId());
            ApplicationDocumentChecklist row = existingByDocType.get(documentType.getId());
            if (row == null) {
                row = new ApplicationDocumentChecklist();
                row.setApplication(application);
                row.setDocumentType(documentType);
            }
            row.setRequired(mapping.isRequired());
            row.setDisplayOrder(mapping.getDisplayOrder());
            ApplicationAttachment attachment = attachmentByDocType.get(documentType.getId());
            row.setAttachment(attachment);
            if (attachment == null) {
                row.setClerkVerified(false);
                row.setClerkVerifiedByLoginId(null);
                row.setClerkVerifiedAt(null);
            }
            checklistRepository.save(row);
        }

        for (Map.Entry<Long, ApplicationDocumentChecklist> entry : existingByDocType.entrySet()) {
            if (!activeDocTypeIds.contains(entry.getKey())) {
                checklistRepository.delete(entry.getValue());
            }
        }
    }

    @Transactional(readOnly = true)
    public ApplicationDocumentChecklistResponse getChecklist(FilingApplication application) {
        if (application == null || application.getId() == null) {
            throw new IllegalArgumentException("applicationId is required.");
        }
        return buildChecklistResponse(application);
    }

    @Transactional
    public ApplicationDocumentChecklistResponse saveClerkVerification(
            FilingApplication application,
            ApplicationDocumentChecklistSaveRequest request,
            String clerkLogin
    ) {
        if (application == null || application.getId() == null) {
            throw new IllegalArgumentException("applicationId is required.");
        }
        if (request == null || request.getEntries() == null || request.getEntries().isEmpty()) {
            throw new IllegalArgumentException("At least one checklist entry is required.");
        }

        syncChecklistFromApplication(application);
        Map<Long, ApplicationDocumentChecklist> byDocType = loadExistingByDocumentType(application.getId());

        for (ApplicationDocumentChecklistEntryRequest entry : request.getEntries()) {
            if (entry.getDocumentTypeId() == null) {
                throw new IllegalArgumentException("documentTypeId is required for each entry.");
            }
            if (entry.getClerkVerified() == null) {
                throw new IllegalArgumentException("clerkVerified is required (true or false) for each entry.");
            }
            ApplicationDocumentChecklist row = byDocType.get(entry.getDocumentTypeId());
            if (row == null) {
                throw new IllegalArgumentException("Unknown documentTypeId for this application: " + entry.getDocumentTypeId());
            }
            if (Boolean.TRUE.equals(entry.getClerkVerified()) && row.getAttachment() == null) {
                DocumentType dt = row.getDocumentType();
                String label = dt != null ? dt.getName() : String.valueOf(entry.getDocumentTypeId());
                throw new IllegalArgumentException("Cannot verify document not uploaded by applicant: " + label);
            }
            row.setClerkVerified(entry.getClerkVerified());
            if (Boolean.TRUE.equals(entry.getClerkVerified())) {
                row.setClerkVerifiedByLoginId(clerkLogin);
                row.setClerkVerifiedAt(Instant.now());
            } else {
                row.setClerkVerifiedByLoginId(null);
                row.setClerkVerifiedAt(null);
            }
            row.setClerkRemarks(trimToNull(entry.getClerkRemarks()));
            checklistRepository.save(row);
        }

        return buildChecklistResponse(application);
    }

    public void validateRequiredUploadsOnSubmit(FilingApplication application) {
        List<DocumentTypeMapping> mappings = resolveMappings(application);
        if (mappings.isEmpty()) {
            return;
        }
        Map<Long, ApplicationAttachment> attachmentByDocType = indexAttachmentsByDocumentType(application.getAttachments());
        List<String> missing = new ArrayList<>();
        for (DocumentTypeMapping mapping : mappings) {
            if (!mapping.isRequired()) {
                continue;
            }
            DocumentType dt = mapping.getDocumentType();
            if (dt == null || dt.getId() == null) {
                continue;
            }
            if (!attachmentByDocType.containsKey(dt.getId())) {
                missing.add(dt.getName() != null ? dt.getName() : dt.getCode());
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Required documents missing for submission: " + String.join(", ", missing)
            );
        }
    }

    public void validateMappedAttachmentDocumentTypes(FilingApplication application) {
        List<DocumentTypeMapping> mappings = resolveMappings(application);
        if (mappings.isEmpty()) {
            return;
        }
        Set<Long> allowedDocTypeIds = new HashSet<>();
        for (DocumentTypeMapping mapping : mappings) {
            if (mapping.getDocumentType() != null && mapping.getDocumentType().getId() != null) {
                allowedDocTypeIds.add(mapping.getDocumentType().getId());
            }
        }
        Set<Long> seenDocTypeIds = new HashSet<>();
        if (application.getAttachments() != null) {
            for (ApplicationAttachment attachment : application.getAttachments()) {
                if (attachment.getDocumentType() == null || attachment.getDocumentType().getId() == null) {
                    continue;
                }
                Long docTypeId = attachment.getDocumentType().getId();
                if (!allowedDocTypeIds.contains(docTypeId)) {
                    throw new IllegalArgumentException("Attachment documentTypeId is not configured for this case category and subject.");
                }
                if (!seenDocTypeIds.add(docTypeId)) {
                    throw new IllegalArgumentException("Duplicate upload for document type id: " + docTypeId);
                }
            }
        }
    }

    public void assertClerkVerificationCompleteForForward(FilingApplication application) {
        ApplicationDocumentChecklistResponse view = buildChecklistResponse(application);
        if (!view.isDocumentsConfigured()) {
            return;
        }
        if (!view.isAllRequiredUploaded()) {
            throw new IllegalArgumentException("All required documents must be uploaded before forwarding to PO.");
        }
        if (!view.isAllRequiredClerkVerified()) {
            throw new IllegalArgumentException(
                    "Verify all required uploaded documents before forwarding to PO."
            );
        }
    }

    private ApplicationDocumentChecklistResponse buildChecklistResponse(FilingApplication application) {
        List<ApplicationDocumentChecklist> rows = checklistRepository.findByApplicationIdOrderByDisplayOrderAscIdAsc(application.getId());
        ApplicationDocumentChecklistResponse out = new ApplicationDocumentChecklistResponse();
        out.setApplicationId(application.getId());
        out.setCaseCategoryId(application.getCaseCategory() != null ? application.getCaseCategory().getId() : null);
        out.setSubjectId(application.getSubject() != null ? application.getSubject().getId() : null);
        out.setDocumentsConfigured(!rows.isEmpty());

        boolean allRequiredUploaded = true;
        boolean allRequiredClerkVerified = true;

        for (ApplicationDocumentChecklist row : rows) {
            ApplicationDocumentChecklistItemResponse item = toItemResponse(row);
            out.getItems().add(item);
            if (row.isRequired()) {
                if (!item.isUploaded()) {
                    allRequiredUploaded = false;
                }
                if (!item.isClerkVerified()) {
                    allRequiredClerkVerified = false;
                }
            }
        }

        if (!out.isDocumentsConfigured()) {
            allRequiredUploaded = true;
            allRequiredClerkVerified = true;
        }

        out.setAllRequiredUploaded(allRequiredUploaded);
        out.setAllRequiredClerkVerified(allRequiredClerkVerified);
        return out;
    }

    private List<DocumentTypeMapping> resolveMappings(FilingApplication application) {
        CaseCategory category = application.getCaseCategory();
        Subject subject = application.getSubject();
        if (category == null || category.getId() == null || subject == null || subject.getId() == null) {
            return List.of();
        }
        return mappingRepository.findByCaseCategoryIdAndSubjectIdOrderByDisplayOrderAsc(category.getId(), subject.getId());
    }

    private Map<Long, ApplicationDocumentChecklist> loadExistingByDocumentType(Long applicationId) {
        Map<Long, ApplicationDocumentChecklist> out = new HashMap<>();
        for (ApplicationDocumentChecklist row : checklistRepository.findByApplicationIdOrderByDisplayOrderAscIdAsc(applicationId)) {
            if (row.getDocumentType() != null && row.getDocumentType().getId() != null) {
                out.put(row.getDocumentType().getId(), row);
            }
        }
        return out;
    }

    private static Map<Long, ApplicationAttachment> indexAttachmentsByDocumentType(List<ApplicationAttachment> attachments) {
        Map<Long, ApplicationAttachment> out = new HashMap<>();
        if (attachments == null) {
            return out;
        }
        for (ApplicationAttachment attachment : attachments) {
            if (attachment.getDocumentType() != null && attachment.getDocumentType().getId() != null) {
                out.put(attachment.getDocumentType().getId(), attachment);
            }
        }
        return out;
    }

    private static ApplicationDocumentChecklistItemResponse toItemResponse(ApplicationDocumentChecklist row) {
        ApplicationDocumentChecklistItemResponse item = new ApplicationDocumentChecklistItemResponse();
        item.setChecklistId(row.getId());
        DocumentType dt = row.getDocumentType();
        if (dt != null) {
            item.setDocumentTypeId(dt.getId());
            item.setDocumentType(toDocumentTypeResponse(dt));
        }
        item.setRequired(row.isRequired());
        item.setDisplayOrder(row.getDisplayOrder());
        ApplicationAttachment attachment = row.getAttachment();
        item.setUploaded(attachment != null);
        if (attachment != null) {
            item.setAttachmentId(attachment.getId());
            item.setFileName(attachment.getFileName());
            item.setStorageKey(attachment.getStorageKey());
            item.setMimeType(attachment.getMimeType());
            item.setUploadedAt(attachment.getUploadedAt());
        }
        item.setClerkVerified(row.isClerkVerified());
        item.setClerkVerifiedByLoginId(row.getClerkVerifiedByLoginId());
        item.setClerkVerifiedAt(row.getClerkVerifiedAt());
        item.setClerkRemarks(row.getClerkRemarks());
        return item;
    }

    private static DocumentTypeResponse toDocumentTypeResponse(DocumentType dt) {
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

    private static DocumentTypeFilingResponse toFilingRequirement(DocumentTypeMapping mapping) {
        DocumentType dt = mapping.getDocumentType();
        if (dt == null) {
            return null;
        }
        return new DocumentTypeFilingResponse(
                dt.getId(),
                dt.getCode(),
                dt.getName(),
                dt.getLocalName(),
                dt.isValidForPhotoId(),
                dt.isValidForAddress(),
                dt.getSourceUrl(),
                mapping.isRequired(),
                mapping.getDisplayOrder()
        );
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
