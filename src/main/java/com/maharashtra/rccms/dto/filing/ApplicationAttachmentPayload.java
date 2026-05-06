package com.maharashtra.rccms.dto.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationAttachmentPayload {

    private String kind;
    private String storageKey;
    private String fileName;
    private String mimeType;
    private Instant uploadedAt;
    private String uploadedByLoginId;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getUploadedByLoginId() {
        return uploadedByLoginId;
    }

    public void setUploadedByLoginId(String uploadedByLoginId) {
        this.uploadedByLoginId = uploadedByLoginId;
    }
}
