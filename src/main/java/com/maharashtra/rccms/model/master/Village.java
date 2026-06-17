package com.maharashtra.rccms.model.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "master_village")
public class Village extends BoundaryNamedLgdBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "taluka_id", nullable = false)
    private Taluka taluka;

    /** Denormalized parent taluka LGD code for import / integration lookups. */
    @Column(name = "taluka_lgd_code", length = 64)
    private String talukaLgdCode;

    public Taluka getTaluka() {
        return taluka;
    }

    public void setTaluka(Taluka taluka) {
        this.taluka = taluka;
    }

    public String getTalukaLgdCode() {
        return talukaLgdCode;
    }

    public void setTalukaLgdCode(String talukaLgdCode) {
        this.talukaLgdCode = talukaLgdCode;
    }
}
