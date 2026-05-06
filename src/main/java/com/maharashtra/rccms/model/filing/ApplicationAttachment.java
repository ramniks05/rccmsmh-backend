package com.maharashtra.rccms.model.filing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "application_attachment")
@SuppressWarnings("null")
public class ApplicationAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private FilingApplication application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 48)
    private ApplicationAttachmentKind kind;

    @Column(name = "storage_key", nullable = false, length = 1024)
    private String storageKey;

    @Column(name = "file_name", nullable = false, length = 512)
    private String fileName;

    @Column(name = "mime_type", length = 128)
    private String mimeType;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt = Instant.now();

    @Column(name = "uploaded_by_login_id", length = 190)
    private String uploadedByLoginId;

    public Long getId() {
        return id;
    }

    public FilingApplication getApplication() {
        return application;
    }

    public void setApplication(FilingApplication application) {
        this.application = application;
    }

    public ApplicationAttachmentKind getKind() {
        return kind;
    }

    public void setKind(ApplicationAttachmentKind kind) {
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
