package com.maharashtra.rccms.model.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Master list of supporting / identity document kinds (e.g. Aadhaar, PAN, passport).
 * Flags control whether the document type is acceptable as photo ID or as address proof.
 * {@link #sourceUrl} points to the official portal used when a submission is disputed.
 */
@Entity
@Table(name = "master_document_type")
public class DocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String code;

    @Column(nullable = false, length = 512)
    private String name;

    @Column(name = "local_name", length = 1024)
    private String localName;

    @Column(name = "valid_for_photo_id", nullable = false)
    private boolean validForPhotoId;

    @Column(name = "valid_for_address", nullable = false)
    private boolean validForAddress;

    @Column(name = "source_url", length = 2048)
    private String sourceUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public boolean isValidForPhotoId() {
        return validForPhotoId;
    }

    public void setValidForPhotoId(boolean validForPhotoId) {
        this.validForPhotoId = validForPhotoId;
    }

    public boolean isValidForAddress() {
        return validForAddress;
    }

    public void setValidForAddress(boolean validForAddress) {
        this.validForAddress = validForAddress;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
