package com.maharashtra.rccms.model.master;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Boundary master with LGD code (state, district, subdistrict, taluka, village).
 * {@link Division} does not use LGD codes and extends {@link BoundaryNamedBase} instead.
 */
@MappedSuperclass
public abstract class BoundaryNamedLgdBase extends BoundaryNamedBase {

    /**
     * Local Government Directory (or compatible) code for import / integration.
     */
    @Column(name = "lgd_code", length = 64)
    private String lgdCode;

    public String getLgdCode() {
        return lgdCode;
    }

    public void setLgdCode(String lgdCode) {
        this.lgdCode = lgdCode;
    }
}
