package com.maharashtra.rccms.model.master;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * District under state. {@link #division} is optional when import uses state → district directly.
 */
@Entity
@Table(name = "master_district")
public class District extends BoundaryNamedLgdBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id")
    private Division division;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }
}
