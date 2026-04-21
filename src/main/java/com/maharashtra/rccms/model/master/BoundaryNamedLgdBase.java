package com.maharashtra.rccms.model.master;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Common column mapping for geographic boundary masters only (state through village).
 * <p>
 * This is <strong>not</strong> an {@link jakarta.persistence.Entity} — it does not map to
 * its own table. Concrete boundary entities extend this and each maps to one table.
 * </p>
 */
@MappedSuperclass
public abstract class BoundaryNamedLgdBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "local_name", length = 512)
    private String localName;

    /**
     * Local Government Directory (or compatible) code for import / integration.
     */
    @Column(name = "lgd_code", length = 64)
    private String lgdCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getLgdCode() {
        return lgdCode;
    }

    public void setLgdCode(String lgdCode) {
        this.lgdCode = lgdCode;
    }
}
