package com.maharashtra.rccms.model.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * District under state. Linked to division by {@link #divisionCode} (not a foreign key).
 */
@Entity
@Table(name = "master_district")
public class District extends BoundaryNamedLgdBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    /** Revenue division code (matches {@link Division#getDivisionCode()} for the same state). */
    @Column(name = "division_code", length = 64)
    private String divisionCode;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }
}
