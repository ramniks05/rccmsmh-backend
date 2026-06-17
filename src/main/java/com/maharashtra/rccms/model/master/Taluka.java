package com.maharashtra.rccms.model.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Taluka (tehsil) under a district.
 */
@Entity
@Table(name = "master_taluka")
public class Taluka extends BoundaryNamedLgdBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    /** Denormalized parent district LGD code for import / integration lookups. */
    @Column(name = "district_lgd_code", length = 64)
    private String districtLgdCode;

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public String getDistrictLgdCode() {
        return districtLgdCode;
    }

    public void setDistrictLgdCode(String districtLgdCode) {
        this.districtLgdCode = districtLgdCode;
    }
}
