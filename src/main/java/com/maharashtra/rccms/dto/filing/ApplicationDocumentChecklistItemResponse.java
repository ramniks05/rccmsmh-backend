package com.maharashtra.rccms.dto.filing;

import com.maharashtra.rccms.dto.DocumentTypeResponse;

import java.time.Instant;

public class ApplicationDocumentChecklistItemResponse {
    private Long checklistId;
    private Long documentTypeId;
    private DocumentTypeResponse documentType;
    private boolean required;
    private int displayOrder;
    private boolean uploaded;
    private Long attachmentId;
    private String fileName;
    private String storageKey;
    private String mimeType;
    private Instant uploadedAt;
    private boolean clerkVerified;
    private String clerkVerifiedByLoginId;
    private Instant clerkVerifiedAt;
    private String clerkRemarks;

    public Long getChecklistId() {
        return checklistId;
    }

    public void setChecklistId(Long checklistId) {
        this.checklistId = checklistId;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public DocumentTypeResponse getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentTypeResponse documentType) {
        this.documentType = documentType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public boolean isClerkVerified() {
        return clerkVerified;
    }

    public void setClerkVerified(boolean clerkVerified) {
        this.clerkVerified = clerkVerified;
    }

    public String getClerkVerifiedByLoginId() {
        return clerkVerifiedByLoginId;
    }

    public void setClerkVerifiedByLoginId(String clerkVerifiedByLoginId) {
        this.clerkVerifiedByLoginId = clerkVerifiedByLoginId;
    }

    public Instant getClerkVerifiedAt() {
        return clerkVerifiedAt;
    }

    public void setClerkVerifiedAt(Instant clerkVerifiedAt) {
        this.clerkVerifiedAt = clerkVerifiedAt;
    }

    public String getClerkRemarks() {
        return clerkRemarks;
    }

    public void setClerkRemarks(String clerkRemarks) {
        this.clerkRemarks = clerkRemarks;
    }
}
