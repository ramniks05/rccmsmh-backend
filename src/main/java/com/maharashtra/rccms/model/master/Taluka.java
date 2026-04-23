package com.maharashtra.rccms.model.master;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Taluka (tehsil / sub-district) under a district.
 */
@Entity
@Table(name = "master_taluka")
public class Taluka extends BoundaryNamedLgdBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    /**
     * Optional at DB level for backward compatibility with existing rows.
     * API enforces providing subdistrictId for new taluka creation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subdistrict_id")
    private Subdistrict subdistrict;

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Subdistrict getSubdistrict() {
        return subdistrict;
    }

    public void setSubdistrict(Subdistrict subdistrict) {
        this.subdistrict = subdistrict;
    }
}
