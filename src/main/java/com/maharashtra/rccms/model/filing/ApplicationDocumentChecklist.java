package com.maharashtra.rccms.model.filing;

import com.maharashtra.rccms.model.master.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "application_document_checklist",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_app_doc_checklist_app_doc_type",
                columnNames = {"application_id", "document_type_id"}
        )
)
@SuppressWarnings("null")
public class ApplicationDocumentChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private FilingApplication application;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    @Column(name = "is_required", nullable = false)
    private boolean required;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id")
    private ApplicationAttachment attachment;

    @Column(name = "clerk_verified", nullable = false)
    private boolean clerkVerified;

    @Column(name = "clerk_verified_by_login_id", length = 150)
    private String clerkVerifiedByLoginId;

    @Column(name = "clerk_verified_at")
    private Instant clerkVerifiedAt;

    @Column(name = "clerk_remarks", length = 2000)
    private String clerkRemarks;

    public Long getId() {
        return id;
    }

    public FilingApplication getApplication() {
        return application;
    }

    public void setApplication(FilingApplication application) {
        this.application = application;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
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

    public ApplicationAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(ApplicationAttachment attachment) {
        this.attachment = attachment;
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
